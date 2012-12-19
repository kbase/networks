 package us.kbase.networks.adaptor.genericMySQL;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class GenericMySQLAdaptor extends AbstractAdaptor{
	


	// TODO: upgrade code to use connection pool management library
	private static class ConnectionManager {
		private static Connection con = null;
		public static String host = "localhost";
		public static int port = 3306;
		public static String dbName = "mysql";
		public static String user = "root";
		public static String passwd = "";
		public static Connection getConnection() throws ClassNotFoundException, SQLException {			
			if(con == null) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://"+host+":" + port +"/" + dbName, user, passwd);
			}
			return con;
		}
	}
			
    private static ObjectMapper m = new ObjectMapper();

    // TODO: prepared stmt will be set for each Dataset & Edgetype
    private PreparedStatement pstFindNeighbor;
    private PreparedStatement pstFindIntNetwork;
	
	// TODO: be prepared for mysql connections & sql stmt (prepared)
    @Override
	protected List<Dataset> loadDatasets() throws AdaptorException {
		
    	List<Dataset> dsl = new ArrayList<Dataset>();
		String [] datasetProps  = config.getStringArray("dataset.list");
		
		for(int i = 0; i < datasetProps.length; i++) {
			dsl.add(loadDataset(datasetProps[i]));
		}
		return dsl;
	}
	
	private Dataset loadDataset(String rn) throws AdaptorException {
		InputStream fr;
		try {
			fr = getClass().getResourceAsStream("/"+ rn);
			return (Dataset) m.readValue(fr, Dataset.class);
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage(), e);
		}
	}

	public GenericMySQLAdaptor(Configuration config) throws Exception{
		super(config);
		//ConnectionManager.host   = config.getString("host", "localhost");
		ConnectionManager.host   = "10.0.8.34";
		ConnectionManager.port   = config.getInt   ("port", 3306);
		ConnectionManager.dbName = config.getString("db", "mysql");
		ConnectionManager.user   = config.getString("user", "root");
		ConnectionManager.passwd   = config.getString("passwd", "");
		
		// TODO: the following will be generalized for each dataset & nodeType & edgeType
		this.pstFindNeighbor   = ConnectionManager.getConnection().prepareStatement(config.getString("sql.findNeighbor"));
		this.pstFindIntNetwork = ConnectionManager.getConnection().prepareStatement(config.getString("sql.findIntNetwork"));
	}

	/*
	@Override
	public List<Dataset> loadDatasets() throws AdaptorException {
		// TODO: make deep copy later
		return dsl; 
	}	
	*/
	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType)
			throws AdaptorException {
		List<Dataset> result = new ArrayList<Dataset>();
		for(Dataset d : this.dsl) {
			if(d.getNetworkType() == networkType) {
				result.add(d);
			}
		}
		return result;
	}
	*/
	

	/*
	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource)
			throws AdaptorException {
		List<Dataset> result = new ArrayList<Dataset>();
		for(Dataset d : this.dsl) {
			if(d.getDatasetSource() == datasetSource) {
				result.add(d);
			}
		}
		return result;
	}
	*/
	

	/*
	@Override
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException {
		List<Dataset> result = new ArrayList<Dataset>();
		for(Dataset d : this.dsl) {
			if(d.getTaxons().contains(taxon)) {
				result.add(d);
			}
		}
		return result;
	}
	*/

	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> result = new ArrayList<Dataset>();
		for(Dataset d : this.dsl) {
			if(d.getNetworkType() == networkType && d.getDatasetSource() == datasetSource 
					&& d.getTaxons().contains(taxon)) {
				result.add(d);
			}
		}
		return result;
	}
	*/

	@Override
	public Network buildNetwork(Dataset dataset) throws AdaptorException {
		return null;
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes)
			throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}

	private Node getNode(String id, String name, Entity entity, NodeType nt) {
		switch(nt) {
		case GENE: return Node.buildGeneNode(id, name, entity);
		case PROTEIN: return Node.buildProteinNode(id, name, entity);
		case CLUSTER: return Node.buildClusterNode(id, name, entity);
		}
		return null;
	}
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity)
			throws AdaptorException {
		//if(!this.dsl.contains(dataset)) return null;
		if( !hasDataset(dataset.getId())) return null;
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(
				IdGenerator.Network.nextId(),
				"netowrk" + dataset.getId()+"."+ entity.getId()+".firstNeighbor", 
				graph);
		
		NodeType nt = Enum.valueOf(NodeType.class, dataset.getProperty("default.nodeType"));
//		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		int rsIdx = Integer.parseInt(dataset.getProperty("sql.findNeighbor.rsIndex"));
		int rsConfidenceIdx = 0;
		if(dataset.getProperty("sql.findNeighbor.rsConfidenceIndex") != null) {
			rsConfidenceIdx = Integer.parseInt(dataset.getProperty("sql.findNeighbor.rsConfidenceIndex"));
		}
		
		String [] psIdxes = dataset.getProperty("sql.findNeighbor.psIndex").split(":"); 
		
		Node query = getNode(IdGenerator.Node.nextId(), entity.getId(), new Entity(entity.getId(), EntityType.GENE), nt);
		graph.addVertex(query);
		
		
		try {
			for(String psIdx : psIdxes) {
				if(dataset.getProperty("sql.findNeighbor.like").equals("yes")) {
					this.pstFindNeighbor.setString(Integer.parseInt(psIdx), entity.getId() + '%');					
				}
				else {
					this.pstFindNeighbor.setString(Integer.parseInt(psIdx), entity.getId());
				}
			}
			ResultSet rs = this.pstFindNeighbor.executeQuery();
			while(rs.next()) {
				String neighborId = rs.getString(rsIdx);
				Node neighbor = getNode(IdGenerator.Node.nextId(), neighborId, new Entity(neighborId, EntityType.UNKNOWN), nt);
				graph.addVertex(neighbor);
				Edge edge = new Edge(IdGenerator.Edge.nextId(), entity.getId()+":"+neighborId, dataset);
				if(rsConfidenceIdx > 0) {
					edge.setConfidence(rs.getFloat(rsConfidenceIdx));
				}
				graph.addEdge(edge, query, neighbor);
			}
		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity,
		List<EdgeType> edgeTypes) throws AdaptorException {
		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		if(!edgeTypes.contains(et)) return null;
		return buildFirstNeighborNetwork(dataset, entity);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
			throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		NodeType nt = Enum.valueOf(NodeType.class, dataset.getProperty("default.nodeType"));
		for(Entity entity : entities) {
			String geneId = entity.getId();
			Node node = getNode(IdGenerator.Node.nextId(), geneId, new Entity(geneId, EntityType.GENE), nt);
			graph.addVertex(node);
		}
		Set<Node> intCollection = new HashSet<Node>(graph.getVertices());
		Network network = new Network(
				IdGenerator.Network.nextId()
				, dataset.getName()+"."+ Entity.toIdsString(entities, "-")+".intNetwork"
				, graph);
		for(Entity entity : entities) {
			Network subnet = this.buildFirstNeighborNetwork(dataset, entity);
			Graph<Node,Edge> subGraph = subnet.getGraph();
			Set<Node> nodeCollection = new HashSet<Node>(subGraph.getVertices());
			nodeCollection.removeAll(intCollection);
			for(Node n : nodeCollection) {
				subGraph.removeVertex(n);
			}
			for(Edge e : subGraph.getEdges()) {
				graph.addEdge(e, subGraph.getSource(e), subGraph.getDest(e));
			}
		}
		return network;
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> edgeTypes) throws AdaptorException {
		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		if(!edgeTypes.contains(et)) return null;
		return buildInternalNetwork(dataset, entities);
	}

	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}

}
