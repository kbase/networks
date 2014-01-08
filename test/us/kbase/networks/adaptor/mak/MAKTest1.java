package us.kbase.networks.adaptor.mak;

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

public class MAKTest1 {

	static Adaptor adaptor;
	
	final NetworkType networkType = NetworkType.CO_EXPRESSION;
	final DatasetSource datsetSource = DatasetSource.MAK_BICLUSTER;
	final Taxon taxon = new Taxon("kb|g.20848");
	
	final String datasetId = "kb|dataset.mak2";
	final String badDatasetId = "kb|netdataset.mak.QQQ";

	final String geneId = "kb|g.20848.CDS.1671";
	final String biclusterId = "kb|bicluster.3311";
	
	
	final List<String> geneIds = Arrays.asList(
			"kb|g.20848.CDS.1454", 
			"kb|g.20848.CDS.868", 
			"kb|g.20848.CDS.1671",
			"kb|g.20848.CDS.2554", 
			"kb|g.20848.CDS.1031");		
	
	final List<String> biclusterIds =  Arrays.asList(
			"kb|bicluster.3311", 
			"kb|bicluster.3337");
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		adaptor = new GenericAdaptorFactory("mak.config").buildAdaptor();
	}

	@Test
	public void testGetNetworkTypes() throws AdaptorException {
		List<NetworkType> networkTypes = adaptor.getNetworkTypes();
		assertNotNull("should return a list of NetworkTypes", networkTypes);
		assertEquals("list should contain exactly 2 NetworkTypes", 2, networkTypes.size());
		assertTrue("one of NetworkType should be " + networkType, networkTypes.contains(networkType));				
	}	
	
	@Test
	public void testGetDatasetSources() throws AdaptorException {
		List<DatasetSource> datasetSources = adaptor.getDatasetSources();
		assertNotNull("should return a list of DatasetSources", datasetSources);
		assertEquals("list should contain exactly one dataset source", 1, datasetSources.size());
		assertEquals("the dataset source should be " + datsetSource, 
				datsetSource, datasetSources.get(0) );				
	}
	
	@Test
	public void testGetTaxons() throws AdaptorException {
		List<Taxon> taxons = adaptor.getTaxons();
		assertNotNull("should return a list of Taxons", taxons);
		assertTrue("list should contain 1 taxon", taxons.size() == 1);
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
		List<Dataset> datasets = adaptor.getDatasets(networkType);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertEquals("number of datasets should be equal 1", 
				1, datasets.size());
	}

	@Test
	public void testGetDatasetsDatasetSource() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(datsetSource);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertEquals("number of datasets should be equal the number of ALL datasets", 
				adaptor.getDatasets().size(), datasets.size());
		
		for(DatasetSource dss: DatasetSource.values())
		{
			if(dss == datsetSource) continue;
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
		List<Dataset> datasets = adaptor.getDatasets(networkType, datsetSource, taxon);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		
		for(Dataset ds: datasets)
		{
			assertTrue("dataset should specific for the query taxon", 
					ds.getTaxons().contains(taxon));
			assertEquals("the network type of dataset should be " + networkType, 
					networkType, ds.getNetworkType());
			assertEquals("the dataset source of dataset should be " + datsetSource, 
					datsetSource, ds.getDatasetSource());
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
		assertEquals("network should have 3 edges", 
				2, network.getGraph().getEdgeCount());	
				
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 183 nodes", 
				183, network.getGraph().getVertexCount());		
		assertEquals("network should have 212 edges", 
				212, network.getGraph().getEdgeCount());			

		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(biclusterId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 107 nodes", 
				107, network.getGraph().getVertexCount());		
		assertEquals("network should have 106 edges", 
				106, network.getGraph().getEdgeCount());
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(biclusterId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntity() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);
				
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds));
		assertEquals("network should have 0 nodes since the default edge type is GENE_CLUSTER", 
				0, network.getGraph().getVertexCount());		

		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(biclusterIds));
		assertEquals("network should have 0 nodes since the default edge type is GENE_CLUSTER", 
				0, network.getGraph().getVertexCount());		

		List<String> entityIds = new ArrayList<String>();
		entityIds.addAll(geneIds);
		entityIds.addAll(biclusterIds);
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds));
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 4 edges", 
				4, network.getGraph().getEdgeCount());
		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntityListOfEdgeType() throws AdaptorException {

		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 7 edges", 
				7, network.getGraph().getEdgeCount());
				
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		

		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(biclusterIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(biclusterIds), Arrays.asList(NodeType.EDGE_CLUSTER_CLUSTER));
		assertEquals("not impelemnted yet; network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		

		List<String> entityIds = new ArrayList<String>();
		entityIds.addAll(geneIds);
		entityIds.addAll(biclusterIds);
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 4 edges", 
				4, network.getGraph().getEdgeCount());
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 4 nodes", 
				4, network.getGraph().getVertexCount());		
		assertEquals("network should have 7 edges", 
				7, network.getGraph().getEdgeCount());

		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER, NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 6 nodes", 
				6, network.getGraph().getVertexCount());		
		assertEquals("network should have 11 edges", 
				11, network.getGraph().getEdgeCount());		
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
		
		datasets = adaptor.getDatasets(Entity.toEntity(biclusterId));
		assertTrue("should return > 0 datasets", datasets.size() > 0);
		for(Dataset ds: datasets)
		{
			assertTrue("should be specific for the kb|g.20848 genome", 
					ds.getTaxons().contains(taxon));
		}		
	}

}
