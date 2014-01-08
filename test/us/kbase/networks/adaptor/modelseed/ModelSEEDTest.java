package us.kbase.networks.adaptor.modelseed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;
import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;
import edu.uci.ics.jung.graph.Graph;

public class ModelSEEDTest {
	static Adaptor adaptor;
	
	final String genomeId = "kb|g.0";
//	final String genomeId = "kb|g.21765";
	
	final Entity queryGene = Entity.toEntity("kb|g.0.peg.10");
//	final Entity queryGene = Entity.toEntity("kb|g.21765.CDS.967"); 
	final Entity querySS = Entity.toEntity("kb|subsystem.Nitrate and nitrite ammonification");
	
//	final List<String> queryGeneIds = Arrays.asList("kb|g.0.peg.10",      "kb|g.0.peg.1032",     "kb|g.0.peg.1002",     "kb|g.0.peg.880", "kb|g.0.peg.847",      "kb|g.0.peg.843",      "kb|g.0.peg.1247");
	final List<Entity> queryGenes = Entity.toEntities( 		
		Arrays.asList(new String[]{
			"kb|g.0.peg.10",      
			"kb|g.0.peg.1032",     
			"kb|g.0.peg.1002",     
			"kb|g.0.peg.880", 
			"kb|g.0.peg.847",      
			"kb|g.0.peg.843",      
			"kb|g.0.peg.1247"
	})); 

	/*	
	final List<Entity> queryGenes = Entity.toEntities( 		
		Arrays.asList(new String[]{
			"kb|g.21765.CDS.967", 
			"kb|g.21765.CDS.2797", 
			"kb|g.21765.CDS.2666",                   
			"kb|g.21765.CDS.1814", 
			"kb|g.21765.CDS.2426", 
			"kb|g.21765.CDS.2043"
		})); 
*/	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
		assertEquals("Graph should have 1 edge", 1, g.getEdgeCount());
		assertEquals("Graph should have 2 nodes", 2, g.getVertexCount());
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
		assertEquals("Graph should have 5 edges", 5, g.getEdgeCount());
		assertEquals("Graph should have " + queryGenes.size() + " nodes", queryGenes.size(), g.getVertexCount());
		
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
		assertEquals("Graph should have 31 edges", 31, g.getEdgeCount());
		assertEquals("Graph should have 32 nodes", 32, g.getVertexCount());
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGenesInSameSubsystems() throws AdaptorException {
		Taxon ecoli = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), Entity.toEntity("kb|g.0.peg.3750"), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 35 nodes", 35, g.getVertexCount());
	}
	
	@Test
	public void shouldReturnNetworkForEcoliGeneClusterStartWithGene() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.1870");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), Entity.toEntity("kb|g.1870.peg.1847"), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 8 nodes", 8, g.getVertexCount());
	}	
	
	@Test
	public void shouldReturnNetworkForEcoliGeneClusterStartWithSubsystem() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.1870");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), Entity.toEntity("kb|subsystem.Acetyl-CoA fermentation to Butyrate"), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertNotNull("Should get a network back", network);
		Graph<Node,Edge> g = network.getGraph();
		assertNotNull("Network should have graph", g);
		assertEquals("Graph should have 18 nodes", 18, g.getVertexCount());
	}
}
