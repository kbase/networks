package us.kbase.networks.adaptor.JDBCAdaptor;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.configuration.Configuration;

import us.kbase.networks.adaptor.AbstractAdaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.IdGenerator;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mchange.v2.c3p0.*;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class GenericAdaptor extends AbstractAdaptor{


	private class PreparedStatements {
		public Hashtable<String, PreparedStatement> sql2pstmt = new Hashtable<String,PreparedStatement>();

		public PreparedStatements (Dataset ds) throws Exception {
			C3P0ProxyConnection castCon = (C3P0ProxyConnection) cpds.getConnection();
			Set<String> propertyStr = ds.getPropertyNames();
			for( String pn : propertyStr) {
				if (! pn.startsWith(SQL_Statement_Prefix)) continue;
				sql2pstmt.put(pn,castCon.prepareStatement(ds.getProperty(pn)));
			}
		}
	}

	private static final String SQL_Statement_Prefix = "SQL.";
	private static final String SQL_Like_Statement_Prefix = "SLIKE.";
	private static final String PreparedStatement_BIND_Prefix1 = "QIDX1.";
	private static final String PreparedStatement_BIND_Prefix2 = "QIDX2."; // for internal networks only

	private static final String Node1_Type_Mapping_Prefix= "EDGE2NODETYPE1.";
	private static final String Node2_Type_Mapping_Prefix = "EDGE2NODETYPE2.";

	private static final String Resultset_Node1_Index_Prefix  = "RSNode1Index.";
	private static final String Resultset_Node2_Index_Prefix  = "RSNode2Index.";
	private static final String Resultset_Weight_Index_Prefix = "RSWeightIndex.";

	private static final String Safer_Internal_Network_Prefix = "SafeInt.";

	private static final String DefaultEdgeTypes = "Default.EdgeTypes";
	private static final String SupportedEdgeTypes = "Supported.EdgeTypes";

	private Hashtable<String,PreparedStatements> ds2pstmts = new Hashtable<String,PreparedStatements>();
	
	private static final HashMap<String,Node> entityid2Node = new HashMap<String,Node>();

	private ComboPooledDataSource cpds = null;

	private static ObjectMapper m = new ObjectMapper();

	private Node getNode(String id, String name, Entity entity, NodeType nt) {
		switch(nt) {
		case GENE: return Node.buildGeneNode(id, name, entity);
		case PROTEIN: return Node.buildProteinNode(id, name, entity);
		case CLUSTER: return Node.buildClusterNode(id, name, entity);
		}
		return null;
	}

	private Node getNode(Entity e, NodeType nt) {
		
		String key = e.getId()+"."+e.getType()+"."+nt.toString();
		if(!entityid2Node.containsKey(key)){
			entityid2Node.put(key, getNode(IdGenerator.Node.nextId(), e.getId(), e, nt));
			
		}
		return entityid2Node.get(key);
	}
	
	
	public String getCurrentMethodName()
	{
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		return stackTraceElements[1].toString();
	}

	// TODO: be prepared for mysql connections & sql stmt (prepared)
	@Override
	protected List<Dataset> loadDatasets() throws AdaptorException {

		// due to the initialization orders, I need to make sure the following
		if(cpds == null) setConnectionPool();  	

		List<Dataset> dsl = new ArrayList<Dataset>();
		String [] datasetProps  = config.getStringArray("dataset.list");

		for(int i = 0; i < datasetProps.length; i++) {
			Dataset ds =loadDataset(datasetProps[i]); 
			dsl.add(ds);
		}
		return dsl;
	}

	private Dataset loadDataset(String rn) throws AdaptorException {
		InputStream fr;
		Dataset ds = null;
		try {
			fr = getClass().getResourceAsStream("/"+rn);
			ds = (Dataset) m.readValue(fr, Dataset.class);

		} catch (Exception e) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return ds;
	}

	private void setConnectionPool() throws AdaptorException{
		if(cpds != null) return;

		try {
			cpds = new ComboPooledDataSource(config.getString("storename"));
			Iterator<String> it = config.getKeys("c3po");
			while(it.hasNext()){
				String key = it.next();
				String method_name = key.replaceAll("c3po.", "");
				String value = config.getString(key);
				try { 
					int ivalue = Integer.parseInt(value);
					java.lang.reflect.Method method = cpds.getClass().getMethod(method_name, Integer.TYPE);
					method.invoke(cpds,  ivalue);
				} catch (NumberFormatException e){
					java.lang.reflect.Method method = cpds.getClass().getMethod(method_name, String.class);
					method.invoke(cpds, value);
				}
			}
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage(), e);
		}

	}
	public GenericAdaptor(Configuration config) throws Exception{
		super(config);
		if(cpds == null) setConnectionPool();
		for(Dataset ds : this.id2datasetHash.values()) {
			PreparedStatements ps = new PreparedStatements(ds);
			ds2pstmts.put(ds.getId(), ps);
		}
		
	}

	@Override
	public Network buildNetwork(Dataset dataset) throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;
		if(dataset.getProperty(DefaultEdgeTypes) == null ) return null;

		String [] edgeTypeStr = dataset.getProperty(DefaultEdgeTypes).split(":");
		List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
		for(String etStr : edgeTypeStr) {
			edgeTypes.add(Enum.valueOf(EdgeType.class, etStr));
		}
		return buildNetwork(dataset, edgeTypes);
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> queryEdgeTypes)
			throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();

		Network network = new Network(
				IdGenerator.Network.nextId(),
				"network" + dataset.getId()+".buildNetwork", 
				graph);

		// Supported EdgeType checking
		String [] dsEdgeTypeStr = dataset.getProperty(SupportedEdgeTypes).split(":");
		List<EdgeType> dsEdgeTypes = new ArrayList<EdgeType>();
		for(String ets : dsEdgeTypeStr) { dsEdgeTypes.add(Enum.valueOf(EdgeType.class, ets)); }
		dsEdgeTypes.retainAll(queryEdgeTypes);
		if(dsEdgeTypes.size() < 1) return null;

		try {
			String method_name = "buildNetwork";
			// For each EdgeTypes
			for(EdgeType et : dsEdgeTypes) {
				// determine Node Type
				String ndStr [] = et.toString().split("_");

				NodeType nt1 = Enum.valueOf(NodeType.class, ndStr[0]);
				NodeType nt2 = Enum.valueOf(NodeType.class, ndStr[1]);

				if(dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]) == null || 
						dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]) == null)
					throw new AdaptorException("Couldn't find proper EdgeType to Node EntityType Mapping: " + 
							et.toString());
				EntityType et1 = Enum.valueOf(EntityType.class, dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]));
				EntityType et2 = Enum.valueOf(EntityType.class, dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]));			

				String property_suffix = method_name + "." + et1 + "_" + et2;

				PreparedStatement pstmt = ds2pstmts.get(dataset.getId()).sql2pstmt.get(SQL_Statement_Prefix + property_suffix);
				ResultSet rs = pstmt.executeQuery();

				if(dataset.getProperty(Resultset_Node1_Index_Prefix + property_suffix) == null || 
						dataset.getProperty(Resultset_Node2_Index_Prefix + property_suffix) == null) {
					throw new AdaptorException("SQL Result Index for Node 1 and 2 are not defined"); 
				}
				int node1Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node1_Index_Prefix  + property_suffix)); // don't need but for better design and safety
				int node2Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node2_Index_Prefix  + property_suffix));
				int weightIdx = -1;
				if (dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix) != null) {
					weightIdx = Integer.parseInt(dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix));
				}
				

				Map<String,Node> nodes = new HashMap<String,Node>(); 

				while(rs.next()) {
					String node1_id = rs.getString(node1Idx);
					String node2_id = rs.getString(node2Idx);
					Node node1 = null;
					Node node2 = null;
					if(nodes.containsKey(node1_id)) {
						node1 = nodes.get(node1_id);
					} else {
						node1 = getNode(new Entity(node1_id, et1), nt1);
						graph.addVertex(node1);
					}
					if(nodes.containsKey(node2_id)) {
						node2 = nodes.get(node2_id);
					} else {
						node2 = getNode(new Entity(node2_id, et2), nt2);
						graph.addVertex(node2);
					}
					Edge edge = new Edge(IdGenerator.Edge.nextId(), node1_id+":"+node2_id, dataset);
					if(weightIdx > -1) {
						edge.setConfidence(rs.getFloat(weightIdx));
					}
					graph.addEdge(edge, node1, node2);
				}
				rs.close();
			}

		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
	}


	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity)
			throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;
		if(dataset.getProperty(DefaultEdgeTypes) == null ) return null;

		String [] edgeTypeStr = dataset.getProperty(DefaultEdgeTypes).split(":");
		List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
		for(String etStr : edgeTypeStr) {
			edgeTypes.add(Enum.valueOf(EdgeType.class, etStr));
		}
		return buildFirstNeighborNetwork(dataset, entity, edgeTypes);
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity,
			List<EdgeType> queryEdgeTypes) throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();

		Network network = new Network(
				IdGenerator.Network.nextId(),
				"network" + dataset.getId()+"."+ entity.getId()+".firstNeighbor", 
				graph);

		// Supported EdgeType checking
		String [] dsEdgeTypeStr = dataset.getProperty(SupportedEdgeTypes).split(":");
		List<EdgeType> dsEdgeTypes = new ArrayList<EdgeType>();
		for(String ets : dsEdgeTypeStr) { dsEdgeTypes.add(Enum.valueOf(EdgeType.class, ets)); }
		dsEdgeTypes.retainAll(queryEdgeTypes);
		if(dsEdgeTypes.size() < 1) return null;

		try {
			String method_name = "buildFirstNeighborNetwork";
			// For each EdgeTypes
			for(EdgeType et : dsEdgeTypes) {
				// determine Node Type
				String ndStr [] = et.toString().split("_");

				NodeType nt1 = Enum.valueOf(NodeType.class, ndStr[0]);
				NodeType nt2 = Enum.valueOf(NodeType.class, ndStr[1]);

				if(dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]) == null || 
						dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]) == null)
					throw new AdaptorException("Couldn't find proper EdgeType to Node EntityType Mapping: " + 
							et.toString());
				EntityType et1 = Enum.valueOf(EntityType.class, dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]));
				EntityType et2 = Enum.valueOf(EntityType.class, dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]));			

				String property_suffix = method_name + "." + et1 + "_" + et2 + "." + entity.getType();

				PreparedStatement pstmt = ds2pstmts.get(dataset.getId()).sql2pstmt.get(SQL_Statement_Prefix + property_suffix);
				String [] psIdx = dataset.getProperty(PreparedStatement_BIND_Prefix1 + property_suffix).split(":");
				for (String pi : psIdx) {
					if(dataset.getProperty(SQL_Like_Statement_Prefix + property_suffix) != null) {
						pstmt.setString(Integer.parseInt(pi), entity.getId() + '%');
					} else {
						pstmt.setString(Integer.parseInt(pi), entity.getId());

					}
				}
				ResultSet rs = pstmt.executeQuery();

				if(dataset.getProperty(Resultset_Node1_Index_Prefix + property_suffix) == null || 
						dataset.getProperty(Resultset_Node2_Index_Prefix + property_suffix) == null) {
					throw new AdaptorException("SQL Result Index for Node 1 and 2 are not defined"); 
				}
				int node1Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node1_Index_Prefix  + property_suffix)); // don't need but for better design and safety
				int node2Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node2_Index_Prefix  + property_suffix));
				int weightIdx = -1;
				if (dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix) != null) {
					weightIdx = Integer.parseInt(dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix));
				}
				
				Node node1 = null;
				Node node2 = null;
				boolean first = true;
				while(rs.next()) {

					String node1_id = rs.getString(node1Idx);
					String node2_id = rs.getString(node2Idx);

					node1 = getNode(new Entity(node1_id, et1), nt1);
					node2 = getNode(new Entity(node2_id, et2), nt2);
					
					graph.addVertex(node1);					
					graph.addVertex(node2);

					Edge edge = new Edge(IdGenerator.Edge.nextId(), node1_id+":"+node2_id, dataset);
					if(weightIdx > -1) {
						edge.setConfidence(rs.getFloat(weightIdx));
					}
					graph.addEdge(edge, node1, node2);
					first = false;
				}
				rs.close();
			}

		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
	}




	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
			throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;
		if(dataset.getProperty(DefaultEdgeTypes) == null ) return null;

		String [] edgeTypeStr = dataset.getProperty(DefaultEdgeTypes).split(":");
		List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
		for(String etStr : edgeTypeStr) {
			edgeTypes.add(Enum.valueOf(EdgeType.class, etStr));
		}
		return buildInternalNetwork(dataset, entities, edgeTypes);
	}

	
	private String list2String (List<Entity> entities) {
		String result = "";
		boolean first = true;
		
		for(Entity e : entities) {
			if(first) {
				result = "'" +e.getId();
				first = false;
			} else {
				result += "','" + e.getId();
			}
		}
		return result+ "'";
	}
	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> queryEdgeTypes) throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;
		if(entities.size() < 1) return null;
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		
		String qetstr1 = "";
		String qetstr2 = "";
		String qestr1 = "";
		String qestr2 = "";
		Map<String,List<Entity> > qet2list = new TreeMap<String, List<Entity>>();
		Set<String> queryIDs = new HashSet<String>();
		
		for(Entity e : entities) {
			queryIDs.add(e.getId());
			if(!qet2list.containsKey(e.getType().toString())) {
				List<Entity> el = new ArrayList<Entity>();
				qet2list.put(e.getType().toString(), el);
				el.add(e);
			} else {
				qet2list.get(e.getType().toString()).add(e);
			}
		}
		if(qet2list.size() > 2) {
			throw new AdaptorException("There are more than two Entity types on query entities");
		} 
		qetstr1 = (String) qet2list.keySet().toArray()[0];
		qestr1 = list2String(qet2list.get(qetstr1));
		if (qet2list.size() == 2){
			qetstr2 = (String) qet2list.keySet().toArray()[1];
			qestr2 = list2String(qet2list.get(qetstr2));
		}
		
		
		
		Network network = new Network(
				IdGenerator.Network.nextId(),
				"network" + dataset.getId()+"."+ qestr1.replaceAll("','", "-") +":" + qestr2.replaceAll("','","-")+".internalNetwork." + qetstr1 +"_"+qetstr2, 
				graph);

		// Supported EdgeType checking
		String [] dsEdgeTypeStr = dataset.getProperty(SupportedEdgeTypes).split(":");
		List<EdgeType> dsEdgeTypes = new ArrayList<EdgeType>();
		for(String ets : dsEdgeTypeStr) { dsEdgeTypes.add(Enum.valueOf(EdgeType.class, ets)); }
		dsEdgeTypes.retainAll(queryEdgeTypes);
		if(dsEdgeTypes.size() < 1) return null;

		try {
			String method_name = "buildInternalNetwork";
			// For each EdgeTypes
			for(EdgeType et : dsEdgeTypes) {
				// determine Node Type
				String ndStr [] = et.toString().split("_");

				NodeType nt1 = Enum.valueOf(NodeType.class, ndStr[0]);
				NodeType nt2 = Enum.valueOf(NodeType.class, ndStr[1]);

				if(dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]) == null || 
						dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]) == null)
					throw new AdaptorException("Couldn't find proper EdgeType to Node EntityType Mapping: " + 
							et.toString());
				EntityType et1 = Enum.valueOf(EntityType.class, dataset.getProperty(Node1_Type_Mapping_Prefix+ndStr[0]));
				EntityType et2 = Enum.valueOf(EntityType.class, dataset.getProperty(Node2_Type_Mapping_Prefix+ndStr[1]));			

				//if(et1 == et2 && qet2list.size() != 1) {
				//	throw new AdaptorException(et1.toString() +"_" + et2.toString() + " network but there is two query entity types");
				//}
				String property_suffix = "";
				if(qetstr2.equals("")) {
					property_suffix = method_name + "." + et1 + "_" + et2 + "." + qetstr1;
				} else {
					if(!et1.equals(qetstr1)) {
						// swap 1 & 2
						String tmp = qetstr1;
						qetstr1 = qetstr2;
						qetstr2 = tmp;
						
						tmp = qestr1;
						qestr1 = qestr2;
						qestr2 = tmp;
					}
					property_suffix = method_name + "." + et1 + "_" + et2 + "." + qetstr1 + "_" + qetstr2;
				}
				
				if(dataset.getProperty(SQL_Like_Statement_Prefix + property_suffix) != null ||
						dataset.getProperty(Safer_Internal_Network_Prefix) != null) {
					
					// not implemented yet...
					throw new AdaptorException("Like statement or safer network build is not implemented yet");
//					for(Entity entity : entities) {
//						String geneId = entity.getId();
//						Node node = getNode(IdGenerator.Node.nextId(), entity.getId(), entity, nt);
//						graph.addVertex(node);
//					}
//					Set<Node> intCollection = new HashSet<Node>(graph.getVertices());
//					for(Entity entity : entities) {
//						Network subnet = this.buildFirstNeighborNetwork(dataset, entity);
//						Graph<Node,Edge> subGraph = subnet.getGraph();
//						Set<Node> nodeCollection = new HashSet<Node>(subGraph.getVertices());
//						nodeCollection.removeAll(intCollection);
//						for(Node n : nodeCollection) {
//							subGraph.removeVertex(n);
//						}
//						for(Edge e : subGraph.getEdges()) {
//							graph.addEdge(e, subGraph.getSource(e), subGraph.getDest(e));
//						}
//					}

				}

				
				PreparedStatement pstmt = ds2pstmts.get(dataset.getId()).sql2pstmt.get(SQL_Statement_Prefix + property_suffix);
				Connection con = pstmt.getConnection();
				
				// convert all occurrence of '?' with the index
				String sql_stmt = dataset.getProperty(SQL_Statement_Prefix + property_suffix);
				int qidx = sql_stmt.indexOf('?');
				int psidx = 1;
				String new_query = sql_stmt.substring(0, qidx) + "?1";
				int lqidx = qidx;
				while((qidx = sql_stmt.indexOf('?', qidx+1)) > -1) {
					
					new_query += sql_stmt.substring(lqidx + 1, qidx) + '?' + ++psidx;
					lqidx = qidx;
				}
				new_query += sql_stmt.substring(lqidx + 1);
				
				String [] psIdx = dataset.getProperty(PreparedStatement_BIND_Prefix1 + property_suffix).split(":");
				for (String pi : psIdx) {
						//pstmt.setString(Integer.parseInt(pi), qestr1);
					new_query = new_query.replaceAll("\\?" + pi, qestr1);
						
				}
				if(!qestr2.equals("")) {
					psIdx = dataset.getProperty(PreparedStatement_BIND_Prefix2 + property_suffix).split(":");
					for (String pi : psIdx) {
						//pstmt.setString(Integer.parseInt(pi), qestr2);
						new_query = new_query.replaceAll("\\?" + pi, qestr2);
					}
				}
				//ResultSet rs = pstmt.executeQuery();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(new_query);

				if(dataset.getProperty(Resultset_Node1_Index_Prefix + property_suffix) == null || 
						dataset.getProperty(Resultset_Node2_Index_Prefix + property_suffix) == null) {
					throw new AdaptorException("SQL Result Index for Node 1 and 2 are not defined"); 
				}
				int node1Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node1_Index_Prefix  + property_suffix)); // don't need but for better design and safety
				int node2Idx  = Integer.parseInt(dataset.getProperty(Resultset_Node2_Index_Prefix  + property_suffix));
				int weightIdx = -1;
				if (dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix) != null) {
					weightIdx = Integer.parseInt(dataset.getProperty(Resultset_Weight_Index_Prefix + property_suffix));
				}

				Map<String,Node> nodes = new TreeMap<String,Node>(); 
				while(rs.next()) {
					String node1_id = rs.getString(node1Idx);
					String node2_id = rs.getString(node2Idx);
					if(!queryIDs.contains(node1_id) || !queryIDs.contains(node2_id)) continue; // It shouldn't happen but if it happens, then let's skip
					Node node1 = null;
					Node node2 = null;
					// add validation for the generated Entity is the same Entity on Query
					if(nodes.containsKey(node1_id)) {
						node1 = nodes.get(node1_id);
					} else {
						node1 = getNode(new Entity(node1_id, et1), nt1);
						nodes.put(node1_id, node1);
						graph.addVertex(node1);
					}
					if(nodes.containsKey(node2_id)) {
						node2 = nodes.get(node2_id);
					} else {
						node2 = getNode(new Entity(node2_id, et2), nt2);
						nodes.put(node2_id, node2);
						graph.addVertex(node2);
					}
					
					Edge edge = new Edge(IdGenerator.Edge.nextId(), node1_id+":"+node2_id+":"+et1 + "_" + et2, dataset);
					if(weightIdx > -1) {
						edge.setConfidence(rs.getFloat(weightIdx));
					}
					graph.addEdge(edge, node1, node2);
				}
				rs.close();
				stmt.close();
			}

		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
	}

	private boolean containEntity(Dataset dataset, Entity entity) throws AdaptorException {
		try {
			String method_name = "getDatasets";
			String property_suffix = method_name + "." + entity.getType();

			PreparedStatement pstmt = ds2pstmts.get(dataset.getId()).sql2pstmt.get(SQL_Statement_Prefix + property_suffix);
			String [] psIdx = dataset.getProperty(PreparedStatement_BIND_Prefix1 + property_suffix).split(":");
			for (String pi : psIdx) {
				if(dataset.getProperty(SQL_Like_Statement_Prefix + property_suffix) != null) {
					pstmt.setString(Integer.parseInt(pi), entity.getId() + '%');
				} else {
					pstmt.setString(Integer.parseInt(pi), entity.getId());

				}
			}
			ResultSet rs = pstmt.executeQuery();
			boolean result = false;
			if(rs.next()) {
				if(rs.getInt(1) > 0) {
					result = true;
				}
			} 
			rs.close();
			return result;
		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		
		List<Dataset> result  = new ArrayList<Dataset>();
		for( Dataset ds : id2datasetHash.values()) {
			if(containEntity(ds, entity)){
				result.add(ds);
			}
		}
		return result;
	}

}
