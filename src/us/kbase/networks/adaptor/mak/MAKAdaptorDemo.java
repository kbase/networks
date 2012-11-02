package us.kbase.networks.adaptor.mak;

import java.util.Arrays;
import java.util.List;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.Taxon;

public class MAKAdaptorDemo {

	Adaptor adaptor = new MAKAdaptorFactory().buildAdaptor();
	String geneId = "kb|g.20848.CDS.2811";
	Dataset goodDataset = new Dataset("kb|dataset.mak1", "", "", null, null, (Taxon) null);
	
	private void run() throws AdaptorException {
		test_getDatasets1();
		test_buildFirstNeighborNetwork1();
	}

	
	private void test_buildFirstNeighborNetwork1() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());		
	}
	
	
	private void test_getDatasets1() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets();
		NetworksUtil.printDatasets("", datasets);
	}
			
	public static void main(String[] args) throws AdaptorException {
		new MAKAdaptorDemo().run();		
	}
}