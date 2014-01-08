package us.kbase.networks.adaptor.ppi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import us.kbase.networks.core.*;
import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory;
import edu.uci.ics.jung.graph.Graph;

public class PPITest {
    static Adaptor adaptor;

    final String ecoliID = "kb|g.1870";

    // dataset records for Hu 2009 TAP data
    // 1st is ENIGMA dataset; 2nd is MOL dataset
    final String huID1 = "1";
    final String huKBID1 = "kb|netdataset.ppi."+huID1;

    final String huID2 = "7";
    final String huKBID2 = "kb|netdataset.ppi."+huID2;

    final Entity atpA = new Entity("kb|g.1870.peg.3693",
				   EntityType.GENE);
    
    final Entity atpE = new Entity("kb|g.1870.peg.3980",
				   EntityType.GENE);

    final Entity atpSynthase = new Entity("kb|ppi.19381",
					  EntityType.PPI_COMPLEX);
    
    @Before
	public void setup() throws Exception {
    	// adaptor = new PPIAdaptorFactory().buildAdaptor();
	adaptor = new GenericAdaptorFactory("ppi.config").buildAdaptor();	
    }

    @Test
	public void hasAdaptor() throws Exception {
	assertNotNull(adaptor);
    }

    @Test
	public void testDatasets() throws AdaptorException {
	assertNotNull("should return a list of Datasets", adaptor.getDatasets());
    }

