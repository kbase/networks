package us.kbase.networks.adaptor.mak;

import java.util.Arrays;
import java.util.List;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;

public class MAKAdaptorDemo {

	Adaptor adaptor;
	Entity gene = new Entity("kb|g.20848.CDS.2811", EntityType.GENE);
	Entity bicluster = new Entity("kb|bicluster.245", EntityType.BICLUSTER);
		
	private void run() throws AdaptorException {
		adaptor = new MAKAdaptorFactory().buildAdaptor();
		test_getDatasets1();
		test_buildFirstNeighborNetwork1();
		test_buildFirstNeighborNetwork2();
	}

	private void test_buildFirstNeighborNetwork2() throws AdaptorException {
		Dataset ds = adaptor.getDatasets().get(0);
		Network network = adaptor.buildFirstNeighborNetwork(ds, bicluster, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());		
	}
	
	
	private void test_buildFirstNeighborNetwork1() throws AdaptorException {
		Dataset ds = adaptor.getDatasets().get(0);
		Network network = adaptor.buildFirstNeighborNetwork(ds, gene, Arrays.asList(EdgeType.GENE_CLUSTER));
		
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
