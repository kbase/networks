package us.kbase.networks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import us.kbase.networks.core.NodeType;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class NetworksClientUtil {

	public static void printNetwork(Network network) {
		System.out.println("//------- Network: " + network.getName());
		List<Dataset> datasets =  network.getDatasets();
		
		System.out.println("### Datasets ");
		printDatasets("", datasets);

		System.out.println("### Nodes count = " + network.getNodes().size());
		for(Node node: network.getNodes())
		{
			System.out.println("Node: " + node.getId() + "\t" + node.getName() + "\t" + node.getEntityId());
			for(String propertyName: node.getProperties().keySet())
			{
				System.out.println("\t" + propertyName + ": " + node.getProperties().get(propertyName));
			}
		}
		System.out.println("### Edges count = " + network.getEdges().size());
		for(Edge edge: network.getEdges())
		{
			System.out.println("Edge: " + edge.getId() + "\t" + edge.getName());
			for(String propertyName: edge.getProperties().keySet())
			{
				System.out.println("\t" + propertyName + ": " + edge.getProperties().get(propertyName));
			}
		}
		System.out.println("### Network: " + network.getName());
		
	}
	
	public static void printDatasets(String paramName, List<Dataset> datasets)
	{
		System.out.println("//------- Datasets");
		System.out.println("Datasets count (" + paramName + "): = " + datasets.size());
		for(Dataset dataset: datasets)
		{
			System.out.println(dataset.getId() 
					+ "\t" + dataset.getNetworkType()
					+ "\t" + dataset.getSourceRef()
					+ "\t" + dataset.getName()
					);
		}		
	}	
	
	public static void visualizeNetwork(Network network)
	{
		Graph<Node,Edge> graph = new SparseMultigraph<Node, Edge>();
		Hashtable<String, Node> id2nodes = new Hashtable<String, Node>();
		for(Node node: network.getNodes())
		{
			graph.addVertex(node);
			id2nodes.put(node.getId(), node);
		}
		
		for(Edge edge: network.getEdges())
		{
			graph.addEdge(edge, id2nodes.get(edge.getNodeId1()), id2nodes.get(edge.getNodeId2()));
		}
		visualizeNetwork(graph);
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
				us.kbase.networks.core.EntityType etype = us.kbase.networks.core.EntityType.detect(node.getEntityId());
				
				if(etype == etype.GENE)
				{
					color = 
						node.getProperties().get("Regulator") != null 
						? Color.blue: Color.green;
				}
				else if(node.getType().equals(NodeType.CLUSTER.name()) )
				{
					if(etype == etype.REGULON)
					{
						color = color.blue;
					}
					else if(etype == etype.PPI_COMPLEX)
					{
						color = Color.yellow;
					}
					else if(etype == etype.SUBSYSTEM)
					{
						color = Color.CYAN;
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
