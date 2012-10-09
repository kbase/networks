package us.kbase.networks.adaptor.test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;


public class TestAdaptor implements Adaptor{


	@Override
	public List<Dataset> getDatasets() {
		return Arrays.asList(TestDataProvider.testDatasets);
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType) {
		List<Dataset> resDatasets = new Vector<Dataset>();
		
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.getNetworkType() == networkType) 
			{
				resDatasets.add(ds);
			}
		}
		return resDatasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) {
		List<Dataset> resDatasets = new Vector<Dataset>();
		
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.getDatasetSource() == datasetSource) 
			{
				resDatasets.add(ds);
			}
		}
		return resDatasets;
	}

	@Override
	public boolean hasDataset(Dataset dataset) {
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.equals(dataset)) return true;
		}
		return false;
	}	
	
	@Override
	public List<Dataset> getDatasets(Taxon taxon) {
		List<Dataset> resDatasets = new Vector<Dataset>();
		
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			for(Taxon dsTaxon: ds.getTaxons())
			{
				if(dsTaxon.equals(taxon)) 
				{
					resDatasets.add(ds);
					continue;
				}				
			}
		}
		return resDatasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) {
		List<Dataset> resDatasets = new Vector<Dataset>();
		
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.getNetworkType() != networkType) continue; 
			if(ds.getDatasetSource() != datasetSource) continue; 
				
			for(Taxon dsTaxon: ds.getTaxons())
			{
				if(dsTaxon.equals(taxon)) 
				{
					resDatasets.add(ds);
					continue;
				}				
			}
		}
		return resDatasets;
	}
	
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId) {

		// Create a graph of a desired type
		Graph<Node, Edge> graph = new SparseGraph<Node, Edge>();
		
		//Populate graph with nodes and edges
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.equals(dataset))
			{
				Node centralNode = TestDataProvider.getDatasetGeneNode(dataset, geneId);
				if(centralNode == null) break;
				
				
				graph.addVertex(centralNode);				
				Node[] firstNeighborNodes = TestDataProvider.getFirstNeighborNodes(dataset, geneId);
				for(Node neighborNode: firstNeighborNodes)
				{
					graph.addVertex(neighborNode);
					graph.addEdge(new Edge("kd|netedge.1", "name1", dataset), centralNode, neighborNode);
				}
			}
		}		
		
		// Create a network object and return it
		return new Network("kb|network.1", "first neighbor network", graph);
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId,
			List<EdgeType> edgeTypes) {

		// Create a graph of a desired type
		Graph<Node, Edge> graph = new SparseGraph<Node, Edge>();
		
		//Populate graph with nodes and edges
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.equals(dataset))
			{
				Node centralNode = TestDataProvider.getDatasetGeneNode(dataset, geneId);
				if(centralNode == null) break;
				
				
				graph.addVertex(centralNode);
				
				// Add nodes and edges
				for(EdgeType edgeType : edgeTypes)
				{
					if(edgeType == EdgeType.GENE_GENE)
					{
						Node[] firstNeighborNodes = TestDataProvider.getFirstNeighborNodes(dataset, geneId);
						int i = 0;
						for(Node neighborNode: firstNeighborNodes)
						{
							graph.addVertex(neighborNode);
							graph.addEdge(new Edge("kd|netedge." + i, "name" + i, dataset), centralNode, neighborNode);
							i++;
						}
					} else if(edgeType == EdgeType.GENE_CLUSTER)
					{
						Node[] clusterNodes = TestDataProvider.getDatasetClusterNodes(dataset, geneId);
						int i = 0;
						for(Node clusterNode: clusterNodes)
						{
							graph.addVertex(clusterNode);
							graph.addEdge(new Edge("kd|netedge." + i, "name" + i, dataset), centralNode, clusterNode);
							i++;
						}						
					}
				}
				
			}
		}		
		
		// Create a network object and return it
		return new Network("kb|network.1", "first neighbor network", graph);		
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds) {
		// Create a graph of a desired type
		Graph<Node, Edge> graph = new SparseGraph<Node, Edge>();
		
		//Populate graph with nodes and edges
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.equals(dataset))
			{
				Node[] nodes = TestDataProvider.getDatasetNodes(dataset, geneIds);
				// Add nodes
				for(Node node: nodes)
				{
					graph.addVertex(node);	
				}
				
				// Add edges
				for(int i = 0; i < nodes.length; i++)
				{
					Node node1 = nodes[i];					
					for(int j = i + 1; j < nodes.length; j++)
					{
						Node node2 = nodes[j];
						if(TestDataProvider.isConnected(node1, node2) )
						{
							graph.addEdge(new Edge("kd|netedge.1", "name1", dataset), node1, node2);
						}
					}
				}				
			}
		}		
		
		// Create a network object and return it
		return new Network("kb|network.1", "internal network", graph);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
			List<EdgeType> edgeTypes) {
		// Create a graph of a desired type
		Graph<Node, Edge> graph = new SparseGraph<Node, Edge>();
		
		//Populate graph with nodes and edges
		for(Dataset ds: TestDataProvider.testDatasets)
		{
			if(ds.equals(dataset))
			{
				Node[] nodes = TestDataProvider.getDatasetNodes(dataset, geneIds);
				// Add nodes
				for(Node node: nodes)
				{
					graph.addVertex(node);	
				}
				
				// Add edges
				for(EdgeType edgeType: edgeTypes)
				{
					if(edgeType == EdgeType.GENE_GENE)
					{
						for(int i = 0; i < nodes.length; i++)
						{
							Node node1 = nodes[i];					
							for(int j = i + 1; j < nodes.length; j++)
							{
								Node node2 = nodes[j];
								if(TestDataProvider.isConnected(node1, node2) )
								{
									graph.addEdge(new Edge("kd|netedge.1", "name1", dataset), node1, node2);
								}
							}
						}				
					}
					else if(edgeType == EdgeType.GENE_CLUSTER)
					{
						for(Node node: nodes)
						{
							Node[] clusterNodes = TestDataProvider.getDatasetClusterNodes(dataset,node.getEntity().getId());
							int i = 0;
							for(Node clusterNode: clusterNodes)
							{
								graph.addVertex(clusterNode);
								graph.addEdge(new Edge("kd|netedge." + i, "name" + i, dataset), node, clusterNode);
								i++;
							}	
						}
					}
					else if(edgeType == EdgeType.PROTEIN_PROTEIN)
					{
						continue;
					}					
				}				
			}
		}		
		
		// Create a network object and return it
		return new Network("kb|network.1", "internal network", graph);
	}

	@Override
	public Network buildNetwork(Dataset dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

}
