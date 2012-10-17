package us.kbase.networks.adaptor.genericMySQL;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class GenericMySQLAdaptor implements Adaptor{
	
	public static final String DATASET_ID_PREFIX = "kb|netdataset.";
	public static final String NETWORK_ID_PREFIX = "kb|net.";
	public static final String NODE_ID_PREFIX = "kb|netnode.";
	public static final String EDGE_ID_PREFIX = "kb|netedge.";

	
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
	
	
	
	private Configuration ac;
	private List<Dataset> dsl;
    private static ObjectMapper m = new ObjectMapper();

    // TODO: prepared stmt will be set for each Dataset & Edgetype
    private PreparedStatement pstFindNeighbor;
    private PreparedStatement pstFindIntNetwork;
	
	// TODO: be prepared for mysql connections & sql stmt (prepared)
	private void loadDatasets() throws AdaptorException {
		
		dsl = new ArrayList<Dataset>();
		String [] datasetProps  = ac.getStringArray("dataset.list");
		
		for(int i = 0; i < datasetProps.length; i++) {
			dsl.add(loadDataset(datasetProps[i]));
		}
	}
	
	private Dataset loadDataset(String rn) throws AdaptorException {
		FileReader fr;
		try {
			fr = new FileReader(rn);
			/* The content of rn:
			 * {
				  "id" : "id",
				  "name" : "myname",
				  "description" : "desc",
				  "networkType" : "PROT_PROT_INTERACTION",
				  "datasetSource" : "INTACT",
				  "taxon" : [ "3701", "3702" ],
				  "properties" : {
				    "testKey2" : "testValue2",
				    "testKey1" : "testValue1"
				  }
			   }
			 */
			return (Dataset) m.readValue(fr, Dataset.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AdaptorException(e.getMessage(), e);
		}
	}

	public GenericMySQLAdaptor(Configuration ac) throws Exception{
		this.ac = ac;
		loadDatasets();
		ConnectionManager.host   = ac.getString("host", "localhost");
		ConnectionManager.port   = ac.getInt   ("port", 3306);
		ConnectionManager.dbName = ac.getString("db", "mysql");
		ConnectionManager.user   = ac.getString("user", "root");
		ConnectionManager.passwd   = ac.getString("passwd", "");
		
		// TODO: the following will be generalized for each dataset & nodeType & edgeType
		this.pstFindNeighbor   = ConnectionManager.getConnection().prepareStatement(ac.getString("sql.findNeighbor"));
		this.pstFindIntNetwork = ConnectionManager.getConnection().prepareStatement(ac.getString("sql.findIntNetwork"));
	}

	@Override
	public List<Dataset> getDatasets() throws AdaptorException {
		// TODO: make deep copy later
		return dsl; 
	}	
	
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

	@Override
	public boolean hasDataset(Dataset dataset) throws AdaptorException {
		return this.dsl.contains(dataset);
	}

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
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId)
			throws AdaptorException {
		if(!this.dsl.contains(dataset)) return null;
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(this.NETWORK_ID_PREFIX+dataset.getId()+"."+ geneId+".firstNeighbor", "name", graph);
		
		NodeType nt = Enum.valueOf(NodeType.class, dataset.getProperty("default.nodeType"));
//		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		int rsIdx = Integer.parseInt(dataset.getProperty("sql.findNeighbor.rsIndex"));
		
		Node query = getNode(this.NODE_ID_PREFIX + geneId, geneId, new Entity(geneId), nt);
		graph.addVertex(query);
		try {
			this.pstFindIntNetwork.setString(1, geneId);
			ResultSet rs = this.pstFindIntNetwork.executeQuery();
			while(rs.next()) {
				String neighborId = rs.getString(rsIdx);
				Node neighbor = getNode(this.NODE_ID_PREFIX + neighborId, neighborId, new Entity(neighborId), nt);
				graph.addVertex(neighbor);
				graph.addEdge(new Edge(this.EDGE_ID_PREFIX+geneId+":"+neighborId, geneId+":"+neighborId, dataset), 
						query, neighbor);
			}
		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId,
		List<EdgeType> edgeTypes) throws AdaptorException {
		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		if(!edgeTypes.contains(et)) return null;
		return buildFirstNeighborNetwork(dataset, geneId);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds)
			throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		NodeType nt = Enum.valueOf(NodeType.class, dataset.getProperty("default.nodeType"));
		for(String geneId : geneIds) {
			Node node = getNode(this.NODE_ID_PREFIX + geneId, geneId, new Entity(geneId), nt);
			graph.addVertex(node);
		}
		Set<Node> intCollection = new HashSet<Node>(graph.getVertices());
		Network network = new Network(this.NETWORK_ID_PREFIX+dataset.getId()+"."+ StringUtils.join(geneIds, "-")+".intNetwork", dataset.getName()+"."+ StringUtils.join(geneIds, "-")+".intNetwork", graph);
		for(String geneId : geneIds) {
			Network subnet = this.buildFirstNeighborNetwork(dataset, geneId);
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
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
			List<EdgeType> edgeTypes) throws AdaptorException {
		EdgeType et = Enum.valueOf(EdgeType.class, dataset.getProperty("default.edgeType"));
		if(!edgeTypes.contains(et)) return null;
		return buildInternalNetwork(dataset, geneIds);
	}

}
