package us.kbase.networks.adaptor.regprecise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory;

public class RegPreciseAdaptorTest {

	static Adaptor adaptor;
	
	final Taxon taxon = new Taxon("kb|g.20848");
	
	final String datasetId = "kb|netdataset.regprecise.1";
	final String badDatasetId = "kb|netdataset.regprecise.QQQ";

	final String geneId = "kb|g.20848.CDS.1671";
	final String regulonId = "kb|g.20848.regulon.5527";
	
	
	final List<String> geneIds = Arrays.asList(
			"kb|g.20848.CDS.1454", 
			"kb|g.20848.CDS.868", 
			"kb|g.20848.CDS.1671",
			"kb|g.20848.CDS.2554", 
			"kb|g.20848.CDS.1031");		
	
	final List<String> regulonIds =  Arrays.asList(
			"kb|g.20848.regulon.5895", 
			"kb|g.20848.regulon.6391", 
			"kb|g.20848.regulon.5527", 
			"kb|g.20848.regulon.5959", 
			"kb|g.20848.regulon.5623", 
			"kb|g.20848.regulon.42337", 
			"kb|g.20848.regulon.44741" );
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		adaptor = new GenericAdaptorFactory("regprecise.config").buildAdaptor();
	}

	@Test
	public void testGetNetworkTypes() throws AdaptorException {
		List<NetworkType> networkTypes = adaptor.getNetworkTypes();
		assertNotNull("should return a list of NetworkTypes", networkTypes);
		assertEquals("list should contain exactly one NetworkType", 1, networkTypes.size());
		assertEquals("the NetworkType should be REGULATORY_NETWORK", 
				NetworkType.REGULATORY_NETWORK, networkTypes.get(0));				
	}	
	
	@Test
	public void testGetDatasetSources() throws AdaptorException {
		List<DatasetSource> datasetSources = adaptor.getDatasetSources();
		assertNotNull("should return a list of DatasetSources", datasetSources);
		assertEquals("list should contain exactly one dataset source", 1, datasetSources.size());
		assertEquals("the dataset source should be RegPrecise", 
				DatasetSource.REGPRECISE, datasetSources.get(0) );				
	}
	
	@Test
	public void testGetTaxons() throws AdaptorException {
		List<Taxon> taxons = adaptor.getTaxons();
		assertNotNull("should return a list of Taxons", taxons);
		assertTrue("list should contain > 90 taxons", taxons.size() > 90);
		assertTrue("should contain Shewanella oneidensis MR1 genome", taxons.contains(taxon));
	}

	
	@Test
	public void testGetDatasets() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets();
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertTrue("number of datasets should be >= the numebr of taxons", datasets.size() >= adaptor.getTaxons().size());		
	}

	@Test
	public void testGetDatasetsNetworkType() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.REGULATORY_NETWORK);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertEquals("number of datasets should be equal the number of ALL datasets", 
				adaptor.getDatasets().size(), datasets.size());
		
		for(NetworkType nt: NetworkType.values())
		{
			if(nt == NetworkType.REGULATORY_NETWORK) continue;
			datasets = adaptor.getDatasets(nt);
			assertEquals("list should contain 0 datasets", 0, datasets.size());		
		}
	}

	@Test
	public void testGetDatasetsDatasetSource() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(DatasetSource.REGPRECISE);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertEquals("number of datasets should be equal the number of ALL datasets", 
				adaptor.getDatasets().size(), datasets.size());
		
		for(DatasetSource dss: DatasetSource.values())
		{
			if(dss == DatasetSource.REGPRECISE) continue;
			datasets = adaptor.getDatasets(dss);
			assertEquals("list should contain 0 datasets", 0, datasets.size());		
		}
	}

	@Test
	public void testGetDatasetsTaxon() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(taxon);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		
		for(Dataset ds: datasets)
		{
			assertTrue("dataset should be specific for the query taxon", 
					ds.getTaxons().contains(taxon));
		}
	}

	@Test
	public void testGetDatasetsNetworkTypeDatasetSourceTaxon() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.REGULATORY_NETWORK, DatasetSource.REGPRECISE, taxon);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		
		for(Dataset ds: datasets)
		{
			assertTrue("dataset should specific for the query taxon", 
					ds.getTaxons().contains(taxon));
			assertEquals("the network type of dataset should be REGULATORY_NETWORK", 
					NetworkType.REGULATORY_NETWORK, ds.getNetworkType());
			assertEquals("the dataset source of dataset should be REGPRECISE", 
					DatasetSource.REGPRECISE, ds.getDatasetSource());
		}	
	}

	@Test
	public void testHasDataset() throws AdaptorException {
		boolean flHasDataset = adaptor.hasDataset(datasetId);
		assertTrue("should return true", flHasDataset);
		
		flHasDataset = adaptor.hasDataset(badDatasetId);
		assertFalse("should return false", flHasDataset);
	}

	@Test
	public void testGetDataset() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);
		assertNotNull("should return exactly one dataset", 
				ds);
		assertTrue("the dataset id should be the same as query", 
				ds.getId().equals(datasetId));
		
		ds = adaptor.getDataset(badDatasetId);
		assertNull("should NOT return any datasets", ds);
	}

	@Test
	public void testBuildNetworkDataset() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildNetwork(ds);
		assertEquals("method is not implemented yet; should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
	}

	@Test
	public void testBuildNetworkDatasetListOfEdgeType() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildNetwork(ds, Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("method is not implemented yet; should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetEntity() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);

		Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId));
		assertEquals("network should have 6 nodes", 
				6, network.getGraph().getVertexCount());		
		assertEquals("network should have 5 edges", 
				5, network.getGraph().getEdgeCount());		
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetEntityListOfEdgeType() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);

		Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 6 nodes", 
				6, network.getGraph().getVertexCount());		
		assertEquals("network should have 5 edges", 
				5, network.getGraph().getEdgeCount());		
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 98 nodes", 
				98, network.getGraph().getVertexCount());		
		assertEquals("network should have 123 edges", 
				123, network.getGraph().getEdgeCount());			

		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(regulonId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 31 nodes", 
				31, network.getGraph().getVertexCount());		
		assertEquals("network should have 30 edges", 
				30, network.getGraph().getEdgeCount());
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(regulonId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntity() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);
				
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds));
		assertEquals("network should have 0 nodes since the default edge type is GENE_CLUSTER", 
				0, network.getGraph().getVertexCount());		

		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(regulonIds));
		assertEquals("network should have 0 nodes since the default edge type is GENE_CLUSTER", 
				0, network.getGraph().getVertexCount());		

		List<String> entityIds = new ArrayList<String>();
		entityIds.addAll(geneIds);
		entityIds.addAll(regulonIds);
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds));
		assertEquals("network should have 10 nodes", 
				10, network.getGraph().getVertexCount());		
		assertEquals("network should have 15 edges", 
				15, network.getGraph().getEdgeCount());
		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntityListOfEdgeType() throws AdaptorException {

		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 5 nodes", 
				5, network.getGraph().getVertexCount());		
		assertEquals("network should have 17 edges", 
				17, network.getGraph().getEdgeCount());
				
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		

		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(regulonIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(regulonIds), Arrays.asList(NodeType.EDGE_CLUSTER_CLUSTER));
		assertEquals("not impelemnted yet; network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		

		List<String> entityIds = new ArrayList<String>();
		entityIds.addAll(geneIds);
		entityIds.addAll(regulonIds);
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 10 nodes", 
				10, network.getGraph().getVertexCount());		
		assertEquals("network should have 15 edges", 
				15, network.getGraph().getEdgeCount());
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 5 nodes", 
				5, network.getGraph().getVertexCount());		
		assertEquals("network should have 17 edges", 
				17, network.getGraph().getEdgeCount());

		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER, NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 10 nodes", 
				10, network.getGraph().getVertexCount());		
		assertEquals("network should have 17 edges", 
				32, network.getGraph().getEdgeCount());		
	}

	@Test
	public void testGetDatasetsEntity() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(Entity.toEntity(geneId));
		assertTrue("should return > 0 datasets", datasets.size() > 0);
		for(Dataset ds: datasets)
		{
			assertTrue("should be specific for the kb|g.20848 genome", 
					ds.getTaxons().contains(taxon));
		}	
		
		datasets = adaptor.getDatasets(Entity.toEntity(regulonId));
		assertTrue("should return > 0 datasets", datasets.size() > 0);
		for(Dataset ds: datasets)
		{
			assertTrue("should be specific for the kb|g.20848 genome", 
					ds.getTaxons().contains(taxon));
		}		
	}

}
