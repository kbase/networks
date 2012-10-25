package us.kbase.networks;

import java.util.Arrays;
import java.util.List;

import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.Taxon;

public class NetworksAPIDemo {
	
	NetworksAPI api;	
	
	public void run() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();
		
		// Ecoli: three adaptors work together (ModelSEED, PPI, RegPrecise)
		test_buildFirstNeighborNetwork("kb|g.21765", "kb|g.21765.CDS.543");
		
		// Shewnalla: two adapters work together (RegPrecise, MAK)
		test_buildFirstNeighborNetwork("kb|g.20848", "kb|g.20848.CDS.1671");
		
	}
	
	private void test_buildFirstNeighborNetwork(String genomeId, String geneId) throws AdaptorException
	{
		Taxon taxon = new Taxon(genomeId);
		
		List<Dataset> datasets = api.getDatasets(taxon);

		// To avoid to many interactions...
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.3");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.5");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.9");
		
		NetworksUtil.printDatasets(genomeId, datasets);
		
		Network network = 
			api.buildFirstNeighborNetwork(datasets, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());	
	}
	

	public static void main(String[] args) throws AdaptorException {
		new NetworksAPIDemo().run();
	}
	
}
