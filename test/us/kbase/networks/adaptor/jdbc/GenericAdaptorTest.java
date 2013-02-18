package us.kbase.networks.adaptor.jdbc;

import org.apache.commons.configuration.PropertiesConfiguration;

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

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public class GenericAdaptorTest {

  static Adaptor adaptor = null;
  static PropertiesConfiguration conf = null;
  
  static Taxon taxon = null;
  static NetworkType nt = null;
  static EdgeType et = null;
  static DatasetSource dsrc = null;
  static String entityId1 = "";
  static String entityId2 = "";
  
  static String datasetId = "";
  static String badDatasetId = "";

  static String geneId = "kb|g.20848.CDS.1671";
  static String regulonId = "kb|g.20848.regulon.54";
  
  
  static List<String> entityIds1 = null;
  static List<String> entityIds2 = null;

  private static final String TST_CONFIGURATION_FILENAME = "TestConfigFN";
  private static final String ADAPTOR_CONFIG_FILENAME = "AdaptorConfigFN";
  private static final String TST_GET_NETWORK_TYPES_SIZE = "GetNetworkTypes.Size";
  private static final String TST_GET_DATASET_SOURCES_SIZE = "GetDatasetSources.Size";
  private static final String TST_BUILD_FIRST_NEIGHT_V_SIZE = "BuildFirstNeighbor.VSize";
  private static final String TST_BUILD_FIRST_NEIGHT_E_SIZE = "BuildFirstNeighbor.ESize";
  private static final String TST_BUILD_FIRST_NEIGHT_ET1_ET_V_SIZE = "BuildFirstNeighbor.ET1.ET.VSize";
  private static final String TST_BUILD_FIRST_NEIGHT_ET1_ET_E_SIZE = "BuildFirstNeighbor.ET1.ET.ESize";
  private static final String TST_BUILD_FIRST_NEIGHT_ET2_ET_V_SIZE = "BuildFirstNeighbor.ET2.ET.VSize";
  private static final String TST_BUILD_FIRST_NEIGHT_ET2_ET_E_SIZE = "BuildFirstNeighbor.ET2.ET.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET1_V_SIZE = "BuildIntNetwork.QET1.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET2_V_SIZE = "BuildIntNetwork.QET2.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET12_V_SIZE = "BuildIntNetwork.QET12.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET1_E_SIZE = "BuildIntNetwork.QET1.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET2_E_SIZE = "BuildIntNetwork.QET2.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET12_E_SIZE = "BuildIntNetwork.QET12.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET1_ET_V_SIZE = "BuildIntNetwork.QET1.ET.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET2_ET_V_SIZE = "BuildIntNetwork.QET2.ET.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET12_ET_V_SIZE = "BuildIntNetwork.QET12.ET.VSize";
  private static final String TST_BUILD_INT_NETWORK_ET1_ET_E_SIZE = "BuildIntNetwork.QET1.ET.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET2_ET_E_SIZE = "BuildIntNetwork.QET2.ET.ESize";
  private static final String TST_BUILD_INT_NETWORK_ET12_ET_E_SIZE = "BuildIntNetwork.QET12.ET.ESize";

  private static final String TST_NETWORK_TYPE_ID = "NetworkType.ID";
  private static final String TST_DATASET_SOURCE_ID = "DatasetSource.ID";
  private static final String TST_TAXON_ID = "Taxon.ID";
  private static final String TST_GOOD_DATASET_ID = "GoodDataset.ID";
  private static final String TST_BAD_DATASET_ID = "BadDataset.ID";
  private static final String TST_ENTITY_ID1 = "Entity1.ID";
  private static final String TST_ENTITY_ID2 = "Entity2.ID";
  private static final String TST_ENTITY_IDS1 = "Entity1.IDS";
  private static final String TST_ENTITY_IDS2 = "Entity2.IDS";
  private static final String TST_EDGE_TYPE = "EdgeType.ID";
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
	System.out.println(System.getProperty(TST_CONFIGURATION_FILENAME));
    conf = new PropertiesConfiguration(System.getProperty(TST_CONFIGURATION_FILENAME));
    adaptor = new GenericAdaptorFactory(conf.getString(ADAPTOR_CONFIG_FILENAME)).buildAdaptor();
    taxon = new Taxon(conf.getString(TST_TAXON_ID));
    nt = Enum.valueOf(NetworkType.class,conf.getString(TST_NETWORK_TYPE_ID));
    dsrc = Enum.valueOf(DatasetSource.class,conf.getString(TST_DATASET_SOURCE_ID));
    datasetId = conf.getString(TST_GOOD_DATASET_ID);
    badDatasetId = conf.getString(TST_BAD_DATASET_ID, "badid");
    entityId1 = conf.getString(TST_ENTITY_ID1, "");
    entityId2 = conf.getString(TST_ENTITY_ID2, "");
    entityIds1 = Arrays.asList(conf.getStringArray(TST_ENTITY_IDS1));
    entityIds2 = Arrays.asList(conf.getStringArray(TST_ENTITY_IDS2));
    et = Enum.valueOf(EdgeType.class,conf.getString(TST_EDGE_TYPE));
  }

  @Test
  public void testGetNetworkTypes() throws AdaptorException {
    List<NetworkType> networkTypes = adaptor.getNetworkTypes();
    assertNotNull("should return a list of NetworkTypes", networkTypes);
    assertEquals("list should contain exactly "+ conf.getInt(TST_GET_NETWORK_TYPES_SIZE) +  " NetworkType", 1, conf.getInt(TST_GET_NETWORK_TYPES_SIZE));
    assertEquals("the NetworkType should be " + conf.getString(TST_NETWORK_TYPE_ID), 
        nt, networkTypes.get(0));        
  }  
  
  @Test
  public void testGetDatasetSources() throws AdaptorException {
    List<DatasetSource> datasetSources = adaptor.getDatasetSources();
    assertNotNull("should return a list of DatasetSources", datasetSources);
    assertEquals("list should contain exactly " + conf.getInt(TST_GET_DATASET_SOURCES_SIZE) +  " dataset source", conf.getInt(TST_GET_DATASET_SOURCES_SIZE), datasetSources.size());
    
    assertTrue("the dataset source should contain " + dsrc, 
        datasetSources.contains(dsrc) );        
  }
  
  @Test
  public void testGetTaxons() throws AdaptorException {
    List<Taxon> taxons = adaptor.getTaxons();
    assertNotNull("should return a list of Taxons", taxons);
    assertTrue("list should contain at least one taxons", taxons.size() > 0);
    assertTrue("should contain " + taxon, taxons.contains(taxon));
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
    List<Dataset> datasets = adaptor.getDatasets(nt);
    assertNotNull("should return a list of Datasets", datasets);
    assertTrue("list should contain at least one dataset", datasets.size() > 0);    
    assertEquals("number of datasets should be equal the number of ALL datasets", 
        adaptor.getDatasets().size(), datasets.size());
    
    for(NetworkType nti: NetworkType.values())
    {
      if(nti == nt) continue;
      datasets = adaptor.getDatasets(nti);
      assertEquals("list should contain 0 datasets", 0, datasets.size());    
    }
  }

  @Test
  public void testGetDatasetsDatasetSource() throws AdaptorException {
    List<Dataset> datasets = adaptor.getDatasets(dsrc);
    assertNotNull("should return a list of Datasets", datasets);
    assertTrue("list should contain at least one dataset", datasets.size() > 0);
    // not true for FN
    //assertEquals("number of datasets should be equal the number of ALL datasets", 
    //    adaptor.getDatasets().size(), datasets.size());
    
    //for(DatasetSource dss: DatasetSource.values())
    //{
    //  if(dss == dsrc) continue;
    //  datasets = adaptor.getDatasets(dss);
    //  assertEquals("list should contain 0 datasets", 0, datasets.size());    
    //}
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
    List<Dataset> datasets = adaptor.getDatasets(nt, dsrc, taxon);
    assertNotNull("should return a list of Datasets", datasets);
    assertTrue("list should contain at least one dataset", datasets.size() > 0);
    
    for(Dataset ds: datasets)
    {
      assertTrue("dataset should specific for the query taxon", 
          ds.getTaxons().contains(taxon));
      assertEquals("the network type of dataset should be REGULATORY_NETWORK", 
          nt, ds.getNetworkType());
      assertEquals("the dataset source of dataset should be " + dsrc, 
          dsrc, ds.getDatasetSource());
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
    //TODO: Need to be improved
    Dataset ds = adaptor.getDataset(datasetId);
    
    Network network = adaptor.buildNetwork(ds, Arrays.asList(et));
    assertEquals("method is not implemented yet; should have 0 nodes", 
        0, network.getGraph().getVertexCount());    
  }

  @Test
  public void testBuildFirstNeighborNetworkDatasetEntity() throws AdaptorException {
    Dataset ds = adaptor.getDataset(datasetId);

    Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(entityId1));
    assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_V_SIZE)  + " nodes", 
        conf.getInt(TST_BUILD_FIRST_NEIGHT_V_SIZE), network.getGraph().getVertexCount());    
    assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_E_SIZE) + " edges", 
        conf.getInt(TST_BUILD_FIRST_NEIGHT_E_SIZE), network.getGraph().getEdgeCount());    
  }

  @Test
  public void testBuildFirstNeighborNetworkDatasetEntityListOfEdgeType() throws AdaptorException {
    Dataset ds = adaptor.getDataset(datasetId);

    Network network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(entityId1), Arrays.asList(et));
    assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_ET1_ET_V_SIZE)  + " nodes", conf.getInt(TST_BUILD_FIRST_NEIGHT_ET1_ET_V_SIZE), network.getGraph().getVertexCount());
    assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_ET1_ET_E_SIZE) + " edges", conf.getInt(TST_BUILD_FIRST_NEIGHT_ET1_ET_E_SIZE), network.getGraph().getEdgeCount());    

    
    if(!entityId2.equals("")) {
      network = adaptor.buildFirstNeighborNetwork(ds, Entity.toEntity(entityId2), Arrays.asList(et));
      assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_ET2_ET_V_SIZE)  + " nodes", conf.getInt(TST_BUILD_FIRST_NEIGHT_ET2_ET_V_SIZE), network.getGraph().getVertexCount());
      assertEquals("network should have " + conf.getInt(TST_BUILD_FIRST_NEIGHT_ET2_ET_E_SIZE) + " edges", conf.getInt(TST_BUILD_FIRST_NEIGHT_ET2_ET_E_SIZE), network.getGraph().getEdgeCount());
    }
  }

  @Test
  public void testBuildInternalNetworkDatasetListOfEntity() throws AdaptorException {
    Dataset ds = adaptor.getDataset(datasetId);
        
    Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds1));
    assertEquals("the number of network nodes (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET1_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET1_V_SIZE), network.getGraph().getVertexCount());    
    assertEquals("the number of network edges (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET1_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET1_E_SIZE), network.getGraph().getEdgeCount());    

    if(! entityIds2.equals("")){
	    network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds2));
	    assertEquals("the number of network nodes (entityIds2) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET2_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET2_V_SIZE), network.getGraph().getVertexCount());    
	    assertEquals("the number of network edges (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET2_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET2_E_SIZE), network.getGraph().getEdgeCount());    

	    List<String> entityIds = new ArrayList<String>();
	    entityIds.addAll(entityIds1);
	    entityIds.addAll(entityIds2);
	    network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds));
	    assertEquals("the number of network nodes should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET12_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET12_V_SIZE), network.getGraph().getVertexCount());    
	    assertEquals("the number of network edges should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET12_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET12_E_SIZE), network.getGraph().getEdgeCount());    
    }
  }

  @Test
  public void testBuildInternalNetworkDatasetListOfEntityListOfEdgeType() throws AdaptorException {
	    Dataset ds = adaptor.getDataset(datasetId);
        
	    Network network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds1),Arrays.asList(et));
	    assertEquals("the number of network nodes (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET1_ET_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET1_ET_V_SIZE), network.getGraph().getVertexCount());    
	    assertEquals("the number of network edges (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET1_ET_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET1_ET_E_SIZE), network.getGraph().getEdgeCount());    

	    if(! entityIds2.equals("")){
		    network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds2),Arrays.asList(et));
		    assertEquals("the number of network nodes (entityIds2) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET2_ET_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET2_ET_V_SIZE), network.getGraph().getVertexCount());    
		    assertEquals("the number of network edges (entityIds1) should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET2_ET_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET2_ET_E_SIZE), network.getGraph().getEdgeCount());    

		    List<String> entityIds = new ArrayList<String>();
		    entityIds.addAll(entityIds1);
		    entityIds.addAll(entityIds2);
		    network = adaptor.buildInternalNetwork(ds, Entity.toEntities(entityIds),Arrays.asList(et));
		    assertEquals("the number of network nodes should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET12_ET_V_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET12_ET_V_SIZE), network.getGraph().getVertexCount());    
		    assertEquals("the number of network edges should be " + conf.getInt(TST_BUILD_INT_NETWORK_ET12_ET_E_SIZE), conf.getInt(TST_BUILD_INT_NETWORK_ET12_ET_E_SIZE), network.getGraph().getEdgeCount());    
	    }
  }

  @Test
  public void testGetDatasetsEntity() throws AdaptorException {
    List<Dataset> datasets = adaptor.getDatasets(Entity.toEntity(entityId1));
    assertTrue("should return > 0 datasets", datasets.size() > 0);
    for(Dataset ds: datasets)
    {
      assertTrue("should be specific for " + taxon, 
          ds.getTaxons().contains(taxon));
    }    
  }
}
