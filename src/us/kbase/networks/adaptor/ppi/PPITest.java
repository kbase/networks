package us.kbase.networks.adaptor.ppi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.strbio.util.StringUtil;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;

public class PPITest {
	Adaptor adaptor = null;

	final String ecoliID = "kb|g.21765";

	final Entity atpA = new Entity("kb|g.21765.CDS.3606", EntityType.GENE);
	final Entity atpE = new Entity("kb|g.21765.CDS.3743", EntityType.GENE);

	@Before
	public void setup() throws Exception {
		adaptor = new PPIAdaptorFactory().buildAdaptor();
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
		List<Dataset> datasets = adaptor.getDatasets(
				NetworkType.PROT_PROT_INTERACTION, DatasetSource.ECOCYC, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset",
				datasets.size() > 0);
		for (Dataset dataset : datasets) {
			assertTrue("dataset taxons should contain E. coli", dataset
					.getTaxons().contains(ecoli));
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
			assertTrue("dataset taxons should contain E. coli", dataset
					.getTaxons().contains(ecoli));
		}
	}

	@Test
	public void testEcocycFirstNeighbor() throws AdaptorException {
		Taxon ecoli = new Taxon(ecoliID);
		List<Dataset> datasets = adaptor.getDatasets(
				NetworkType.PROT_PROT_INTERACTION, DatasetSource.ECOCYC, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset",
				datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0),
				atpE, Arrays.asList(EdgeType.PROTEIN_CLUSTER));
		assertNotNull("Should get a network back", network);
		Graph<Node, Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		// should be 3 complexes with atpE, one edge to each complex
		assertEquals("Graph should have 3 edges", 3, g.getEdgeCount());
		assertEquals("Graph should have 4 nodes", 4, g.getVertexCount());
	}

	@Test
	public void testEcocycNetwork() throws AdaptorException {
		Taxon ecoli = new Taxon(ecoliID);
		List<Dataset> datasets = adaptor.getDatasets(
				NetworkType.PROT_PROT_INTERACTION, DatasetSource.ECOCYC, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertEquals("list should contain exactly one dataset", 1, datasets
				.size());
		Network network = adaptor.buildInternalNetwork(datasets.get(0), Arrays
				.asList(atpE, atpA), Arrays.asList(EdgeType.PROTEIN_PROTEIN));
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
		assertEquals("Graph should have 1 edge", 1, g.getEdgeCount());
		assertEquals("Graph should have 2 nodes", 2, g.getVertexCount());
		for (Node n : allNodes) {
			int stoichiometry = StringUtil.atoi(n.getProperty("stoichiometry"));
			String nodeGeneID = n.getName();
			if (atpE.equals(nodeGeneID))
				assertEquals("atpE should have stoichiometry 10", 10,
						stoichiometry);
			else if (atpA.equals(nodeGeneID))
				assertEquals("atpA should have stoichiometry 3", 3,
						stoichiometry);
		}
		for (Edge e : g.getEdges()) {
			assertEquals("Edge should be complex ATPSYN-CPLX", "ATPSYN-CPLX", e
					.getProperty("description"));
		}
	}

	/**
	 * run all tests
	 */
	public static void main(String args[]) {
		org.junit.runner.JUnitCore
				.main("us.kbase.networks.adaptor.ppi.PPITest");
	}
}
