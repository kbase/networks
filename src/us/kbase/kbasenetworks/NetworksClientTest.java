package us.kbase.kbasenetworks;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.kbase.common.service.JsonClientException;
import us.kbase.kbasenetworks.core.Taxon;

public class NetworksClientTest {

	String url = "http://127.0.0.1:1111";
	//String url = "http://127.0.0.1:7064/KBaseNetworksService";
	
	KBaseNetworksClient networksAPI;

	static class RegulomeData{
		static final Taxon taxon = new Taxon("kb|g.3574");
		
		static final String datasetId = "kb|g.3574.regulome.0";
		static final String badDatasetId = "kb|g.3899.nds.abcd";

		static final String geneId = "kb|g.3574.peg.200";
		static final String clusterId = "kb|g.3574.regulon.56";
		
		static final List<String> geneIds = Arrays.asList(
				"kb|g.3574.peg.200", 
				"kb|g.3574.peg.2438", 
				"kb|g.3574.peg.603" 
				);		
		
		static final List<String> clusterIds =  Arrays.asList(
				"kb|g.3574.regulon.56", 
				"kb|g.3574.regulon.57");

		static final String dataSourceName = "REGPRECISE_CURATED 3.0";
		static final String networkTypeName = "REGULATORY_NETWORK";		
	};
	
	
	
    static class PlantData{
    	static final Taxon taxon = new Taxon("kb|g.3899");
		
		static final String datasetId = "kb|g.3899.nds.12";
		static final String badDatasetId = "kb|g.3899.nds.abcd";

		static final String geneId = "kb|g.3899.CDS.69528";
		static final String clusterId = "kb|g.3899.nds.12.edge.93313";
		
		
		static final List<String> geneIds = Arrays.asList(
				"kb|g.3899.CDS.68927", 
				"kb|g.3899.CDS.43764", 
				"kb|g.3899.CDS.66599",
				"kb|g.3899.CDS.55077", 
				"kb|g.3899.CDS.44955");		
		
		static final List<String> clusterIds =  Arrays.asList(
				"kb|g.3899.nds.12.edge.93313", 
				"kb|g.3899.nds.12.edge.830647", 
				"kb|g.3899.nds.12.edge.831542", 
				"kb|g.3899.nds.12.edge.832046", 
				"kb|g.3899.nds.12.edge.832205" );     		
	};  	

