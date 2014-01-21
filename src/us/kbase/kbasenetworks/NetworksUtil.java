package us.kbase.kbasenetworks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Edge;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.Node;
import us.kbase.kbasenetworks.core.NodeType;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class NetworksUtil {

	public static void printNetwork(Network network) {
		System.out.println("//------- Network: " + network.getName());
		List<Dataset> datasets =  network.getDatasets();
		
		System.out.println("### Datasets ");
		printDatasets("", datasets);

		System.out.println("### Nodes count = " + network.getGraph().getVertexCount());
		for(Node node: network.getGraph().getVertices())
		{
			System.out.println("Node: " + node.getId() + "\t" + node.getName());
			for(String propertyName: node.getPropertyNames())
			{
				System.out.println("\t" + propertyName + ": " + node.getProperty(propertyName));
			}
		}
		System.out.println("### Edges count = " + network.getGraph().getEdgeCount());
		for(Edge edge: network.getGraph().getEdges())
		{
			System.out.println("Edge: " + edge.getId() + "\t" + edge.getName());
			for(String propertyName: edge.getPropertyNames())
			{
				System.out.println("\t" + propertyName + ": " + edge.getProperty(propertyName));
			}
		}
		System.out.println("### Network: " + network.getGraph().toString());
		
	}
	
	public static void printDatasets(String paramName, List<Dataset> datasets)
	{
		System.out.println("//------- Datasets");
		System.out.println("Datasets count (" + paramName + "): = " + datasets.size());
		for(Dataset dataset: datasets)
		{
			System.out.println(dataset.getId() 
					+ "\t" + dataset.getNetworkType().getName()
					+ "\t" + dataset.getDatasetSource().getName()
					+ "\t" + dataset.getName()
					);
		}		
	}	
	
	
	public static void visualizeNetwork(final Graph<Node,Edge> graph)
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
						node.getProperty("Regulator") != null 
						? Color.blue: Color.green;
				}
				else if(node.getType() == NodeType.CLUSTER)
				{
					if(node.getEntityId().contains("regulon"))
					{
						color = color.blue;
					}
					else if(node.getName().startsWith("complex "))
					{
						color = Color.yellow;
					}
					else{
						color = Color.red;	
					}					
				}				
				return color;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);	
//		vv.getRenderContext().setEdgeFillPaintTransformer(edgePaint);
		JFrame frame = new JFrame("Simple Graph View"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.getContentPane().add(vv); 
		frame.pack();
		frame.setVisible(true);
	}	
	
	public static void removeDataset(List<Dataset> datasets, String datasetId) {
		for(Dataset ds: datasets)
		{
			if(ds.getId().equals(datasetId))
			{
				datasets.remove(ds);
				break;
			}
		}
	}	
}
