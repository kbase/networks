package us.kbase.kbasenetworks.adaptor.cdmi;

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

import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.jdbc.GenericAdaptorFactory;
import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.NodeType;
import us.kbase.kbasenetworks.core.Taxon;

public class CDMAdaprotTest4Regulome {
	static Adaptor adaptor;
	
	final Taxon taxon = new Taxon("kb|g.3574");
	
	final String datasetId = "kb|g.3574.regulome.0";
	final String badDatasetId = "kb|g.3899.nds.abcd";

	final String geneId = "kb|g.3574.peg.200";
	final String clusterId = "kb|g.3574.regulon.56";
	
	final List<String> geneIds = Arrays.asList(
			"kb|g.3574.peg.200", 
			"kb|g.3574.peg.2438", 
			"kb|g.3574.peg.603" 
			);		
	
	final List<String> clusterIds =  Arrays.asList(
			"kb|g.3574.regulon.56", 
			"kb|g.3574.regulon.57");

	final String dataSourceName = "REGPRECISE_CURATED 3.0";
	final String networkTypeName = "REGULATORY_NETWORK";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		adaptor = new GenericAdaptorFactory("cdm_regulome.config").buildAdaptor();
	}

	@Test
	public void testGetDatasetSources() throws AdaptorException {
		List<String> dsSources = adaptor.getDatasetSources();
		assertNotNull("should return a list of DatasetSources", dsSources);
		assertTrue("list should contain srouces", dsSources.size() > 0 );
		assertTrue("should contain " + dataSourceName, dsSources.contains(dataSourceName));
	}
	
	@Test
	public void testGetNetworkTypes() throws AdaptorException {
		List<String> nwTypes = adaptor.getNetworkTypes();
		assertNotNull("should return a list of network types", nwTypes);
		assertTrue("list should contain srouces", nwTypes.size() > 0 );
		assertTrue("should contain " + networkTypeName, nwTypes.contains(networkTypeName ));
	}
	
	@Test
	public void testGetTaxons() throws AdaptorException {
		List<Taxon> taxons = adaptor.getTaxons();
		assertNotNull("should return a list of Taxons", taxons);
		assertTrue("list should contain taxons", taxons.size() > 0 );
		assertTrue("should contain P. tr.", taxons.contains(taxon));
	}

	
	@Test
	public void testGetDatasets() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets();
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);		
		assertTrue("number of datasets should be >= the numebr of taxons", datasets.size() >= adaptor.getTaxons().size());		
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
		assertTrue("method should have bigger than 0 nodes", 
				network.getGraph().getVertexCount()>0);		
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetEntity() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);

		Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId));
		assertTrue("network should have nodes", 
				network.getGraph().getVertexCount()>0);		
		assertTrue("network should have edges", 
				network.getGraph().getEdgeCount()>0);		
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetEntityListOfEdgeType() throws AdaptorException {
		Dataset ds = adaptor.getDataset(datasetId);

		Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);		
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(geneId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);			

		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(clusterId), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);
		
		network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(clusterId), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertEquals("network should have 0 nodes", 
				0, network.getGraph().getVertexCount());		
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfEntityListOfEdgeType() throws AdaptorException {

		Dataset ds = adaptor.getDataset(datasetId);
		
		Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(geneIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		

		List<String> entityIds = new ArrayList<String>();
		entityIds.addAll(geneIds);
		entityIds.addAll(clusterIds);
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);
		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_GENE));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);

		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER, NodeType.EDGE_GENE_GENE));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);		
		network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds), Arrays.asList(NodeType.EDGE_GENE_CLUSTER, NodeType.EDGE_GENE_GENE));
		assertTrue("network should have > 0 nodes", 
				network.getGraph().getVertexCount() > 0);		
		assertTrue("network should have > 0 edges", 
				network.getGraph().getEdgeCount() > 0);		
	}

	@Test
	public void testGetDatasetsEntity() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets(Entity.toEntity(geneId));
		assertTrue("should return > 0 datasets", datasets.size() > 0);
		for(Dataset ds: datasets)
		{
			assertTrue("should be specific for the " + taxon + " genome", 
					ds.getTaxons().contains(taxon));
		}	
		
		datasets = adaptor.getDatasets(Entity.toEntity(clusterId));
		assertTrue("should return > 0 datasets", datasets.size() > 0);
		for(Dataset ds: datasets)
		{
			assertTrue("should be specific for the " + taxon + " genome", 
					ds.getTaxons().contains(taxon));
		}		
	}


}
