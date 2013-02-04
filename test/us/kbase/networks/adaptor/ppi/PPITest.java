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
import org.strbio.util.StringUtil;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.*;
import edu.uci.ics.jung.graph.Graph;

public class PPITest {
    static Adaptor adaptor;

    final String ecoliID = "kb|g.1870";

    final Entity atpA = new Entity("kb|g.1870.peg.3693",
				   EntityType.GENE);
    
    final Entity atpE = new Entity("kb|g.1870.peg.3980",
				   EntityType.GENE);

    final Entity atpSynthase = new Entity("kb|ppi.19539",
					  EntityType.PPI_COMPLEX);
    
    @Before
	public void setup() throws Exception {
    	adaptor = new PPIAdaptorFactory().buildAdaptor();
	// adaptor = new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("ppi.config").buildAdaptor();	
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
							    Arrays.asList(EdgeType.GENE_CLUSTER));
	assertNotNull("Should get a network back", network);
	Graph<Node, Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);
	
	// should be 3 complexes with atpE, one edge to each complex
	assertEquals("Graph should have 3 edges",
		     3,
		     g.getEdgeCount());

	NetworksUtil.printNetwork(network);

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
		assertNotNull("Edge should have is_directional property",
			      e.getProperty("is_directional"));
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
						       Arrays.asList(EdgeType.GENE_GENE));
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
	    int stoichiometry = StringUtil.atoi(n.getProperty("stoichiometry"));
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
							    Arrays.asList(EdgeType.GENE_CLUSTER));
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
		stoichiometry = StringUtil.atoi(s);
	    
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
					       Arrays.asList(EdgeType.GENE_GENE));
	// NetworksUtil.visualizeNetwork(network.getGraph());
	// NetworksUtil.printNetwork(network);
	
	assertNotNull("Should get a network back",
		      network);
	g = network.getGraph();
	assertNotNull("Network should have graph",
		      g);

	assertEquals("Graph should have 8 nodes",
		     8,
		     g.getVertexCount());

	assertEquals("Graph should have 41 edges",
		     41,
		     g.getEdgeCount());
	
	for (Node n : g.getVertices()) {
	    int stoichiometry = StringUtil.atoi(n.getProperty("stoichiometry"));
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
    
    /**
     * run all tests
     */
    public static void main(String args[]) {
	org.junit.runner.JUnitCore.main("us.kbase.networks.adaptor.ppi.PPITest");
    }
}
