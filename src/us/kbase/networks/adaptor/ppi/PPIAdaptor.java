package us.kbase.networks.adaptor.ppi;

import java.sql.*;
import java.util.*;

import us.kbase.networks.adaptor.*;
import us.kbase.networks.core.*;
import us.kbase.networks.adaptor.ppi.local.PPI;

import org.strbio.util.*;
import edu.uci.ics.jung.graph.*;

/**
 * Class implementing an Adaptor for PPI data in KBase Networks API
 * 
 * @version 3.01, 2/5/13
 * @author JMC
 */
public class PPIAdaptor extends AbstractAdaptor {

    // default edge type for PPI network:
    public static final List<EdgeType> DEFAULT_EDGE_TYPES =
	Arrays.asList(EdgeType.PROTEIN_CLUSTER);

    // default Cluster node ID format
    public static final String CLUSTER_PPI_ID_PREFIX = "kb|ppi.";

    public static final String ADAPTOR_PREFIX = "ppi";

    public PPIAdaptor() throws AdaptorException {
	super(null);
    }

    /**
     * Get a list of all PPI datasets
     */
    @Override
	protected List<Dataset> loadDatasets() throws AdaptorException {
	List<Dataset> rv = new Vector<Dataset>();
	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    ResultSet rs;

	    rs = stmt.executeQuery("select id from interaction_dataset");
	    while (rs.next()) {
		int datasetID = rs.getInt(1);
		Dataset d = buildDataset(datasetID);
		rv.add(d);
	    }
	    rs.close();
	    stmt.close();
	    con.close();
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
     * get PPI datasets containing a requested entity
     */
    @Override
	public List<Dataset> getDatasets(Entity entity)
	throws AdaptorException {
	
	List<Dataset> rv = new Vector<Dataset>();
	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    PreparedStatement stmt = PPI.prepareStatement(con,
							  "select distinct(i.interaction_dataset_id) from interaction i, interaction_protein f where f.interaction_id=i.id and f.feature_id=?");

	    String featureID = entity.getId();
	    stmt.setString(1, featureID);

	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
		int datasetID = rs.getInt(1);
		Dataset d = buildDataset(datasetID);
		rv.add(d);
	    }
	    rs.close();
	    stmt.close();
	    con.close();
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
     * build 1st neighbor dataset with default edge types
     */
    @Override
	public Network buildFirstNeighborNetwork(Dataset dataset,
						 Entity entity)
	throws AdaptorException {

	return buildFirstNeighborNetwork(dataset, entity, DEFAULT_EDGE_TYPES);
    }

    /**
     * build 1st neighbor dataset with specified edge types
     */
    @Override
	public Network buildFirstNeighborNetwork(Dataset dataset,
						 Entity entity,
						 List<EdgeType> edgeTypes) throws AdaptorException {

	Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
	Network network = new Network(IdGenerator.Network.nextId(), "", graph);

	String kbID = dataset.getId();
	int datasetID = StringUtil.atoi(IdGenerator.toLocalId(kbID));

	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    PreparedStatement stmt;
	    ResultSet rs;

	    EntityType queryType = entity.getType();
	    if ((queryType != EntityType.GENE) &&
		(queryType != EntityType.PROTEIN) &&
		(queryType != EntityType.PPI_COMPLEX))
		throw new AdaptorException("PPI adaptor can't handle query for entity type "+queryType);
	    
	    // for CLUSTER edges, just need complex that gene is in
	    if (edgeTypes.contains(EdgeType.GENE_CLUSTER) ||
		edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {

		if ((queryType == EntityType.GENE) ||
		    (queryType == EntityType.PROTEIN)) {
		    if (queryType==EntityType.GENE)
			stmt = PPI.prepareStatement(con,
						    "select i.id, f.id, f.protein_id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id and f.feature_id=? order by f.protein_id");
		    else
			stmt = PPI.prepareStatement(con,
						    "select i.id, f.id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id and f.feature_id=? and f.protein_id=?");

		    String featureID = entity.getId();
		    String proteinID = null;
		    if (queryType==EntityType.PROTEIN) {
			String[] ids = parseProteinID(featureID);
			featureID = ids[0];
			proteinID = ids[1];
		    }
		
		    stmt.setInt(1, datasetID);
		    stmt.setString(2, featureID);
		    if (queryType==EntityType.PROTEIN)
			stmt.setString(3, proteinID);

		    Node n2g = null; // representing query as gene
		    Node n2p = null; // representing query as protein

		    // track multiple possible proteins per feature
		    String lastProteinID = null;
		    
		    rs = stmt.executeQuery();
		    while (rs.next()) {
			int complexID = rs.getInt(1);
			int interactionProteinID = rs.getInt(2);

			if (queryType==EntityType.GENE) {
			    proteinID = rs.getString(3);
			    if ((lastProteinID != null) &&
				(!lastProteinID.equals(proteinID))) {
				n2p = null; // build a new protein
				lastProteinID = proteinID;
			    }
			}

			// build node representing query protein, if not
			// already done
			if ((n2g==null) &&
			    edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			    n2g = buildNode(featureID,
					    null,
					    NodeType.GENE);
			    graph.addVertex(n2g);
			}
			if ((n2p==null) &&
			    edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			    n2p = buildNode(featureID,
					    proteinID,
					    NodeType.PROTEIN);
			    graph.addVertex(n2p);
			}

			Node n1 = buildComplexNode(complexID);
			graph.addVertex(n1);

			Edge e = null;
			if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			    e = buildEdge(n1,
					  n2g,
					  interactionProteinID,
					  dataset);
			    graph.addEdge(e,
					  n1,
					  n2g,
					  edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			    e = buildEdge(n1,
					  n2p,
					  interactionProteinID,
					  dataset);
			    graph.addEdge(e,
					  n1,
					  n2p,
					  edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}

			// add approprate TAP bait properties
			if (n2g != null)
			    detectTAPBait(n2g, e);
			if (n2p != null)
			    detectTAPBait(n2p, e);
		    }
		}
		else {
		    // query is a complex
		    int complexID = StringUtil.atoi(entity.getId(),
						    CLUSTER_PPI_ID_PREFIX.length());
		    stmt = PPI.prepareStatement(con,
						"select f.id, f.feature_id, f.protein_id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id and i.id=?");
		    stmt.setInt(1, datasetID);
		    stmt.setInt(2, complexID);

		    Node n1 = buildComplexNode(complexID);
		    graph.addVertex(n1);

		    rs = stmt.executeQuery();
		    while (rs.next()) {
			int interactionProteinID = rs.getInt(1);
			String featureID = rs.getString(2);
			String proteinID = rs.getString(3);

			if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			    Node n2g = buildNode(featureID,
						 null,
						 NodeType.GENE);
			    graph.addVertex(n2g);
				
			    Edge e = buildEdge(n1,
					       n2g,
					       interactionProteinID,
					       dataset);
			    graph.addEdge(e,
					  n1,
					  n2g,
					  edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			    detectTAPBait(n2g, e);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			    Node n2p = buildNode(featureID,
						 proteinID,
						 NodeType.PROTEIN);
			    graph.addVertex(n2p);

			    Edge e = buildEdge(n1,
					       n2p,
					       interactionProteinID,
					       dataset);
			    graph.addEdge(e,
					  n1,
					  n2p,
					  edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);

			    detectTAPBait(n2p, e);
			}
		    }
		}

		rs.close();
		stmt.close();
		con.close();
	    }
	    if (edgeTypes.contains(EdgeType.GENE_GENE)
		|| edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {

		if (queryType == EntityType.GENE) {
		    // get all proteins in same complex as query
		    stmt = PPI.prepareStatement(con,
						"select i.id, f1.feature_id, f1.protein_id, f1.id from interaction i, interaction_protein f1, interaction_protein f2 where i.interaction_dataset_id=? and f1.interaction_id=i.id and f2.interaction_id=i.id and f2.feature_id=? order by i.id asc, f1.rank asc");
		    stmt.setInt(1, datasetID);
		    stmt.setString(2, entity.getId());
		}
		else if (queryType == EntityType.PROTEIN) {
		    // get all proteins in same complex as query
		    stmt = PPI.prepareStatement(con,
						"select i.id, f1.feature_id, f1.protein_id, f1.id from interaction i, interaction_protein f1, interaction_protein f2 where i.interaction_dataset_id=? and f1.interaction_id=i.id and f2.interaction_id=i.id and f2.feature_id=? and f2.protein_id=? order by i.id asc, f1.rank asc");
		    stmt.setInt(1, datasetID);
		    String[] ids = parseProteinID(entity.getId());
		    stmt.setString(2, ids[0]);
		    stmt.setString(3, ids[1]);
		}
		else {
		    // query is a complex
		    int complexID = StringUtil.atoi(entity.getId(),
						    CLUSTER_PPI_ID_PREFIX.length());

		    stmt = PPI.prepareStatement(con,
						"select i.id, f.feature_id, f.protein_id, f.id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id and i.id=? order by f.rank asc");
		    stmt.setInt(1, datasetID);
		    stmt.setInt(2, complexID);
		}

		Vector<Node> nodesInComplexG = new Vector<Node>(); // gene
		Vector<Node> nodesInComplexP = new Vector<Node>(); // protein
		int lastComplexID = 0;

		rs = stmt.executeQuery();
		while (rs.next()) {
		    int complexID = rs.getInt(1);
		    String geneID2 = rs.getString(2);
		    String proteinID2 = rs.getString(3);
		    int interactionProteinID = rs.getInt(4);

		    // if new complex, fully connect all nodes in last complex
		    if (complexID != lastComplexID) {
			if (lastComplexID > 0) {
			    connectAll(nodesInComplexG, graph, dataset);
			    connectAll(nodesInComplexP, graph, dataset);
			    nodesInComplexG.clear();
			    nodesInComplexP.clear();
			}
			lastComplexID = complexID;
		    }

		    Node node;
		    if (edgeTypes.contains(EdgeType.GENE_GENE)) {
			node = buildNode(geneID2,
					 null,
					 NodeType.GENE);
			node.addProperty("interaction_protein_id", ""+interactionProteinID);
			graph.addVertex(node);
			nodesInComplexG.add(node);
		    }
		    if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
			node = buildNode(geneID2,
					 proteinID2,
					 NodeType.PROTEIN);
			node.addProperty("interaction_protein_id", ""+interactionProteinID);
			graph.addVertex(node);
			nodesInComplexP.add(node);
		    }
		}
		rs.close();
		stmt.close();
		con.close();
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
     * build internal network with default edge types
     */
    @Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
	throws AdaptorException {
	return buildInternalNetwork(dataset, entities, DEFAULT_EDGE_TYPES);
    }

    /**
     * build internal network with specified edge types. fixme: inefficient;
     * builds entire network first then subtracts
     */
    @Override
	public Network buildInternalNetwork(Dataset dataset,
					    List<Entity> entities,
					    List<EdgeType> edgeTypes) throws AdaptorException {

	List<String> geneIDs = Entity.toEntityIds(entities);
			
	Network network = buildNetwork(dataset, edgeTypes);
	Graph<Node, Edge> graph = network.getGraph();
	Collection<Node> allNodes = graph.getVertices();
	Vector<Node> v = new Vector<Node>(allNodes);
	for (Node n : v) {
	    if (!geneIDs.contains(n.getName()))
		graph.removeVertex(n); // also removes appropriate edges
	}
	return network;
    }

    /**
     * build complete network with default edge types
     */
    @Override
	public Network buildNetwork(Dataset dataset) throws AdaptorException {

	return buildNetwork(dataset, DEFAULT_EDGE_TYPES);
    }

    /**
     * build complete network with specified edge types
     */
    @Override
	public Network buildNetwork(Dataset dataset,
				    List<EdgeType> edgeTypes)
	throws AdaptorException {

	Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
	Network network = new Network(IdGenerator.Network.nextId(), "", graph);

	String kbID = dataset.getId();
	int datasetID = Integer.parseInt(IdGenerator.toLocalId(kbID));

	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();

	    // get all complexes / proteins in this dataset
	    PreparedStatement stmt = PPI.prepareStatement(con,
							  "select i.id, i.description, f.feature_id, f.protein_id, f.id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id order by i.id asc, f.rank asc");
	    stmt.setInt(1, datasetID);

	    Vector<Node> nodesInComplexG = new Vector<Node>(); // gene
	    Vector<Node> nodesInComplexP = new Vector<Node>(); // protein
	    int lastComplexID = 0;
	    Node lastComplexNode = null;

	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
		int complexID = rs.getInt(1);
		String complexDescription = rs.getString(2);
		String geneID = rs.getString(3);
		String proteinID = rs.getString(4);
		int interactionProteinID = rs.getInt(5);

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

		    if (edgeTypes.contains(EdgeType.GENE_CLUSTER)
			|| edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
			lastComplexNode = buildComplexNode(complexID);
		    }
		}

		Node node;
		if ((edgeTypes.contains(EdgeType.GENE_GENE))
		    || (edgeTypes.contains(EdgeType.GENE_CLUSTER))) {
		    node = buildNode(geneID,
				     null,
				     NodeType.GENE);
		    node.addProperty("interaction_protein_id", ""+interactionProteinID);
		    graph.addVertex(node);
		    nodesInComplexG.add(node);
		}
		if ((edgeTypes.contains(EdgeType.PROTEIN_PROTEIN))
		    || (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER))) {
		    node = buildNode(geneID,
				     proteinID,
				     NodeType.PROTEIN);
		    node.addProperty("interaction_protein_id", ""+interactionProteinID);
		    graph.addVertex(node);
		    nodesInComplexP.add(node);
		}
	    }

	    rs.close();
	    stmt.close();
	    con.close();

	    // connect last complex
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
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}

	return network;
    }

    /**
     * make a PPI dataset into a Dataset object
     */
    private Dataset buildDataset(int datasetID) throws AdaptorException {
	Dataset rv = null;
	try {
	    String kbID = IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX,""+datasetID);

	    // try to find it in preloaded set
	    if (id2datasetHash.contains(kbID))
		return id2datasetHash.get(kbID);

	    // otherwise build it from database
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    ResultSet rs;

	    rs = stmt.executeQuery("select description, data_source, data_url from interaction_dataset where id="+datasetID);
	    if (!rs.next()) {
		rs.close();
		stmt.close();
		con.close();
		throw new Exception("PPI dataset not found: "+datasetID);
	    }
	    String datasetName = rs.getString(1);
	    DatasetSource datasetSource = DatasetSource.PPI;
	    String datasetSourceName = rs.getString(2);
	    if (!rs.wasNull()) {
		if (datasetSourceName.equals("MO"))
		    datasetSource = DatasetSource.MO;
		else if (datasetSourceName.equals("EcoCyc"))
		    datasetSource = DatasetSource.ECOCYC;
	    }
	    String url = rs.getString(3);
	    rs.close();

	    Vector<Taxon> taxons = new Vector<Taxon>();
	    rs = stmt.executeQuery("select genome_id from interaction_dataset_genome where interaction_dataset_id="+datasetID);
	    while (rs.next())
		taxons.add(new Taxon(rs.getString(1)));
	    rs.close();
	    stmt.close();
	    con.close();

	    rv = new Dataset(kbID,
			     datasetName,
			     "PPI network: "+datasetName,
			     NetworkType.PROT_PROT_INTERACTION,
			     datasetSource,
			     taxons);
	    rv.addProperty("interaction_dataset_id", "" + datasetID);
	    rv.addProperty("description", datasetName);
	    if (datasetSourceName != null)
		rv.addProperty("source", datasetSourceName);
	    if (url != null)
		rv.addProperty("url", url);
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
     * make a node representing a protein complex
     */
    private Node buildComplexNode(int interactionID) throws AdaptorException {
	Node rv = null;
	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    ResultSet rs;

	    rs = stmt.executeQuery("select i.interaction_dataset_id, i.description, i.is_directional, i.confidence, m.description, i.data_url, i.citation_id from interaction i left join interaction_detection_type m on m.id=i.detection_method_id where i.id="+interactionID);
	    if (!rs.next()) {
		stmt.close();
		con.close();
		throw new Exception("Interaction not found: "+ interactionID);
	    }
	    int datasetID = rs.getInt(1);
	    String interactionName = rs.getString(2);
	    boolean isDirectional = (rs.getInt(3) == 1);
	    double conf = rs.getDouble(4);
	    if (rs.wasNull())
		conf = Double.NaN;
	    String method = rs.getString(5);
	    String url = rs.getString(6);
	    String citationID = rs.getString(7);
	    rs.close();
	    stmt.close();
	    con.close();

	    rv = Node.buildClusterNode(IdGenerator.Node.nextId(),
				       "complex "+interactionName,
				       new Entity(CLUSTER_PPI_ID_PREFIX+interactionID,
						  EntityType.PPI_COMPLEX));
	    rv.addProperty("interaction_dataset_id", ""+datasetID);
	    rv.addProperty("interaction_id", ""+interactionID);
	    rv.addProperty("description", ""+interactionName);
	    if (isDirectional)
		rv.addProperty("is_directional", "1");
	    if (!Double.isNaN(conf))
		rv.addProperty("confidence", ""+conf);
	    if (method != null)
		rv.addProperty("detection_method", method);
	    if (url != null)
		rv.addProperty("url", url);
	    if (citationID != null)
		rv.addProperty("citation_id", citationID);
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
     * make a node representing a protein or gene.  proteinID is null
     * for genes, and contains the KBase MD5 identifier for proteins.
     * featureID is required for both.
     */
    private Node buildNode(String featureID,
			   String proteinID,
			   NodeType nodeType) {
	Node rv;
	if (nodeType == NodeType.GENE) {
	    rv = Node.buildGeneNode(IdGenerator.Node.nextId(),
				    featureID,
				    new Entity(featureID,
					       EntityType.GENE));
	}
	else {
	    rv = Node.buildProteinNode(IdGenerator.Node.nextId(),
				       featureID+"|"+proteinID,
				       new Entity(featureID+"|"+proteinID,
						  EntityType.PROTEIN));
	}

	return rv;
    }

    /**
     * make an edge between 2 nodes. interactionProteinID refers to the 2nd
     * node.
     */
    private Edge buildEdge(Node n1,
			   Node n2,
			   int interactionProteinID,
			   Dataset d)
	throws AdaptorException {
	Edge rv = null;
	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    ResultSet rs;

	    rs = stmt.executeQuery("select i.interaction_dataset_id, i.description, i.is_directional, i.confidence, m.description, i.data_url, i.citation_id, f.stoichiometry, f.strength, f.rank, i.id from interaction_protein f, interaction i left join interaction_detection_type m on m.id=i.detection_method_id where f.interaction_id=i.id and f.id="+interactionProteinID);
	    if (!rs.next()) {
		stmt.close();
		con.close();
		throw new Exception("Interaction_protein not found: "+interactionProteinID);
	    }
	    int datasetID = rs.getInt(1);
	    String interactionName = rs.getString(2);
	    boolean isDirectional = (rs.getInt(3) == 1);
	    double conf = rs.getDouble(4);
	    if (rs.wasNull())
		conf = Double.NaN;
	    String method = rs.getString(5);
	    String url = rs.getString(6);
	    String citationID = rs.getString(7);
	    int stoichiometry = rs.getInt(8);
	    double strength = rs.getDouble(9);
	    if (rs.wasNull())
		strength = Double.NaN;
	    int rank = rs.getInt(10);
	    int interactionID = rs.getInt(11);
	    rs.close();

	    rv = new Edge(IdGenerator.Edge.nextId(),
			  n1.getName()+"_"+n2.getName(),
			  d);

	    rv.addProperty("interaction_dataset_id", ""+datasetID);
	    rv.addProperty("interaction_id", ""+interactionID);
	    rv.addProperty("interaction_protein_id", ""+interactionProteinID);
	    rv.addProperty("description", ""+interactionName);
	    if (isDirectional)
		rv.addProperty("is_directional", "1");
	    if (!Double.isNaN(conf)) {
		rv.addProperty("confidence", ""+conf);
		rv.setConfidence((float) conf);
	    }
	    if (method != null)
		rv.addProperty("detection_method", method);
	    if (url != null)
		rv.addProperty("url", url);
	    if (citationID != null)
		rv.addProperty("citation_id", citationID);
	    if (stoichiometry > 0)
		rv.addProperty("stoichiometry", ""+stoichiometry);
	    if (!Double.isNaN(strength)) {
		rv.addProperty("strength", ""+strength);
		rv.setStrength((float) strength);
	    }
	    if (rank > 0)
		rv.addProperty("rank", ""+rank);

	    rs = stmt.executeQuery("select description, data from interaction_data where interaction_protein_id="+interactionProteinID);
	    while (rs.next())
		rv.addProperty("data_"+rs.getString(1),
			       rs.getString(2));
	    rs.close();
	    stmt.close();
	    con.close();
	}
	catch (Exception e) {
	    throw new AdaptorException(e.getMessage());
	}
	return rv;
    }

    /**
     * connect all nodes in a complex to each other, given a list.
     */
    private void connectAll(List<Node> nodes,
			    Graph<Node, Edge> g,
			    Dataset d)
	throws AdaptorException {

	int n = nodes.size();
	for (int i = 0; i < n; i++) {
	    Node n1 = nodes.get(i);
	    int interactionProteinID = StringUtil.atoi(n1.getProperty("interaction_protein_id"));

	    // dummy edge, in order to find out node properties:
	    Edge e = buildEdge(n1,
			       n1,
			       interactionProteinID,
			       d);

	    for (String propertyName : e.getPropertyNames())
		n1.addProperty(propertyName, e.getProperty(propertyName));

	    detectTAPBait(n1, e);

	    for (int j=i+1; j<n; j++) {
		Node n2 = nodes.get(j);

		interactionProteinID = StringUtil.atoi(n2.getProperty("interaction_protein_id"));

		// real edge between the 2 nodes
		e = buildEdge(n1,
			      n2,
			      interactionProteinID,
			      d);

		g.addEdge(e,
			  n1,
			  n2,
			  (e.getProperty("is_directed") == null ? edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED : edu.uci.ics.jung.graph.util.EdgeType.DIRECTED));
	    }
	}
    }

    /**
     * connect nodes all in a complex, given a list, to a complex node
     */
    private void connectAll(List<Node> nodes,
			    Node complexNode,
			    Graph<Node,
			    Edge> g,
			    Dataset d) throws AdaptorException {
	for (Node n : nodes) {
	    int interactionProteinID = StringUtil.atoi(n.getProperty("interaction_protein_id"));

	    Edge e = buildEdge(complexNode,
			       n,
			       interactionProteinID,
			       d);

	    for (String propertyName : e.getPropertyNames())
		n.addProperty(propertyName, e.getProperty(propertyName));

	    g.addEdge(e,
		      complexNode,
		      n,		     
		      edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);

	    detectTAPBait(n, e);
	}
    }

    /**
       set "is_bait" property on a node appropriately based on whether a
       node is TAP bait in a complex.  Note that this is a node property,
       so it generally indicates whether a node has been tested as a
       TAP bait in this dataset.       
    */
    public void detectTAPBait(Node n,
			      Edge e) {
	// check whether node is a TAP bait
	String rank = e.getProperty("rank");
	String method = e.getProperty("detection_method");
	if ((rank != null) &&
	    (method != null) &&
	    (method.equals("TAP")) &&
	    (rank.equals("1"))) {
	    String datasetID = e.getProperty("interaction_dataset_id");
	    n.addProperty("is_bait","1");
	    if (datasetID != null)
		n.addProperty("is_bait_dataset_"+datasetID,"1");
	    String interactionID = e.getProperty("interaction_id");
	    if (interactionID != null)
		n.addProperty("is_bait_interaction_"+interactionID,"1");
	}
    }

    /**
       parse out protein, feature ids
    */
    public String[] parseProteinID(String mixedID) throws Exception {
	String[] rv = new String[2];
	String[] ids = mixedID.split("|");
	if (ids.length != 3)
	    throw new AdaptorException("Malformed query id; protein id must be specified as featureID|MD5");
	rv[0] = ids[0]+"|"+ids[1]; // feature ID
	rv[1] = ids[2]; // protein ID
	return rv;
    }
}
