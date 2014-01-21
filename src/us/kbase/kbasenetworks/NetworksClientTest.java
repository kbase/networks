package us.kbase.kbasenetworks;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NetworksClientTest {

	String url = "http://127.0.0.1:1111";
	
	KBaseNetworksClient networksAPI;


    @Before
    public void setUp() throws Exception {
        networksAPI = new KBaseNetworksClient(new URL(url));
    }

    @After
    public void tearDown() throws Exception {
    }
   
    @Test
    public void testUseCase4Shiran1() throws Exception {

        Network network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                        "kb|netdataset.regprecise.1"
//                        ,"kb|dataset.mak2"
                        ,"kb|dataset.mak4"
                ),
                                
                Arrays.asList("kb|g.20848.CDS.837", "kb|g.20848.CDS.3186", "kb|g.20848.CDS.537"), 
                Arrays.asList("GENE_CLUSTER"));

        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        System.out.println();    
    }
    
    
   @Test
    public void testUseCase4Shiran() throws Exception {

        Network network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                        "kb|netdataset.regprecise.1"
//                        "kb|dataset.mak2"
                        ,"kb|dataset.mak4"
                ),
                                
                Arrays.asList("kb|g.20848.CDS.837", "kb|g.20848.CDS.3186", "kb|g.20848.CDS.537"), //, "kb|g.20848.regulon.5239"
                Arrays.asList("GENE_CLUSTER"));

        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        System.out.println();
        
/*        
        network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                		"kb|netdataset.regprecise.301",
                        "kb|netdataset.modelseed.1697",
                        "kb|netdataset.ppi.7"
                ),
                                
                Arrays.asList("kb|g.1870.peg.3322", "kb|g.1870.peg.1532", "kb|g.1870.peg.2087"), //, "kb|g.20848.regulon.5239"
                Arrays.asList("GENE_CLUSTER"));

        ClientNetworksUtil.printNetwork(network);
        ClientNetworksUtil.visualizeNetwork(network);
        System.out.println();        
        
        network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                		"kb|netdataset.regprecise.301",
                        "kb|netdataset.modelseed.1697",
                        "kb|netdataset.ppi.7"
                ),
                                
                Arrays.asList("kb|g.1870.peg.3322", "kb|g.1870.peg.1532", "kb|g.1870.peg.2087"), //, "kb|g.20848.regulon.5239"
                Arrays.asList("GENE_GENE"));

        ClientNetworksUtil.printNetwork(network);
        ClientNetworksUtil.visualizeNetwork(network);
*/        
        System.out.println();                   
    }  
    
     
    //@Test
    public void testUseCase4Shiran2() throws Exception {

    	Network network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
//                        "kb|netdataset.regprecise.301",
                        "kb|netdataset.modelseed.1697"
//                        "kb|netdataset.ppi.7"
                ),
                        
                Arrays.asList("kb|subsystem.Butanol Biosynthesis"),
//                Arrays.asList("kb|g.1870.peg.1847"), //, "kb|g.20848.regulon.5239"
                //Arrays.asList("kb|subsystem.Nitrate and nitrite ammonification"),
                //Arrays.asList("kb|subsystem.Fatty acid degradation regulons"), //, "kb|g.20848.regulon.5239"
                Arrays.asList("GENE_CLUSTER"));

        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        System.out.println();
    }
    
    
    //@Test
    public void testMetagenomes() throws Exception {

    	List<Dataset> datasets = networksAPI.entity2datasets("kb|subsystem.Serine-glyoxylate_cycle");
        NetworksClientUtil.printDatasets("=========", datasets);

    	
    	Network network = networksAPI.buildFirstNeighborNetwork(
                Arrays.asList(
                        datasets.get(0).getId()
                ),
                        
                Arrays.asList("kb|subsystem.Serine-glyoxylate_cycle"),
                Arrays.asList("SUBSYSTEM_CLUSTER"));    	
        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        System.out.println();
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
    public void testPlantNetworks() throws Exception {
        System.out.println("testAllNetworkTypes");
        Network network = networksAPI.buildInternalNetwork(
        		Arrays.asList("kb|netdataset.plant.fn.25","kb|netdataset.plant.cn.6"),
        		Arrays.asList("kb|g.3899.locus.10", "kb|g.3899.locus.11", "kb|g.3899.locus.28905", "kb|g.3899.locus.24583"), 
        		Arrays.asList("GENE_GENE"));
        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        
        
        network = networksAPI.buildInternalNetworkLimitedByStrength(
        		Arrays.asList("kb|netdataset.plant.fn.25","kb|netdataset.plant.cn.6"),
        		Arrays.asList("kb|g.3899.locus.10","kb|g.3899.locus.11","kb|g.3899.locus.28905","kb|g.3899.locus.24583"), 
        		Arrays.asList("GENE_GENE"), 0d);
        NetworksClientUtil.printNetwork(network);
        NetworksClientUtil.visualizeNetwork(network);
        
        
        
        System.out.println();                   
    }
    
    	
    	
    	
    
    //@Test
    public void testDatasetSources2Datasets() throws Exception {
        System.out.println("testDatasetSources2Datasets");
        //String[] sources = new String[]{"AGRIS", "ARANET", "GEO", "INTACT", "PLANTCYC", "POPNET","KEGG"};
        //String[] sources = new String[]{"PPI", "REGPRECISE", "CMONKEY", "ECOCYC", "MAK_BICLUSTER", "MO", "MODELSEED"};
        //String[] sources = new String[]{"MG_RAST"};
        String[] sources = new String[]{"REGPRECISE"};
        
        int count = 0;
        for(String source: sources)
        {
            List<Dataset> datasets = networksAPI.datasetSource2datasets(source);
            count += datasets.size();        	
        }
        System.out.println("Count = " + count);
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
