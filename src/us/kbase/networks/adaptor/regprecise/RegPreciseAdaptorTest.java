package us.kbase.networks.adaptor.regprecise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class RegPreciseAdaptorTest {
	
	Adaptor adaptor = new RegPreciseAdaptorFactory().buildAdaptor();

	String geneId = "kb|g.20848.CDS.2811";
	List<String> geneIds = Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.1454", "kb|g.20848.CDS.2811");
	
	Dataset goodDataset = new Dataset("kb|netdataset.kb|g.20848.regulome.0", "", "", null, null, (Taxon) null);
	Dataset badDataset  = new Dataset("kb|netdataset.kb|g.20848.regulome.1", "", "", null, null, (Taxon) null);
	
	Taxon goodTaxon = new Taxon("kb|g.19732");
	Taxon badTaxon  = new Taxon("kb|g.QQQQQ");
	
	
	private void run() {
		
//		test_getDatasets1();
//		test_getDatasets2();
//		test_getDatasets3();
//		test_getDatasets4();

		
//		test_hasDataset();
		
//		test_buildFirstNeighborNetwork1();
		test_buildFirstNeighborNetwork2();
//		test_buildFirstNeighborNetwork3();
		
//		test_buildInternalNetwork();
	}

	private void test_buildInternalNetwork() {
		Network network = adaptor.buildInternalNetwork(goodDataset, geneIds, Arrays.asList(EdgeType.GENE_GENE));
		
		printNetwork(network);
		visualizeNetwork(network.getGraph());	
	}

	private void test_buildFirstNeighborNetwork3() {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, geneId, Arrays.asList(EdgeType.GENE_GENE,EdgeType.GENE_CLUSTER));
		
		printNetwork(network);
		visualizeNetwork(network.getGraph());		
	}

	private void test_buildFirstNeighborNetwork2() {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, geneId, Arrays.asList(EdgeType.GENE_GENE));
		
		printNetwork(network);
		visualizeNetwork(network.getGraph());
		
	}

	private void test_buildFirstNeighborNetwork1() {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		printNetwork(network);
		visualizeNetwork(network.getGraph());		
	}

	private void printNetwork(Network network) {
		System.out.println("Nodes count = " + network.getGraph().getVertexCount());
		System.out.println("Edges count = " + network.getGraph().getEdgeCount());
		System.out.println(network.getGraph().toString());
	}

	private void test_hasDataset() {
				
		System.out.println("Has good dataset: " + adaptor.hasDataset(goodDataset));		
		System.out.println("Has bad  dataset: " + adaptor.hasDataset(badDataset));		
	}
	
	
	private void test_getDatasets4() {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(goodTaxon);		
		printDatasets("good taxon " + goodTaxon.getGenomeId(), datasets);
		
		datasets = adaptor.getDatasets(badTaxon);		
		printDatasets("bad taxon " + badTaxon.getGenomeId(), datasets);		
	}

	private void test_getDatasets3() {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(DatasetSource.REGPECISE);		
		printDatasets(DatasetSource.REGPECISE.getName(), datasets);
		
		datasets = adaptor.getDatasets(DatasetSource.AGRIS);		
		printDatasets(DatasetSource.AGRIS.getName(), datasets);
		
		datasets = adaptor.getDatasets(DatasetSource.CMONKEY);		
		printDatasets(DatasetSource.CMONKEY.getName(), datasets);
		
		//...
	}


	private void test_getDatasets2() {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(NetworkType.REGULATORY_NETOWRK);		
		printDatasets(NetworkType.REGULATORY_NETOWRK.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.FUNCTIONAL_ASSOCIATION);		
		printDatasets(NetworkType.FUNCTIONAL_ASSOCIATION.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.METABOLIC_PATHWAY);		
		printDatasets(NetworkType.METABOLIC_PATHWAY.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION);		
		printDatasets(NetworkType.PROT_PROT_INTERACTION.getName(), datasets);
	}


	private void test_getDatasets1() {
		List<Dataset> datasets = adaptor.getDatasets();
		printDatasets("", datasets);
	}

	private void printDatasets(String paramName, List<Dataset> datasets)
	{
		for(Dataset dataset: datasets)
		{
			System.out.println(dataset.getId() 
					+ "\t" + dataset.getNetworkType().getName()
					+ "\t" + dataset.getDatasetSource().getName()
					+ "\t" + dataset.getName()
					);
		}
		
		System.out.println("------------------------");
		System.out.println("Datasets count (" + paramName + "): " + datasets.size());
	}
	
	
	private void visualizeNetwork(final Graph<Node,Edge> graph)
	{
		Layout<Node, String> layout = new CircleLayout(graph);
		layout.setSize(new Dimension(600,600));
		BasicVisualizationServer<Node,String> vv =
			new BasicVisualizationServer<Node,String>(layout);
		vv.setPreferredSize(new Dimension(650,650));
		
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller()); 
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());		
		
		Transformer<Node,Paint> vertexPaint = new Transformer<Node,Paint>() {
			public Paint transform(Node node) {
				Color color = Color.black;
				if(node.getType() == NodeType.GENE )
				{
					color = 
						node.getProperty(RegPreciseAdaptor.NODE_PROPERTY_REGULATOR) != null 
						? Color.blue: Color.green;
				}
				else if(node.getType() == NodeType.CLUSTER)
				{
					color = Color.red;
				}
				
				return color;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);	
		JFrame frame = new JFrame("Simple Graph View"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.getContentPane().add(vv); 
		frame.pack();
		frame.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new RegPreciseAdaptorTest().run();
	}
}