    @Before
    public void setUp() throws Exception {
        networksAPI = new KBaseNetworksClient(new URL(url));
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testAllDatasets() throws Exception {
        System.out.println("testAllDatasets");
        List<Dataset> datasets = networksAPI.allDatasets();
        printDatasets("testAllDatasets", datasets);
    }

    @Test
    public void testAllDatasetSources() throws Exception {
        System.out.println("testAllDatasetSources");
        List<DatasetSource> dsSources = networksAPI.allDatasetSources();
        printDatasetSources("testAllDatasetSources", dsSources);
    }


    @Test
    public void testAllNetworkTypes() throws Exception {
        System.out.println("testAllNetworkTypes");
        List<String> networkTypes = networksAPI.allNetworkTypes();
        printStrings("testAllNetworkTypes", networkTypes);
    }  	
    
    @Test
    public void testInternalNetworks() throws Exception {
    	
    	
        System.out.println("testPlantNetworks");
        Network network = networksAPI.buildInternalNetwork(
        		Arrays.asList(RegulomeData.datasetId),
        		RegulomeData.geneIds, 
        		Arrays.asList("GENE_GENE"));
        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        
        System.out.println();                   
    }    
    
    @Test
    public void testDataSources() throws IOException, JsonClientException{
    	for(DatasetSource ds: networksAPI.allDatasetSources()){
    		System.out.println(ds.getName());
    	}
    	
    }
    
    
    //@Test
    public void testDatasetSource2DatasetsModelSEED() throws Exception {
        System.out.println("testDatasetSource2DatasetsModelSEED");
        List<Dataset> datasets = networksAPI.datasetSource2datasets("MODELSEED");
        printDatasets("testDatasetSource2DatasetsModelSEED", datasets);
    }

    //@Test
    public void testDatasetSource2DatasetsMAK() throws Exception {
        System.out.println("testDatasetSource2DatasetsMAK");
        List<Dataset> datasets = networksAPI.datasetSource2datasets("MAK_BICLUSTER");
        printDatasets("testDatasetSource2DatasetsMAK", datasets);
    }
    
    //@Test
    public void testTaxon2Datasets() throws Exception {
        String taxon = "kb|g.1870";//"kb|g.20848";//"kb|g.1870";//
        List<Dataset> datasets = networksAPI.taxon2datasets(taxon);
        printDatasets("testTaxon2Datasets", datasets);
    }

    //@Test
    public void testNetworkType2DatasetsFunc() throws Exception {
        String nettype = "FUNCTIONAL_ASSOCIATION";
        List<Dataset> datasets = networksAPI.networkType2datasets(nettype);
        printDatasets("testNetworkType2Datasets", datasets);
    }

    //@Test
    public void testNetworkType2DatasetsPPI() throws Exception {
        String nettype = "PROT_PROT_INTERACTION";
        List<Dataset> datasets = networksAPI.networkType2datasets(nettype);
        printDatasets("testNetworkType2Datasets", datasets);
    }

    //@Test
    public void testEntity2Datasets() throws Exception {
        List<Dataset> datasets = networksAPI.entity2datasets("kb|g.1870.peg.2087"); //"kb|g.1870.peg.1532", "kb|g.1870.peg.2087"
        printDatasets("testEntity2Datasets", datasets);
    }

    //@Test
    public void testBuildFirstNeighborNetwork() throws Exception {
//		Network network = networksAPI.buildFirstNeighborNetwork(Arrays.asList("kb|netdataset.modelseed.0"), "kb|g.21765.CDS.543", Arrays.asList("GENE_CLUSTER")); 
//		printNetwork("testBuildFirstNeighborNetwork", network);


        Network network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                        "kb|netdataset.regprecise.301",
                        "kb|netdataset.modelseed.0",
                        "kb|netdataset.ppi.7"
                ),
                Arrays.asList("kb|g.21765.CDS.543"),
                Arrays.asList("GENE_CLUSTER"));

        printNetwork("testBuildFirstNeighborNetwork", network);
    }


    //@Test
    public void testBuildInternalNetwork() throws Exception {
        Network network = networksAPI.buildInternalNetwork(Arrays.asList("kb|netdataset.kb|g.20848.regulome.0"),
                Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.1454", "kb|g.20848.CDS.2811"), Arrays.asList("GENE_GENE"));
        printNetwork("testBuildInternalNetwork", network);
    }


    private void printDatasetSources(String title, List<DatasetSource> dsSources) {
        System.out.println("==================  " + title);
        for (DatasetSource dsSource : dsSources) {
            System.out.println(dsSource.getId()
                    + "\t" + dsSource.getName()
                    + "\t" + dsSource.getReference()
                    + "\t" + dsSource.getResourceUrl()
                    + "\t" + dsSource.getDescription()
            );
        }
    }


    private void printDatasets(String title, List<Dataset> datasets) {
        System.out.println("==================  " + title);
        for (Dataset ds : datasets) {
            System.out.println(ds.getId()
                    + "\t" + ds.getName()
                    + "\t" + ds.getNetworkType()
                    + "\t" + ds.getSourceRef()
            );
        }
    }

    private void printStrings(String title, List<String> values) {
        System.out.println("==================  " + title);
        for (String value : values) {
            System.out.println(value);
        }
    }

    private void printNetwork(String title, Network network) {
        System.out.println("==================  " + title);
        System.out.println(
                network.getId()
                        + "\t" + network.getName()
                        + "\t Datasets count: " + network.getDatasets().size()
                        + "\t Nodes count: " + network.getNodes().size()
                        + "\t Edges count: " + network.getEdges().size()
        );
        printDatasets("Network datasets", network.getDatasets());
    }

}
