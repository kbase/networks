package us.kbase.networks.adaptor.mak;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;

public class MAKAdaptorTest {

	Adaptor adaptor = new MAKAdaptorFactory().buildAdaptor();
	String geneId = "kb|g.20848.CDS.2811";
	Dataset goodDataset = new Dataset("kb|dataset.mak1", "", "", null, null, (Taxon) null);
	
	private void run() throws AdaptorException {
		test_getDatasets1();
		test_buildFirstNeighborNetwork1();
	}

	
	private void test_buildFirstNeighborNetwork1() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		printNetwork(network);
		visualizeNetwork(network.getGraph());		
	}
	
	
	private void test_getDatasets1() throws AdaptorException {
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
		
	private void printNetwork(Network network) {
		System.out.println("Nodes count = " + network.getGraph().getVertexCount());
		System.out.println("Edges count = " + network.getGraph().getEdgeCount());
		System.out.println(network.getGraph().toString());
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
	
	public static void main(String[] args) throws AdaptorException {
		new MAKAdaptorTest().run();		
	}
}
