package us.kbase.networks.adaptor.ppi;

import java.sql.*;
import java.util.*;

import us.kbase.networks.adaptor.*;
import us.kbase.networks.core.*;
import us.kbase.networks.adaptor.ppi.local.PPI;

import org.strbio.util.*;
import edu.uci.ics.jung.graph.*;

/**
   Class implementing an Adaptor for PPI data in KBase Networks API

   @version 1.1, 10/17/12
   @author JMC
*/
public class PPIAdaptor implements Adaptor {
    // fixme:  these should all be in a core class
    public static final String DATASET_ID_PREFIX = "kb|netdataset.";
    public static final String NETWORK_ID_PREFIX = "kb|net.";
    public static final String NODE_ID_PREFIX = "kb|netnode.";
    public static final String EDGE_ID_PREFIX = "kb|netedge.";

    // default edge type for PPI network:
    public static final List<EdgeType> DEFAULT_EDGE_TYPES = Arrays.asList(EdgeType.PROTEIN_CLUSTER);

    // default dataset id format for ppi networks:
    public static final String DATASET_PPI_ID_PREFIX = DATASET_ID_PREFIX+"ppi.";

    // default Cluster node id format
    public static final String CLUSTER_PPI_ID_PREFIX = "kb|ppi.";

    // default Edge id format
    public static final String EDGE_PPI_ID_PREFIX = "kb|ppi.edge.";
    
    // helper functions copied from RegPreciseAdaptor.java
    private int uniqueIndex = 0;

    private String getNodeID() {
	return NODE_ID_PREFIX + (uniqueIndex ++);
    }

    private String getEdgeID() {
	return EDGE_ID_PREFIX + (uniqueIndex++);
    }

    private String getNetworkID() {
	return NETWORK_ID_PREFIX + (uniqueIndex++);
    }
    
    /**
       Get a list of all PPI datasets
    */
    @Override public List<Dataset> getDatasets() throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	try{
	    PPI.connectRW();
	    Statement stmt = PPI.createStatement();
	    Statement stmt2 = PPI.createStatement();
	    ResultSet rs, rs2;

	    rs = stmt.executeQuery("select id, description, data_source from interaction_dataset");
	    while (rs.next()) {
		int datasetID = rs.getInt(1);
		String datasetName = rs.getString(2);
		DatasetSource datasetSource = DatasetSource.PPI;
		String datasetSourceName = rs.getString(3);
		if (!rs.wasNull()) {
		    if (datasetSourceName.equals("MO"))
			datasetSource = DatasetSource.MO;
		    else if (datasetSourceName.equals("EcoCyc"))
			datasetSource = DatasetSource.ECOCYC;
		}

		Vector<Taxon> taxons = new Vector<Taxon>();
		rs2 = stmt2.executeQuery("select distinct(substring_index(f.feature_id,'.',2)) from interaction_feature f, interaction i where f.interaction_id=i.id and i.interaction_dataset_id="+datasetID);
		while (rs2.next())
		    taxons.add(new Taxon(rs2.getString(1)));
		rs2.close();

		Dataset d = new Dataset(DATASET_PPI_ID_PREFIX+datasetID,
					datasetName,
					"PPI network: "+datasetName,
					NetworkType.PROT_PROT_INTERACTION,
					datasetSource,
					taxons);
		rv.add(d);
	    }
	    rs.close();
	    stmt2.close();
	    stmt.close();
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
       get list of all datasets of type PPI, but only if the
       caller asked for PPI datasets
    */
    @Override public List<Dataset> getDatasets(NetworkType networkType)
	throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	if (networkType == NetworkType.PROT_PROT_INTERACTION)
	    rv.addAll(getDatasets());
	return rv;
    }

