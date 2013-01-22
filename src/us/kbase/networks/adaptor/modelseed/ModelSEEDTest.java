package us.kbase.networks.adaptor.modelseed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;

public class ModelSEEDTest {
	Adaptor adaptor;
	
//	final String genomeId = "kb|g.0";
	final String genomeId = "kb|g.21765";
	
//	final String queryGeneId = "kb|g.0.peg.10";
	final Entity queryGene = Entity.toEntity("kb|g.21765.CDS.967"); 
	final Entity querySS = Entity.toEntity("kb|subsystem.Nitrate and nitrite ammonification");
	
//	final List<String> queryGeneIds = Arrays.asList("kb|g.0.peg.10",      "kb|g.0.peg.1032",     "kb|g.0.peg.1002",     "kb|g.0.peg.880", "kb|g.0.peg.847",      "kb|g.0.peg.843",      "kb|g.0.peg.1247");
	final List<Entity> queryGenes = Entity.toEntities( 		
		Arrays.asList(new String[]{
			"kb|g.21765.CDS.967", 
			"kb|g.21765.CDS.2797", 
			"kb|g.21765.CDS.2666",                   
			"kb|g.21765.CDS.1814", 
			"kb|g.21765.CDS.2426", 
			"kb|g.21765.CDS.2043"
		})); 
	
	
	public ModelSEEDTest() throws Exception {
		adaptor = new ModelSEEDAdaptorFactory().buildAdaptor();
	}
	
	
	@Test
	public void shouldReturnDataSetForEcoli() throws AdaptorException {
		Taxon ecoli = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		NetworksUtil.printDatasets("", datasets);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		for (Dataset dataset : datasets) {
			assertTrue("dataset taxons should contain kb|g.0 for E. coli", dataset.getTaxons().contains(ecoli));
		}
		
	}
	
	@Test
	public void shouldReturnDataSetForEcoliGene() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(queryGene);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		for (Dataset dataset : datasets) {
			assertTrue("dataset taxons should contain E. coli", dataset.getTaxons().contains(new Taxon(genomeId)));
		}
		
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGene() throws AdaptorException {
		Taxon ecoli = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), queryGene);
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 2 edges", g.getEdgeCount(), 2);
		assertEquals("Graph should have 3 nodes", g.getVertexCount(), 3);
	}
	
	@Test
	public void shouldReturnNetworkForEcoliSubsystem() throws AdaptorException {
		Taxon ecoli = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), querySS);
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 31 edges", g.getEdgeCount(), 31);
		assertEquals("Graph should have 32 nodes", g.getVertexCount(), 32);
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGenes() throws AdaptorException {
		Taxon ecoli = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildInternalNetwork(datasets.get(0), queryGenes);
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();				
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 11 edges", g.getEdgeCount(), 11);
		assertEquals("Graph should have " + queryGenes.size() + " nodes", g.getVertexCount(), queryGenes.size());
		
	}
}
