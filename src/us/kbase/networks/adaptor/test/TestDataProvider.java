package us.kbase.networks.adaptor.test;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;

public class TestDataProvider {
	public static Dataset[] testDatasets = new Dataset[]{
			new Dataset(
					"kb|netdataset.121", 
					"My dataset1", 
					"Dataseet description 1", 
					NetworkType.PROT_PROT_INTERACTION,
					DatasetSource.ARANET, 
					new Taxon("kb|g.125")),
			
			new Dataset(
					"kb|netdataset.158", 
					"My dataset2", 
					"Dataset description 2", 
					NetworkType.REGULATORY_NETOWRK,
					DatasetSource.REGPECISE, 
					new Taxon("kb|g.258"))			
	};
	
	
	public static Node[] getDatasetGeneNodes(Dataset dataset)
	{
		if(testDatasets[0].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.124", "modA", new Entity("kb|g.126.CDS.245")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.56", "modB", new Entity("kb|g.126.CDS.26"))
				};
		}
		else if(testDatasets[1].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.134", "modC", new Entity("kb|g.126.CDS.234")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.54", "modD", new Entity("kb|g.126.CDS.64"))
				};
		}
		return new Node[0]; 
	}
	
	public static Node[] getDatasetClusterNodes(Dataset dataset, String geneId)
	{
		if(testDatasets[0].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildClusterNode("kb|netnode.124", "lexA", new Entity("kb|g.126.regulon.245")),
					Node.buildClusterNode("kb|netnode.34", "modR", new Entity("kb|g.126.regulon.23")),
					Node.buildClusterNode("kb|netnode.56", "crp", new Entity("kb|g.126.regulon.26"))
				};
		}
		else if(testDatasets[1].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildClusterNode("kb|netnode.134", "b1", new Entity("kb|g.126.bicluster.234")),
					Node.buildClusterNode("kb|netnode.34", "b2", new Entity("kb|g.126.bicluster.23")),
					Node.buildClusterNode("kb|netnode.54", "b3", new Entity("kb|g.126.bicluster.64"))
				};
		}
		return new Node[0]; 
	}	
	
	
	public static Node[] getFirstNeighborNodes(Dataset dataset, String geneId)
	{
		if(testDatasets[0].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.124", "modA", new Entity("kb|g.126.CDS.245")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.56", "modB", new Entity("kb|g.126.CDS.26"))
				};
		}
		else if(testDatasets[1].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.134", "modC", new Entity("kb|g.126.CDS.234")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.54", "modD", new Entity("kb|g.126.CDS.64"))
				};
		}
		return new Node[0]; 
	}
	
	
	public static Node[] getDatasetNodes(Dataset dataset, List<String> geneIds)
	{
		if(testDatasets[0].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.124", "modA", new Entity("kb|g.126.CDS.245")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.56", "modB", new Entity("kb|g.126.CDS.26"))
				};
		}
		else if(testDatasets[1].equals(dataset))
		{
			return 			
				new Node[]{
					Node.buildGeneNode("kb|netnode.134", "modC", new Entity("kb|g.126.CDS.234")),
					Node.buildGeneNode("kb|netnode.34", "lexA", new Entity("kb|g.126.CDS.23")),
					Node.buildGeneNode("kb|netnode.54", "modD", new Entity("kb|g.126.CDS.64"))
				};
		}
		return new Node[0]; 
	}	
	
	public static Node getDatasetGeneNode(Dataset dataset, String geneId)
	{
		return 	Node.buildGeneNode("kb|netnode.587", "qqqA", new Entity(geneId));		
	}	
	
	public static List<Node> getDatasetGeneNode(Dataset dataset, List<String> geneIds)
	{
		List<Node> nodes = new Vector<Node>();
		int i = 100;
		for(String geneId: geneIds)
		{
			nodes.add(Node.buildGeneNode("kb|netnode." + i, "qqq" + i, new Entity(geneId)));
			i++;
		}
		
		return 	nodes;		
	}

	public static boolean isConnected(Node node1, Node node2) {
		return Math.random() < 0.5;
	}		
	
}
