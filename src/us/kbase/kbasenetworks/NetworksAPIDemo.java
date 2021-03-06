package us.kbase.kbasenetworks;

import java.util.Arrays;
import java.util.List;

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Edge;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.EntityType;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.Node;
import us.kbase.kbasenetworks.core.NodeType;
import us.kbase.kbasenetworks.core.Taxon;
import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;

public class NetworksAPIDemo {

	NetworksAPI api;

	public void run() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();

		// Shewnalla: two adapters work together (RegPrecise, MAK)

//		testAdaptor_buildFirstNeighborNetwork(new RegPreciseAdaptorFactory().buildAdaptor(), 
//				"kb|g.20848", "kb|g.20848.CDS.1671", Arrays.asList(EdgeType.GENE_GENE));




		// Ecoli: three adaptors work together (ModelSEED, PPI, RegPrecise)
//		test_buildFirstNeighborNetwork("kb|g.21765", "kb|g.21765.CDS.543", Arrays.asList(GENE_CLUSTER_EDGE));

		// Shewnalla: two adapters work together (RegPrecise, MAK)
/*		
//		test_buildFirstNeighborNetwork("kb|g.20848", "kb|g.20848.CDS.1671", Arrays.asList(GENE_CLUSTER_EDGE));
		test_buildInternalNetwork("kb|g.20848",
//				Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.141", "kb|g.20848.CDS.3397", "kb|g.20848.CDS.972", "kb|g.20848.CDS.960", "kb|g.20848.CDS.1846"
//					,"kb|bicluster.110"),
				Arrays.asList("kb|g.20848.CDS.1454", "kb|g.20848.CDS.868", "kb|g.20848.CDS.1671","kb|g.20848.CDS.2554", "kb|g.20848.CDS.1031",
						"kb|g.20848.regulon.33", "kb|g.20848.regulon.2", "kb|g.20848.regulon.54", "kb|g.20848.regulon.29", "kb|g.20848.regulon.48", "kb|g.20848.regulon.169", "kb|g.20848.regulon.171"
						,"kb|bicluster.110"),
				Arrays.asList(GENE_CLUSTER_EDGE));
*/

		// Shewnalla:
/*		
		test_buildInternalNetwork("kb|g.21765",
				Arrays.asList("kb|g.21765.CDS.543", "kb|g.21765.CDS.544", "kb|g.21765.CDS.545"),
				Arrays.asList(GENE_GENE_EDGE));
*/		
		test_buildInternalNetwork("kb|g.20848",
				Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.1454", "kb|g.20848.CDS.2811"),
				Arrays.asList(NodeType.EDGE_GENE_GENE));



	}

	private void test_buildInternalNetwork(String genomeId, List<String> geneIds, List<String> edgeTypes) throws AdaptorException {
		Taxon taxon = new Taxon(genomeId);

		List<Dataset> datasets = api.getDatasets(taxon);
		NetworksUtil.printDatasets("", datasets);
		Network network = api.buildInternalNetwork(Dataset.toDatasetIds(datasets), Entity.toEntities(geneIds) , edgeTypes);

		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());
	}

	private void test_buildFirstNeighborNetwork(String genomeId, String geneId, List<String> edgeTypes) throws AdaptorException
	{
		Taxon taxon = new Taxon(genomeId);

		List<Dataset> datasets = api.getDatasets(taxon);

		// To avoid to many interactions...
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.1");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.3");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.5");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.9");

		NetworksUtil.printDatasets(genomeId, datasets);

		Network network =
			api.buildFirstNeighborNetwork(Dataset.toDatasetIds(datasets), new  Entity(geneId, EntityType.GENE), edgeTypes);

		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());
	}

	private void testAdaptor_buildFirstNeighborNetwork(Adaptor adaptor,String genomeId, String geneId, List<String> edgeTypes) throws AdaptorException
	{
		Taxon taxon = new Taxon(genomeId);
		List<Dataset> datasets = adaptor.getDatasets(taxon);
		NetworksUtil.printDatasets(genomeId, datasets);

		Network network =
			adaptor.buildFirstNeighborNetwork(datasets.get(0), new  Entity(geneId, EntityType.GENE), edgeTypes);

		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());
		for(Edge edge: network.getGraph().getEdges())
		{
			System.out.println("Edge: " + edge.getName());
			Node node = network.getGraph().getSource(edge);
			System.out.println("\tSource node: " + (node!=null? node.getName() : "null"));
			node = network.getGraph().getDest(edge);
			System.out.println("\tDestination node: " + (node!=null? node.getName() : "null"));
			for(Node node1: network.getGraph().getIncidentVertices(edge))
			{
				System.out.println("\tIncident node: " + (node1!=null? node1.getName() : "null"));
			}
		}
	}


	public static void main(String[] args) throws AdaptorException {
		new NetworksAPIDemo().run();
	}

}