    @Test
	public void testDatasetEcocyc() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least one dataset",
		   datasets.size() > 0);
	for (Dataset dataset : datasets) {
	    assertTrue("dataset taxons should contain E. coli",
		       dataset.getTaxons().contains(ecoli));
	}
    }

    @Test
	public void testDatasetEcoliGene() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(atpE);
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least one dataset",
		   datasets.size() > 0);
	for (Dataset dataset : datasets) {
	    assertTrue("dataset taxons should contain E. coli",
		       dataset.getTaxons().contains(ecoli));
	}
    }

    @Test
	public void testEcocycFirstNeighbor() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets",
		      datasets);
	assertTrue("list should contain at least one dataset",
		   datasets.size() > 0);
	Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0),
							    atpE,
							    Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);
	
	// should be 3 complexes with atpE, one edge to each complex
	assertEquals("Graph should have 3 edges",
		     3,
		     g.getEdgeCount());

	// NetworksUtil.printNetwork(network);

	assertEquals("Graph should have 4 nodes",
		     4,
		     g.getVertexCount());

	// should be one node with entity type GENE
	Collection<Node> allNodes = g.getVertices();
	int geneCount = 0;
	Node geneNode = null;
	for (Node n : allNodes) {
	    if (n.getType() == NodeType.GENE) {
		geneCount++;
		geneNode = n;
	    }
	}
	assertEquals("Graph should have 1 gene node",
		     1,
		     geneCount);

	// that node should have incoming edges from every other node
	Collection<Edge> inEdges = g.getInEdges(geneNode);
	assertEquals("Graph should have 3 incoming edges to atpE",
		     3,
		     inEdges.size());

	// no outgoing edges from gene
	Collection<Edge> outEdges = g.getOutEdges(geneNode);
	assertEquals("Graph should have 0 outgoing edges from atpE",
		     0,
		     outEdges.size());

	// check properties of atpE node
	assertNull("atpE should not be a bait in EcoCyc",
		   geneNode.getProperty("is_bait"));

	// incoming edges should have properties matching complex names
	for (Node n : allNodes) {
	    if (n.getType() == NodeType.CLUSTER) {
		String clusterName = n.getName();
		Edge e = g.findEdge(n, geneNode);
		assertNotNull("Needs edge to each complex",
			      e);
		assertEquals("Edge should be named correctly",
			     clusterName+"_"+atpE.getId(),
			     e.getName());
		assertTrue("Edge should NOT have is_directional property (or must be 0)",
			   ((e.getProperty("is_directional") == null) ||
			    (e.getProperty("is_directional").equals("0"))));
		assertEquals("Edge should be directed",
			     edu.uci.ics.jung.graph.util.EdgeType.DIRECTED,
			     g.getEdgeType(e));
		assertEquals("Edge should have stoichiometry 10",
			     "10",
			     e.getProperty("stoichiometry"));
	    }
	}
    }

    @Test
	public void testEcocycNetwork() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertEquals("list should contain exactly one dataset",
		     1,
		     datasets.size());
	Network network = adaptor.buildInternalNetwork(datasets.get(0),
						       Arrays.asList(atpE, atpA),
						       Arrays.asList(NodeType.EDGE_GENE_GENE));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);

	// only complex left should be fragment of atp synthase
	Collection<Node> allNodes = g.getVertices();
	Vector<Node> nodes = new Vector<Node>(allNodes);
	for (Node n : nodes) {
	    if (g.degree(n) == 0)
		g.removeVertex(n);
	}
	assertEquals("Graph should have 1 edge",
		     1,
		     g.getEdgeCount());
	assertEquals("Graph should have 2 nodes",
		     2,
		     g.getVertexCount());
	for (Node n : allNodes) {
	    int stoichiometry = Integer.parseInt(n.getProperty("stoichiometry"));
	    String nodeGeneID = n.getName();
	    if (atpE.equals(nodeGeneID))
		assertEquals("atpE should have stoichiometry 10",
			     10,
			     stoichiometry);
	    else if (atpA.equals(nodeGeneID))
		assertEquals("atpA should have stoichiometry 3",
			     3,
			     stoichiometry);
	}
	for (Edge e : g.getEdges()) {
	    assertEquals("Edge should be complex ATPSYN-CPLX",
			 "ATPSYN-CPLX",
			 e.getProperty("description"));
	}
    }

    @Test
	public void testEcocycNetwork2() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertEquals("list should contain exactly one dataset",
		     1,
		     datasets.size());
	Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0),
							    atpSynthase,
							    Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);
	
	// only complex should be atpsyn
	assertEquals("Graph should have 8 edges",
		     8,
		     g.getEdgeCount());
	assertEquals("Graph should have 9 nodes",
		     9,
		     g.getVertexCount());
	for (Node n : g.getVertices()) {
	    String s = n.getProperty("stoichiometry");
	    int stoichiometry = 0;
	    if (s != null)
		stoichiometry = Integer.parseInt(s); 
	    
	    String nodeGeneID = n.getName();
	    if (atpE.equals(nodeGeneID))
		assertEquals("atpE should have stoichiometry 10",
			     10,
			     stoichiometry);
	    else if (atpA.equals(nodeGeneID))
		assertEquals("atpA should have stoichiometry 3",
			     3,
			     stoichiometry);
	}
	for (Edge e : g.getEdges()) {
	    assertEquals("Edge should be complex ATPSYN-CPLX",
			 "ATPSYN-CPLX",
			 e.getProperty("description"));
	}

	// find all protein-protein connections within ATP Synthase genes
	List<Entity> atpGenes = new ArrayList<Entity>();
	for (Node n: network.getGraph().getVertices()) {
	    if (n.getEntity().getType() == EntityType.GENE)
		atpGenes.add(n.getEntity());
	}
	network = adaptor.buildInternalNetwork(datasets.get(0),
					       atpGenes,
					       Arrays.asList(NodeType.EDGE_GENE_GENE));
	// NetworksUtil.visualizeNetwork(network.getGraph());
	// try {
	// Thread.sleep(100000);
	// }
	// catch (Exception e) {
	// }
	// NetworksUtil.printNetwork(network);
	
	assertNotNull("Should get a network back",
		      network);
	g = network.getGraph();
	assertNotNull("Network should have graph",
		      g);

	// question about how API should work: unique node for each
	// protein, or for each protein/complex?  i.e., if one protein
	// is in 2 different complexes, should it be in the network
	// once or twice?  Old adaptor says twice (20 nodes), new one
	// says once (8 nodes).
	// assertEquals("Graph should have 20 nodes",
	// 20,
	// g.getVertexCount());

	assertEquals("Graph should have 41 edges",
		     41,
		     g.getEdgeCount());
	
	for (Node n : g.getVertices()) {
	    int stoichiometry = Integer.parseInt(n.getProperty("stoichiometry")); 
	    String nodeGeneID = n.getName();
	    if (atpE.equals(nodeGeneID))
		assertEquals("atpE should have stoichiometry 10",
			     10,
			     stoichiometry);
	    else if (atpA.equals(nodeGeneID))
		assertEquals("atpA should have stoichiometry 3",
			     3,
			     stoichiometry);
	}
	for (Edge e : g.getEdges())
	    assertTrue("Edge should be complex with name like  *CPLX  ",  
	    		e.getProperty("description").endsWith("CPLX"));
    }


    @Test
	public void testHuTAPENIGMA() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.PPI,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least 1 dataset",
		   datasets.size() >= 1);

	// find Hu dataset
	Dataset huSet = null;
	for (Dataset d : datasets) {
	    if (huKBID1.equals(d.getId()))
		huSet = d;
	}
	assertNotNull("should include Hu 2009 dataset", huSet);
	
	Network network = adaptor.buildFirstNeighborNetwork(huSet,
							    atpA,
							    Arrays.asList(NodeType.EDGE_GENE_GENE));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);

	// NetworksUtil.printNetwork(network);
	
	/*
	  bug in database data; uncomment when reloaded
	  
	assertEquals("Graph should have 15 nodes",
		     15,
		     g.getVertexCount());

	assertEquals("Graph should have 14 edges",
		     14,
		     g.getEdgeCount());
	*/
    }

    @Test
	public void testHuTAPMOL() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.MO,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least 2 datasets",
		   datasets.size() >= 2);

	// find Hu dataset
	Dataset huSet = null;
	for (Dataset d : datasets) {
	    if (huKBID2.equals(d.getId()))
		huSet = d;
	}
	assertNotNull("should include Hu 2009 dataset", huSet);
	
	Network network = adaptor.buildFirstNeighborNetwork(huSet,
							    atpA,
							    Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);

	// NetworksUtil.printNetwork(network);
	
	/*
	  bug in database data; uncomment when reloaded
	  
	assertEquals("Graph should have 15 nodes",
		     15,
		     g.getVertexCount());

	assertEquals("Graph should have 14 edges",
		     14,
		     g.getEdgeCount());
	*/

	// should be one node with entity type GENE
	Collection<Node> allNodes = g.getVertices();
	int geneCount = 0;
	Node geneNode = null;
	for (Node n : allNodes) {
	    if (n.getType() == NodeType.GENE) {
		geneCount++;
		geneNode = n;
	    }
	}
	assertEquals("Graph should have 1 gene node",
		     1,
		     geneCount);

	// that node should have incoming edges from every other node
	Collection<Edge> inEdges = g.getInEdges(geneNode);
	/*
	assertEquals("Graph should have 15 incoming edges to atpA",
		     15,
		     inEdges.size());
	*/

	// no outgoing edges from gene
	Collection<Edge> outEdges = g.getOutEdges(geneNode);
	assertEquals("Graph should have 0 outgoing edges from atpA",
		     0,
		     outEdges.size());

	// check properties of atpA node
	/*
	  not implemented in generic adaptor:
	  
	assertNotNull("atpA should be a bait in TAP",
		      geneNode.getProperty("is_bait"));
	assertNotNull("atpA should be a bait in Hu dataset",
		      geneNode.getProperty("is_bait_dataset_"+huID));
	*/
	
	// check which complexes atpA is a bait in
	for (Node n : allNodes) {
	    if (n.getType() == NodeType.CLUSTER) {
		String clusterName = n.getName();
		Edge e = g.findEdge(n, geneNode);
		assertNotNull("Needs edge to each complex",
			      e);
		assertEquals("Edge should be named correctly",
			     clusterName+"_"+atpA.getId(),
			     e.getName());
		assertNotNull("Edge should have is_directional property",
			      e.getProperty("is_directional"));
		assertTrue("Edge should have is_directional property == 1",
			   e.getProperty("is_directional").equals("1"));
		assertEquals("Edge should be directed",
			     edu.uci.ics.jung.graph.util.EdgeType.DIRECTED,
			     g.getEdgeType(e));
		assertNull("Edge should NOT have stoichiometry",
			   e.getProperty("stoichiometry"));
		assertNotNull("Edge should have rank property",
			      e.getProperty("rank"));
		int rank = Integer.parseInt(e.getProperty("rank"));
		String complexID = e.getProperty("interaction_id");
		assertNotNull("Edge should have interaction_id property",
			      complexID);
		/*
		  not yet implemented in generic adaptor:
	  
		if (rank==1)
		    assertNotNull("atpA should be a bait in complex "+complexID,
				  geneNode.getProperty("is_bait_interaction_"+complexID));
		else 
		    assertNull("atpA should NOT be a bait in complex "+complexID,
			       geneNode.getProperty("is_bait_interaction_"+complexID));
		*/
	    }
	}
    }
    
    /**
     * run all tests
     */
    public static void main(String args[]) {
	org.junit.runner.JUnitCore.main("us.kbase.networks.adaptor.ppi.PPITest");
    }
}
