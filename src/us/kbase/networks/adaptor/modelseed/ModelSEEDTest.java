package us.kbase.networks.adaptor.modelseed;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;

public class ModelSEEDTest {
	Adaptor adaptor;
	
	@Before
	public void setUp() throws Exception {
		adaptor = new ModelSEEDAdaptorFactory().buildAdaptor();
	}

	@Test
	public void shouldReturnListOfDatasets() throws AdaptorException {
		assertNotNull("should return a list of Datasets", adaptor.getDatasets());
	}
	
	@Test
	public void shouldReturnDataSetForEcoli() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.0");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_PATHWAY, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		for (Dataset dataset : datasets) {
			assertTrue("dataset taxons should contain kb|g.0 for E. coli", dataset.getTaxons().contains(ecoli));
		}
		
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGene() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.0");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_PATHWAY, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), "kb|g.0.peg.10");
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 2 edges", g.getEdgeCount(), 2);
		assertEquals("Graph should have 3 nodes", g.getVertexCount(), 3);
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGenes() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.0");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_PATHWAY, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildInternalNetwork(datasets.get(0), Arrays.asList("kb|g.0.peg.10", "kb|g.0.peg.1032", "kb|g.0.peg.1002", "kb|g.0.peg.880", "kb|g.0.peg.847", "kb|g.0.peg.843", "kb|g.0.peg.1247"));
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 16 edges", g.getEdgeCount(), 16);
		assertEquals("Graph should have 7 nodes", g.getVertexCount(), 7);
	}
}
