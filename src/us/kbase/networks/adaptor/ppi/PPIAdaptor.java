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
 * @version 1.3, 11/6/12
 * @author JMC
 */
public class PPIAdaptor extends AbstractAdaptor {

	// default edge type for PPI network:
	public static final List<EdgeType> DEFAULT_EDGE_TYPES = Arrays
			.asList(EdgeType.PROTEIN_CLUSTER);

	/*
	 * // default dataset id format for ppi networks: public static final String
	 * DATASET_PPI_ID_PREFIX = DATASET_ID_PREFIX+"ppi.";
	 * 
	 * 
	 * // helper functions copied from RegPreciseAdaptor.java private int
	 * uniqueIndex = 0;
	 */

	// default Cluster node id format
	public static final String CLUSTER_PPI_ID_PREFIX = "kb|ppi.";

	// default Edge id format
	public static final String EDGE_PPI_ID_PREFIX = "kb|ppi.edge.";

	public static final String ADAPTOR_PREFIX = "ppi";

	public PPIAdaptor() throws AdaptorException {
		super(null);
	}

	/*
	 * private String getNodeID() { return NODE_ID_PREFIX + (uniqueIndex++); }
	 * 
	 * private String getEdgeID() { return EDGE_ID_PREFIX + (uniqueIndex++); }
	 * 
	 * private String getNetworkID() { return NETWORK_ID_PREFIX +
	 * (uniqueIndex++); }
	 */

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
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new AdaptorException(e.getMessage());
		}
		return rv;
	}

	/**
	 * get list of all datasets of type PPI, but only if the caller asked for
	 * PPI datasets
	 * 
	 * @Override public List<Dataset> getDatasets(NetworkType networkType)
	 *           throws AdaptorException { List<Dataset> rv = new
	 *           Vector<Dataset>(); if (networkType ==
	 *           NetworkType.PROT_PROT_INTERACTION) rv.addAll(getDatasets());
	 *           return rv; }
	 */

	/**
	 * get PPI datasets of a requested source type. Fixme: this is an
	 * inefficient implementation; should be the other way around.
	 * 
	 * @Override public List<Dataset> getDatasets(DatasetSource datasetSource)
	 *           throws AdaptorException { List<Dataset> rv = new
	 *           Vector<Dataset>(); List<Dataset> allSets = getDatasets(); for
	 *           (Dataset d : allSets) { DatasetSource ds2 =
	 *           d.getDatasetSource(); if (datasetSource==null) { if (ds2==null)
	 *           rv.add(d); } else if (datasetSource.equals(ds2)) rv.add(d); }
	 *           return rv; }
	 */

	/**
	 * get PPI datasets containing a requested taxon type.
	 * 
	 * @Override public List<Dataset> getDatasets(Taxon taxon) throws
	 *           AdaptorException { List<Dataset> rv = new Vector<Dataset>();
	 *           try{ PPI.connect(); Connection con = PPI.getConnection();
	 *           PreparedStatement stmt = PPI.prepareStatement(con,"select distinct(i.interaction_dataset_id) from interaction i, interaction_protein f where f.interaction_id=i.id and f.protein_id like ?"
	 *           );
	 * 
	 *           String genomeID = taxon.getGenomeId();
	 *           stmt.setString(1,genomeID+".%");
	 * 
	 *           ResultSet rs = stmt.executeQuery(); while (rs.next()) { int
	 *           datasetID = rs.getInt(1); Dataset d = buildDataset(datasetID);
	 *           rv.add(d); } rs.close(); stmt.close(); con.close(); } catch
	 *           (Exception e) { throw new AdaptorException(e.getMessage()); }
	 *           return rv; }
	 */

	/**
	 * get PPI datasets containing a requested entity
	 */
	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		List<Dataset> rv = new Vector<Dataset>();
		try {
			PPI.connect();
			Connection con = PPI.getConnection();
			PreparedStatement stmt = PPI
					.prepareStatement(
							con,
							"select distinct(i.interaction_dataset_id) from interaction i, interaction_protein f where f.interaction_id=i.id and f.protein_id=?");

			String proteinID = entity.getId();
			stmt.setString(1, proteinID);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int datasetID = rs.getInt(1);
				Dataset d = buildDataset(datasetID);
				rv.add(d);
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage());
		}
		return rv;
	}

	/**
	 * get PPI datasets of a requested network, source, and taxon. Fixme: this
	 * could be implemented more efficiently
	 * 
	 * @Override public List<Dataset> getDatasets(NetworkType networkType,
	 *           DatasetSource datasetSource, Taxon taxon) throws
	 *           AdaptorException { List<Dataset> rv = new Vector<Dataset>(); if
	 *           (networkType != NetworkType.PROT_PROT_INTERACTION) return rv;
	 *           List<Dataset> allSets = getDatasets(datasetSource); for
	 *           (Dataset d : allSets) { if (d.getTaxons().contains(taxon))
	 *           rv.add(d); } return rv; }
	 */

	/**
	 * build 1st neighbor dataset with default edge types
	 */
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity)
			throws AdaptorException {

		return buildFirstNeighborNetwork(dataset, entity, DEFAULT_EDGE_TYPES);
	}

	/**
	 * build 1st neighbor dataset with specified edge types
	 */
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity,
			List<EdgeType> edgeTypes) throws AdaptorException {

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);

		String kbID = dataset.getId();
		int datasetID = Integer.parseInt(IdGenerator.toLocalId(kbID));

		/*
		 * if (kbID.startsWith(DATASET_PPI_ID_PREFIX)) { int pos =
		 * DATASET_PPI_ID_PREFIX.length(); datasetID =
		 * StringUtil.atoi(kbID,pos); } if (datasetID==0) return network; // or
		 * should I throw an exception?
		 */

		try {
			PPI.connect();
			Connection con = PPI.getConnection();

			// for CLUSTER edges, just need complex that gene is in
			if (edgeTypes.contains(EdgeType.GENE_CLUSTER)
					|| edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
				PreparedStatement stmt = PPI
						.prepareStatement(
								con,
								"select i.id, f.id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id and f.protein_id=?");

				stmt.setInt(1, datasetID);
				stmt.setString(2, entity.getId());

				Node n2g = null; // representing query as gene
				Node n2p = null; // representing query as protein

				if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
					n2g = buildNode(entity.getId(), NodeType.GENE);
					graph.addVertex(n2g);
				}
				if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
					n2p = buildNode(entity.getId(), NodeType.PROTEIN);
					graph.addVertex(n2p);
				}

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					int complexID = rs.getInt(1);
					int interactionProteinID = rs.getInt(2);

					Node n1 = buildComplexNode(complexID);
					graph.addVertex(n1);

					if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
						Edge e = buildEdge(n1, n2g, interactionProteinID,
								dataset);
						graph.addEdge(e, n1, n2g,
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
					if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
						Edge e = buildEdge(n1, n2p, interactionProteinID,
								dataset);
						graph.addEdge(e, n1, n2p,
								edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
					}
				}
				rs.close();
				stmt.close();
				con.close();
			}
			if (edgeTypes.contains(EdgeType.GENE_GENE)
					|| edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
				// get all proteins in same complex as query
				PreparedStatement stmt = PPI
						.prepareStatement(
								con,
								"select i.id, f1.protein_id, f1.id from interaction i, interaction_protein f1, interaction_protein f2 where i.interaction_dataset_id=? and f1.interaction_id=i.id and f2.interaction_id=i.id and f2.protein_id=? order by i.id asc, f1.rank asc");
				stmt.setInt(1, datasetID);
				stmt.setString(2, entity.getId());

				Vector<Node> nodesInComplexG = new Vector<Node>(); // gene
				Vector<Node> nodesInComplexP = new Vector<Node>(); // protein
				int lastComplexID = 0;

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					int complexID = rs.getInt(1);
					String geneID2 = rs.getString(2);
					int interactionProteinID = rs.getInt(3);

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
						node = buildNode(geneID2, NodeType.GENE);
						node.addProperty("interaction_protein_id", ""
								+ interactionProteinID);
						graph.addVertex(node);
					}
					if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
						node = buildNode(geneID2, NodeType.PROTEIN);
						node.addProperty("interaction_protein_id", ""
								+ interactionProteinID);
						graph.addVertex(node);
					}

				}
				rs.close();
				stmt.close();
				con.close();
				connectAll(nodesInComplexG, graph, dataset);
				connectAll(nodesInComplexP, graph, dataset);
			}
		} catch (Exception e) {
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
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
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
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes)
			throws AdaptorException {

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);

		String kbID = dataset.getId();
		int datasetID = Integer.parseInt(IdGenerator.toLocalId(kbID));

		/*
		 * int datasetID = 0; if (kbID.startsWith(DATASET_PPI_ID_PREFIX)) { int
		 * pos = DATASET_PPI_ID_PREFIX.length(); datasetID =
		 * StringUtil.atoi(kbID,pos); } if (datasetID==0) return network; // or
		 * should I throw an exception?
		 */

		try {
			PPI.connect();
			Connection con = PPI.getConnection();

			// get all complexes / proteins in this dataset
			PreparedStatement stmt = PPI
					.prepareStatement(
							con,
							"select i.id, i.description, f.protein_id, f.id from interaction i, interaction_protein f where i.interaction_dataset_id=? and f.interaction_id=i.id order by i.id asc, f.rank asc");
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
				int interactionProteinID = rs.getInt(4);

				// if new complex, process all of last complex
				if (complexID != lastComplexID) {
					if (lastComplexID > 0) {
						if (edgeTypes.contains(EdgeType.GENE_GENE)) {
							connectAll(nodesInComplexG, graph, dataset);
						}
						if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
							connectAll(nodesInComplexP, graph, dataset);
						}
						if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
							connectAll(nodesInComplexG, lastComplexNode, graph,
									dataset);
						}
						if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
							connectAll(nodesInComplexP, lastComplexNode, graph,
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
					node = buildNode(geneID, NodeType.GENE);
					node.addProperty("interaction_protein_id", ""
							+ interactionProteinID);
					graph.addVertex(node);
					nodesInComplexG.add(node);
				}
				if ((edgeTypes.contains(EdgeType.PROTEIN_PROTEIN))
						|| (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER))) {
					node = buildNode(geneID, NodeType.PROTEIN);
					node.addProperty("interaction_protein_id", ""
							+ interactionProteinID);
					graph.addVertex(node);
					nodesInComplexP.add(node);
				}
			}

			rs.close();
			stmt.close();
			con.close();

			// connect last complex
			if (edgeTypes.contains(EdgeType.GENE_GENE)) {
				connectAll(nodesInComplexG, graph, dataset);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_PROTEIN)) {
				connectAll(nodesInComplexP, graph, dataset);
			}
			if (edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
				connectAll(nodesInComplexG, lastComplexNode, graph, dataset);
			}
			if (edgeTypes.contains(EdgeType.PROTEIN_CLUSTER)) {
				connectAll(nodesInComplexP, lastComplexNode, graph, dataset);
			}
		} catch (Exception e) {
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
			PPI.connect();
			Connection con = PPI.getConnection();
			Statement stmt = PPI.createStatement(con);
			ResultSet rs;

			rs = stmt
					.executeQuery("select description, data_source, data_url from interaction_dataset where id="
							+ datasetID);
			if (!rs.next()) {
				rs.close();
				stmt.close();
				con.close();
				throw new Exception("PPI dataset not found: " + datasetID);
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
			rs = stmt
					.executeQuery("select distinct(substring_index(f.protein_id,'.',2)) from interaction_protein f, interaction i where f.interaction_id=i.id and i.interaction_dataset_id="
							+ datasetID);
			while (rs.next())
				taxons.add(new Taxon(rs.getString(1)));
			rs.close();
			stmt.close();
			con.close();

			rv = new Dataset(IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX, ""
					+ datasetID), datasetName, "PPI network: " + datasetName,
					NetworkType.PROT_PROT_INTERACTION, datasetSource, taxons);
			rv.addProperty("interaction_dataset_id", "" + datasetID);
			rv.addProperty("description", datasetName);
			if (datasetSourceName != null)
				rv.addProperty("source", datasetSourceName);
			if (url != null)
				rv.addProperty("url", url);
		} catch (Exception e) {
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

			rs = stmt
					.executeQuery("select i.interaction_dataset_id, i.description, i.is_directional, i.confidence, m.description, i.data_url, i.citation_id from interaction i left join interaction_detection_type m on m.id=i.detection_method_id where i.id="
							+ interactionID);
			if (!rs.next()) {
				stmt.close();
				con.close();
				throw new Exception("Interaction not found: " + interactionID);
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

			rv = Node.buildClusterNode(IdGenerator.Node.nextId(), "complex "
					+ interactionName, new Entity(CLUSTER_PPI_ID_PREFIX
					+ interactionID, EntityType.PPI_COMPLEX));
			rv.addProperty("interaction_dataset_id", "" + datasetID);
			rv.addProperty("interaction_id", "" + interactionID);
			rv.addProperty("description", "" + interactionName);
			if (isDirectional)
				rv.addProperty("is_directional", "1");
			if (!Double.isNaN(conf))
				rv.addProperty("confidence", "" + conf);
			if (method != null)
				rv.addProperty("detection_method", method);
			if (url != null)
				rv.addProperty("url", url);
			if (citationID != null)
				rv.addProperty("citation_id", citationID);
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage());
		}
		return rv;
	}

	/**
	 * make a node representing a protein or gene
	 */
	private Node buildNode(String proteinID, NodeType nodeType) {
		Node rv;
		if (nodeType == NodeType.GENE) {
			// fixme: should use API to lookup gene from protein
			rv = Node.buildGeneNode(IdGenerator.Node.nextId(), proteinID,
					new Entity(proteinID, EntityType.PROTEIN));
		} else {
			rv = Node.buildProteinNode(IdGenerator.Node.nextId(), proteinID,
					new Entity(proteinID, EntityType.PROTEIN));
		}

		// todo: add properties, such as aliases
		return rv;
	}

	/**
	 * make an edge between 2 nodes. interactionProteinID refers to the 2nd
	 * node.
	 */
	private Edge buildEdge(Node n1, Node n2, int interactionProteinID, Dataset d)
			throws AdaptorException {
		Edge rv = null;
		try {
			PPI.connect();
			Connection con = PPI.getConnection();
			Statement stmt = PPI.createStatement(con);
			ResultSet rs;

			rs = stmt
					.executeQuery("select i.interaction_dataset_id, i.description, i.is_directional, i.confidence, m.description, i.data_url, i.citation_id, f.stoichiometry, f.strength, f.rank, i.id from interaction_protein f, interaction i left join interaction_detection_type m on m.id=i.detection_method_id where f.interaction_id=i.id and f.id="
							+ interactionProteinID);
			if (!rs.next()) {
				stmt.close();
				con.close();
				throw new Exception("Interaction_protein not found: "
						+ interactionProteinID);
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

			rv = new Edge(IdGenerator.Edge.nextId(), n1.getName() + "_"
					+ n2.getName(), d);

			rv.addProperty("interaction_dataset_id", "" + datasetID);
			rv.addProperty("interaction_id", "" + interactionID);
			rv.addProperty("interaction_protein_id", "" + interactionProteinID);
			rv.addProperty("description", "" + interactionName);
			if (isDirectional)
				rv.addProperty("is_directional", "1");
			if (!Double.isNaN(conf)) {
				rv.addProperty("confidence", "" + conf);
				rv.setConfidence((float) conf);
			}
			if (method != null)
				rv.addProperty("detection_method", method);
			if (url != null)
				rv.addProperty("url", url);
			if (citationID != null)
				rv.addProperty("citation_id", citationID);
			if (stoichiometry > 0)
				rv.addProperty("stoichiometry", "" + stoichiometry);
			if (!Double.isNaN(strength)) {
				rv.addProperty("strength", "" + strength);
				rv.setStrength((float) strength);
			}
			if (rank > 0)
				rv.addProperty("rank", "" + rank);

			rs = stmt
					.executeQuery("select description, data from interaction_data where interaction_protein_id="
							+ interactionProteinID);
			while (rs.next())
				rv.addProperty("data_" + rs.getString(1), rs.getString(2));
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage());
		}
		return rv;
	}

	/**
	 * connect all nodes in a complex to each other, given a list.
	 */
	private void connectAll(List<Node> nodes, Graph<Node, Edge> g, Dataset d)
			throws AdaptorException {

		int n = nodes.size();
		for (int i = 0; i < n; i++) {
			Node n1 = nodes.get(i);
			int interactionProteinID = StringUtil.atoi(n1
					.getProperty("interaction_protein_id"));

			// dummy edge, in order to find out node properties:
			Edge e = buildEdge(n1, n1, interactionProteinID, d);

			for (String propertyName : e.getPropertyNames())
				n1.addProperty(propertyName, e.getProperty(propertyName));

			for (int j = i + 1; j < n; j++) {
				Node n2 = nodes.get(j);

				interactionProteinID = StringUtil.atoi(n2
						.getProperty("interaction_protein_id"));

				// real edge between the 2 nodes
				e = buildEdge(n1, n2, interactionProteinID, d);

				g
						.addEdge(
								e,
								n1,
								n2,
								(e.getProperty("is_directed") == null ? edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED
										: edu.uci.ics.jung.graph.util.EdgeType.DIRECTED));
			}
		}
	}

	/**
	 * connect nodes all in a complex, given a list, to a complex node
	 */
	private void connectAll(List<Node> nodes, Node complexNode,
			Graph<Node, Edge> g, Dataset d) throws AdaptorException {
		for (Node n : nodes) {
			int interactionProteinID = StringUtil.atoi(n
					.getProperty("interaction_protein_id"));

			Edge e = buildEdge(complexNode, n, interactionProteinID, d);

			for (String propertyName : e.getPropertyNames())
				n.addProperty(propertyName, e.getProperty(propertyName));

			g.addEdge(e, complexNode, n,
					edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
		}
	}
}