    /**
       get PPI datasets of a requested source type.  Fixme:  this
       is an inefficient implementation; should be the other
       way around.
    */
    @Override public List<Dataset> getDatasets(DatasetSource datasetSource)
	throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	List<Dataset> allSets = getDatasets();
	for (Dataset d : allSets) {
	    DatasetSource ds2 = d.getDatasetSource();
	    if (datasetSource==null) {
		if (ds2==null)
		    rv.add(d);
	    }
	    else if (datasetSource.equals(ds2))
		rv.add(d);
	}
	return rv;
    }

    /**
       get PPI datasets of a requested taxon type.  Fixme:  this
       could be implemented more efficiently
    */
    @Override public List<Dataset> getDatasets(Taxon taxon)
	throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	List<Dataset> allSets = getDatasets();
	for (Dataset d : allSets) {
	    if (d.getTaxons().contains(taxon))
		rv.add(d);
	}
	return rv;
    }

    /**
       get PPI datasets of a requested network, source, and taxon.
       Fixme:  this could be implemented more efficiently
    */
    @Override public List<Dataset> getDatasets(NetworkType networkType,
					       DatasetSource datasetSource,
					       Taxon taxon)
	throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	if (networkType != NetworkType.PROT_PROT_INTERACTION)
	    return rv;
	List<Dataset> allSets = getDatasets(datasetSource);
	for (Dataset d : allSets) {
	    if (d.getTaxons().contains(taxon))
		rv.add(d);
	}
	return rv;
    }

    /**
       do we have a given dataset?  Fixme: don't know use case
       for this API, so it's very inefficient for now.
    */
    @Override public boolean hasDataset(Dataset dataset)
	throws AdaptorException {
	List<Dataset> allSets = getDatasets();
	if (allSets.contains(dataset))
	    return true;
	return false;
    }

    /**
       build 1st neighbor dataset with default edge types
    */
    @Override public Network buildFirstNeighborNetwork(Dataset dataset,
						       String geneID)
	throws AdaptorException {
	
	return buildFirstNeighborNetwork(dataset, geneID, DEFAULT_EDGE_TYPES);
    }

    /**
       build 1st neighbor dataset with specified edge types
    */
    @Override public Network buildFirstNeighborNetwork(Dataset dataset,
						       String geneID,
						       List<EdgeType> edgeTypes)
	throws AdaptorException {

	Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
	Network network = new Network(getNetworkID(), "", graph);

	String kbID = dataset.getId();
	int datasetID = 0;
	if (kbID.startsWith(DATASET_PPI_ID_PREFIX)) {
	    int pos = DATASET_PPI_ID_PREFIX.length();
	    datasetID = StringUtil.atoi(kbID,pos);
	}
	if (datasetID==0)
	    return network; // or should I throw an exception?

	try {
	    PPI.connect();

	    // for CLUSTER edges, just need complex that gene is in
	    if (edgeTypes.contains(EdgeType.GENE_CLUSTER) ||
		edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
		PreparedStatement stmt = PPI.prepareStatement("select i.id, i.description from interaction i, interaction_feature f where i.interaction_dataset_id=? and f.interaction_id=i.id and f.feature_id=?");

		stmt.setInt(1,datasetID);
		stmt.setString(2,geneID);

		Node n2g = null; // representing query as gene
		Node n2p = null; // representing query as protein

		if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
		    n2g = buildGeneNode(geneID);
		    graph.addVertex(n2g);
		}
		if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
		    n2p = buildProteinNode(geneID);
		    graph.addVertex(n2p);
		}
	    
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
		    int complexID = rs.getInt(1);
		    String complexDescription = rs.getString(2);

		    Node n1 = buildComplexNode(complexID,
					       complexDescription);
		    graph.addVertex(n1);

		    if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			Edge e = buildEdge(n1, n2g, dataset);
			graph.addEdge(e, n1, n2g,
				      edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
		    }
		    if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			Edge e = buildEdge(n1, n2p, dataset);
			graph.addEdge(e, n1, n2p,
				      edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
		    }
		}
		rs.close();
		stmt.close();
	    }
	    if (edgeTypes.contains(EdgeType.GENE_GENE) ||
		edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
		// get all features in same complex as query
		PreparedStatement stmt = PPI.prepareStatement("select i.id, i.description, f1.feature_id from interaction i, interaction_feature f1, interaction_feature f2 where i.interaction_dataset_id=? and f1.interaction_id=i.id and f2.interaction_id=i.id and f2.feature_id=? order by f1.rank asc");
		stmt.setInt(1,datasetID);
		stmt.setString(2,geneID);

		Vector<Node> nodesInComplexG = new Vector<Node>(); // gene
		Vector<Node> nodesInComplexP = new Vector<Node>(); // protein
		int lastComplexID = 0;
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
		    int complexID = rs.getInt(1);
		    String complexDescription = rs.getString(2);
		    String geneID2 = rs.getString(3);

		    // if new complex, fully connect all nodes in last complex
		    if (complexID != lastComplexID) {
			if (lastComplexID > 0) {
			    connectAll(nodesInComplexG,
				       graph,
				       dataset);
			    connectAll(nodesInComplexP,
				       graph,
				       dataset);
			    nodesInComplexG.clear();
			    nodesInComplexP.clear();
			}
			lastComplexID = complexID;
		    }
		    
		    Node node;
		    if (edgeTypes.contains(EdgeType.GENE_GENE)) {
			node = buildGeneNode(geneID2);
			graph.addVertex(node);
		    }
		    if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
			node = buildProteinNode(geneID2);
			graph.addVertex(node);
		    }

		}
		rs.close();
		stmt.close();
		connectAll(nodesInComplexG,
			   graph,
			   dataset);
		connectAll(nodesInComplexP,
			   graph,
			   dataset);
	    }
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
		
	return network;
    }

    /**
       build internal network with default edge types
    */
    @Override public Network buildInternalNetwork(Dataset dataset,
						  List<String> geneIDs)
	throws AdaptorException {
	return buildInternalNetwork(dataset, geneIDs, DEFAULT_EDGE_TYPES);
    }

    /**
       build internal network with specified edge types.   fixme:
       inefficient; builds entire network first then subtracts
    */
    @Override public Network buildInternalNetwork(Dataset dataset,
						  List<String> geneIDs,
						  List<EdgeType> edgeTypes)
	throws AdaptorException {

	Network network = buildNetwork(dataset,edgeTypes);
	Graph<Node, Edge> graph = network.getGraph();
	Collection<Node> allNodes = graph.getVertices();
	for (Node n : allNodes) {
	    if (!geneIDs.contains(n.getName()))
		graph.removeVertex(n);  // also removes appropriate edges
	}
	return network;
    }

    /**
       build complete network with default edge types
    */
    @Override public Network buildNetwork(Dataset dataset)
	throws AdaptorException {
	
	return buildNetwork(dataset, DEFAULT_EDGE_TYPES);
    }

    /**
       build complete network with specified edge types
    */
    @Override public Network buildNetwork(Dataset dataset,
					  List<EdgeType> edgeTypes)
	throws AdaptorException {

	Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
	Network network = new Network(getNetworkID(), "", graph);	

	String kbID = dataset.getId();
	int datasetID = 0;
	if (kbID.startsWith(DATASET_PPI_ID_PREFIX)) {
	    int pos = DATASET_PPI_ID_PREFIX.length();
	    datasetID = StringUtil.atoi(kbID,pos);
	}
	if (datasetID==0)
	    return network; // or should I throw an exception?

	try {
	    PPI.connect();

	    // get all complexes / proteins in this dataset
	    PreparedStatement stmt = PPI.prepareStatement("select i.id, i.description, f.feature_id from interaction i, interaction_feature f where i.interaction_dataset_id=? and f.interaction_id=i.id and order by f.rank asc");
	    stmt.setInt(1,datasetID);

	    Vector<Node> nodesInComplexG = new Vector<Node>(); // gene
	    Vector<Node> nodesInComplexP = new Vector<Node>(); // protein
	    int lastComplexID = 0;
	    Node lastComplexNode = null;
		
	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
		int complexID = rs.getInt(1);
		String complexDescription = rs.getString(2);
		String geneID = rs.getString(3);

		// if new complex, process all of last complex
		if (complexID != lastComplexID) {
		    if (lastComplexID > 0) {
			if (edgeTypes.contains(EdgeType.GENE_GENE)) {
			    connectAll(nodesInComplexG,
				       graph,
				       dataset);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
			    connectAll(nodesInComplexP,
				       graph,
				       dataset);
			}
			if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			    connectAll(nodesInComplexG,
				       lastComplexNode,
				       graph,
				       dataset);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			    connectAll(nodesInComplexP,
				       lastComplexNode,
				       graph,
				       dataset);
			}
			nodesInComplexG.clear();
			nodesInComplexP.clear();
		    }
		    lastComplexID = complexID;

		    if (edgeTypes.contains(EdgeType.GENE_CLUSTER) ||
			edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			lastComplexNode = buildComplexNode(complexID,
							   complexDescription);
		    }
		}
		    
		Node node;
		if ((edgeTypes.contains(EdgeType.GENE_GENE)) ||
		    (edgeTypes.contains(EdgeType.GENE_CLUSTER))) {
		    node = buildGeneNode(geneID);
		    graph.addVertex(node);
		}
		if ((edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) ||
		    (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER))) {
		    node = buildProteinNode(geneID);
		    graph.addVertex(node);
		}
		rs.close();
		stmt.close();
		if (edgeTypes.contains(EdgeType.GENE_GENE)) {
		    connectAll(nodesInComplexG,
			       graph,
			       dataset);
		}
		if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
		    connectAll(nodesInComplexP,
			       graph,
			       dataset);
		}
		if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
		    connectAll(nodesInComplexG,
			       lastComplexNode,
			       graph,
			       dataset);
		}
		if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
		    connectAll(nodesInComplexP,
			       lastComplexNode,
			       graph,
			       dataset);
		}
	    }
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	
	return network;
    }


    /**
       make a node representing a protein complex
    */
    private Node buildComplexNode(int interactionID,
				  String interactionName) {
	Node rv = Node.buildClusterNode(getNodeID(),
					"complex "+interactionName,
					new Entity(CLUSTER_PPI_ID_PREFIX+interactionID));
	// todo: add properties
	return rv;
    }

    /**
       make a node representing a protein
    */
    private Node buildProteinNode(String featureID) {
	Node rv = Node.buildProteinNode(getNodeID(),
					featureID,
					new Entity(featureID));
	// todo: add properties
	return rv;
    }

    /**
       make a node representing a gene
    */
    private Node buildGeneNode(String featureID) {
	Node rv = Node.buildProteinNode(getNodeID(),
					featureID,
					new Entity(featureID));
	// todo: add properties
	return rv;
    }

    /**
       make an edge between 2 nodes
    */
    private Edge buildEdge(Node n1, Node n2, Dataset d) {
	Edge rv = new Edge(getEdgeID(),
			   n1.getName()+"_"+n2.getName(),
			   d);
			   
	// todo: add properties
	return rv;
    }

    /**
       connect all nodes in a complex to each other, given a list.
       fixme:  correct edge direction
    */
    private void connectAll(List<Node> nodes,
			    Graph<Node, Edge> g,
			    Dataset d) {
	int n = nodes.size();
	for (int i=0; i<n; i++) {
	    for (int j=i+1; j<n; j++) {
		Node n1 = nodes.get(i);
		Node n2 = nodes.get(j);
		Edge e = buildEdge(n1, n2, d);
		g.addEdge(e, n1, n2,
			  edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
	    }
	}
			   
	// todo: add properties
    }

    /**
       connect nodes all in a complex, given a list, to a complex node
    */
    private void connectAll(List<Node> nodes,
			    Node complexNode,
			    Graph<Node, Edge> g,
			    Dataset d) {
	for (Node n : nodes) {
	    Edge e = buildEdge(complexNode, n, d);
	    g.addEdge(e, complexNode, n,
		      edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
	}
			   
	// todo: add properties
    }
    
}
