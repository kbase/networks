package us.kbase.networks.adaptor.ppi;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import us.kbase.networks.adaptor.*;
import us.kbase.networks.core.*;
import us.kbase.networks.adaptor.ppi.local.PPI;

import org.strbio.util.*;
import edu.uci.ics.jung.graph.*;

public class PPITest {
    Adaptor adaptor;
    
    final String ecoliID = "kb|g.21765";

    final String atpA = "kb|g.21765.CDS.3606";
    final String atpE = "kb|g.21765.CDS.3743";
    
    @Before
	public void setup() throws Exception {
	adaptor = new PPIAdaptorFactory().buildAdaptor();
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
	assertTrue("list should contain at least one dataset", datasets.size() > 0);
	for (Dataset dataset : datasets) {
	    assertTrue("dataset taxons should contain E. coli", dataset.getTaxons().contains(ecoli));
	}
    }
    
    @Test
	public void testDatasetEcoliGene() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(new Entity(atpE,
								EntityType.PROTEIN));
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least one dataset", datasets.size() > 0);
	for (Dataset dataset : datasets) {
	    assertTrue("dataset taxons should contain E. coli", dataset.getTaxons().contains(ecoli));
	}
    }
	
    @Test
	public void testEcocycFirstNeighbor() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertTrue("list should contain at least one dataset", datasets.size() > 0);
	Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0),
							    atpE,
							    Arrays.asList(EdgeType.PROTEIN_CLUSTER));
	assertNotNull("Should get a network back", network);
	Graph<Node,Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);
	// should be 3 complexes with atpE, one edge to each complex
	assertEquals("Graph should have 3 edges", 3, g.getEdgeCount());
	assertEquals("Graph should have 4 nodes", 4, g.getVertexCount());
    }

    @Test
	public void testEcocycNetwork() throws AdaptorException {
	Taxon ecoli = new Taxon(ecoliID);
	List<Dataset> datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION,
						     DatasetSource.ECOCYC,
						     ecoli);
	assertNotNull("should return a list of Datasets", datasets);
	assertEquals("list should contain exactly one dataset", 1, datasets.size());
	Network network = adaptor.buildInternalNetwork(datasets.get(0),
						       Arrays.asList(atpE, atpA),
						       Arrays.asList(EdgeType.PROTEIN_PROTEIN));
	assertNotNull("Should get a network back", network);
	Graph<Node,Edge> g = network.getGraph();
	assertNotNull("Network should have graph", g);
	// only complex left should be fragment of atp synthase
	Collection<Node> allNodes = g.getVertices();
	Vector<Node> nodes = new Vector<Node>(allNodes);
	for (Node n : nodes) {
	    if (g.degree(n)==0)
		g.removeVertex(n);
	}
	assertEquals("Graph should have 1 edge", 1, g.getEdgeCount());
	assertEquals("Graph should have 2 nodes", 2, g.getVertexCount());
	for (Node n : allNodes) {
	    int stoichiometry = StringUtil.atoi(n.getProperty("stoichiometry"));
	    String nodeGeneID = n.getName();
	    if (atpE.equals(nodeGeneID))
		assertEquals("atpE should have stoichiometry 10", 10, stoichiometry);
	    else if (atpA.equals(nodeGeneID))
		assertEquals("atpA should have stoichiometry 3", 3, stoichiometry);
	}
	for (Edge e : g.getEdges()) {
	    assertEquals("Edge should be complex ATPSYN-CPLX", "ATPSYN-CPLX", e.getProperty("description"));
	}
    }
    
    /**
       run all tests
    */
    public static void main(String args[]) {
	org.junit.runner.JUnitCore.main("us.kbase.networks.adaptor.ppi.PPITest");
    }
}
