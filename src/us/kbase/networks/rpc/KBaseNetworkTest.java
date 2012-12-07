package us.kbase.networks.rpc;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.kbase.networks.rpc.client.Dataset;
import us.kbase.networks.rpc.client.DatasetSource;
import us.kbase.networks.rpc.client.KBaseNetworks;
import us.kbase.networks.rpc.client.Network;

public class KBaseNetworkTest {

	String url = "http://127.0.0.1:8080/KBaseNetworksRPC/networks";
//	String url = "http://140.221.92.147:8080/KBaseNetworksRPC/networks";
//	String url = "http://127.0.0.1:7064/KBaseNetworksRPC/networks";
	KBaseNetworks networksAPI;
		
	
	@Before
	public void setUp() throws Exception {
		networksAPI = new KBaseNetworks(url);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllDatasets() throws Exception {
		List<Dataset> datasets = networksAPI.allDatasets(); 
		printDatasets("testAllDatasets", datasets);
	}


	@Test
	public void testAllDatasetSources() throws Exception {
		List<DatasetSource> dsSources = networksAPI.allDatasetSources(); 
		printDatasetSources("testAllDatasetSources", dsSources);
	}


	@Test
	public void testAllNetworkTypes() throws Exception {
		List<String> networkTypes = networksAPI.allNetworkTypes(); 
		printStrings("testAllNetworkTypes", networkTypes);
	}


	@Test
	public void testDatasetSource2Datasets() throws Exception {
		List<Dataset> datasets = networksAPI.datasetSource2Datasets("MODELSEED"); 
		printDatasets("testDatasetSource2Datasets", datasets);
	}

	@Test
	public void testTaxon2Datasets() throws Exception {
		List<Dataset> datasets = networksAPI.taxon2Datasets("kb|g.21765"); 
		printDatasets("testTaxon2Datasets", datasets);
	}

	@Test
	public void testNetworkType2Datasets() throws Exception {
		List<Dataset> datasets = networksAPI.networkType2Datasets("PROT_PROT_INTERACTION"); 
		printDatasets("testNetworkType2Datasets", datasets);
	}

	@Test
	public void testEntity2Datasets() throws Exception {
		List<Dataset> datasets = networksAPI.entity2Datasets("kb|g.21765.CDS.967"); 
		printDatasets("testEntity2Datasets", datasets);
	}

	@Test
	public void testBuildFirstNeighborNetwork() throws Exception {
		Network network = networksAPI.buildFirstNeighborNetwork(Arrays.asList("kb|netdataset.kb|fm.0"), "kb|g.21765.CDS.543", Arrays.asList("GENE_CLUSTER")); 
		printNetwork("testBuildFirstNeighborNetwork", network);
	}


	@Test
	public void testBuildInternalNetwork() throws Exception {
		Network network = networksAPI.buildInternalNetwork(Arrays.asList("kb|netdataset.kb|g.20848.regulome.0"), 
				Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.1454", "kb|g.20848.CDS.2811"), Arrays.asList("GENE_GENE")); 
		printNetwork("testBuildInternalNetwork", network);
	}



	private void printDatasetSources(String title, List<DatasetSource> dsSources) {
		System.out.println("==================  " + title);
		for(DatasetSource dsSource: dsSources)
		{
			System.out.println(dsSource.id
					+ "\t" + dsSource.name
					+ "\t" + dsSource.reference
					+ "\t" + dsSource.resourceURL
					+ "\t" + dsSource.description
					);
		}		
	}
	
	
	private void printDatasets(String title, List<Dataset> datasets) {
		System.out.println("==================  " + title);
		for(Dataset ds: datasets)
		{
			System.out.println(ds.id
					+ "\t" + ds.name
					+ "\t" + ds.networkType
					+ "\t" + ds.sourceReference
					);
		}		
	}
	
	private void printStrings(String title, List<String> values) {
		System.out.println("==================  " + title);
		for(String value: values)
		{
			System.out.println(value);
		}		
	}
	
	private void printNetwork(String title, Network network) {
		System.out.println("==================  " + title);
		System.out.println(
				network.id
				+ "\t" + network.name
				+ "\t Datasets count: " + network.datasets.size()
				+ "\t Nodes count: " + network.nodes.size()
				+ "\t Edges count: " + network.edges.size()
				);
	}
	
}
