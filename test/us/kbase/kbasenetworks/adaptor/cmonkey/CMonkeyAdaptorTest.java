package us.kbase.kbasenetworks.adaptor.cmonkey;

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

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.DatasetSource;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.NetworkType;
import us.kbase.kbasenetworks.core.NodeType;
import us.kbase.kbasenetworks.core.Taxon;
import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.jdbc.GenericAdaptorFactory;

public class CMonkeyAdaptorTest {

	static Adaptor adaptor;
	
	final Taxon taxon = new Taxon("kb|g.3562");
	
	final String datasetId = "kb|dataset.cmonkey.2";
	final String badDatasetId = "kb|dataset.cmonkey.QQQ";

	final String geneId = "kb|g.3562.peg.1142";
	final String regulonId = "kb|bicluster.cmonkey.1";
	
	
	final List<String> geneIds = Arrays.asList(
			"kb|g.3562.peg.1593", 
			"kb|g.3562.peg.368", 
			"kb|g.3562.peg.531");		
	
	final List<String> regulonIds =  Arrays.asList(
			"kb|bicluster.cmonkey.1", 
			"kb|bicluster.cmonkey.2" );
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		adaptor = new GenericAdaptorFactory("cmonkey.config").buildAdaptor();
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
				DatasetSource.CMONKEY, datasetSources.get(0) );				
	}
	
	@Test
	public void testGetTaxons() throws AdaptorException {
		List<Taxon> taxons = adaptor.getTaxons();
		assertNotNull("should return a list of Taxons", taxons);
		assertTrue("list should contain > 10 taxons", taxons.size() > 10);
		assertTrue("should contain " + taxon + " genome", taxons.contains(taxon));
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
		List<Dataset> datasets = adaptor.getDatasets(DatasetSource.CMONKEY);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertEquals("number of datasets should be equal the number of ALL datasets", 
				adaptor.getDatasets().size(), datasets.size());
		
		for(DatasetSource dss: DatasetSource.values())
		{
			if(dss == DatasetSource.CMONKEY) continue;
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
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.REGULATORY_NETWORK, DatasetSource.CMONKEY, taxon);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		
		for(Dataset ds: datasets)
		{
			assertTrue("dataset should specific for the query taxon", 
					ds.getTaxons().contains(taxon));
			assertEquals("the network type of dataset should be REGULATORY_NETWORK", 
					NetworkType.REGULATORY_NETWORK, ds.getNetworkType());
			assertEquals("the dataset source of dataset should be CMONKEY", 
					DatasetSource.CMONKEY, ds.getDatasetSource());
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
		assertEquals("network should have 3 nodes", 
				3, network.getGraph().getVertexCount());		
		assertEquals("network should have 2 edges", 
				2, network.getGraph().getEdgeCount());		
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetEntityListOfEdgeType() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);

		Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 3 nodes", 
				3, network.getGraph().getVertexCount());		
		assertEquals("network should have 2 edges", 
				2, network.getGraph().getEdgeCount());		
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 33 nodes", 
				33, network.getGraph().getVertexCount());		
		assertEquals("network should have 32 edges", 
				32, network.getGraph().getEdgeCount());			

		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(regulonId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 6 nodes", 
				6, network.getGraph().getVertexCount());		
		assertEquals("network should have 5 edges", 
				5, network.getGraph().getEdgeCount());
		
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
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 3 edges", 
				3, network.getGraph().getEdgeCount());
		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntityListOfEdgeType() throws AdaptorException {

		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 3 nodes", 
				3, network.getGraph().getVertexCount());		
		assertEquals("network should have 3 edges", 
				3, network.getGraph().getEdgeCount());
				
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
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 3 edges", 
				3, network.getGraph().getEdgeCount());
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 3 nodes", 
				3, network.getGraph().getVertexCount());		
		assertEquals("network should have 3 edges", 
				3, network.getGraph().getEdgeCount());

		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER, NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 6 edges", 
				6, network.getGraph().getEdgeCount());		
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
