package us.kbase.networks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorRepository;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;

public class NetworksAPI {
	
	private static NetworksAPI networksAPI;
	private AdaptorRepository adaptorRepository; 	
	
	private NetworksAPI() throws AdaptorException{		
		adaptorRepository = AdaptorRepository.getAdaptorRepository();
	}
		
	public static NetworksAPI getNetworksAPI() throws AdaptorException
	{
		if(networksAPI == null)
		{
			networksAPI = new NetworksAPI();			
		}		
		return networksAPI;
	}
	
	public List<NetworkType> getNetworkTypes()
	{
		return Arrays.asList(NetworkType.values());
	}
	
	public List<DatasetSource> getDatasetSources()
	{
		return Arrays.asList(DatasetSource.values());
	}
	
	public List<Dataset> getDatasets() throws AdaptorException
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets());
		}		
		return datasets;
	}
	
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets(networkType));
		}		
		return datasets;
	}
	
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets(datasetSource));
		}		
		return datasets;
	}	
	
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			List<Dataset> adaptorDatasets = adaptor.getDatasets(entity);
			if(adaptorDatasets != null)
			{
				datasets.addAll(adaptorDatasets);
			}
		}		
		return datasets;		
	}
	
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets(taxon));
		}		
		return datasets;
	}	
			
	public Network buildNetwork(Dataset dataset) throws AdaptorException
	{
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{					
			if(adaptor.hasDataset(dataset.getId()) )
			{
				return adaptor.buildNetwork(dataset);
			}
		}	
		return null;
	}
	
	
	public Network buildFirstNeighborNetwork(List<Dataset> datasets, Entity entity,
			List<EdgeType> edgeTypes) throws AdaptorException 
	{		
		List<Network> networks = new Vector<Network>();
		for(Dataset dataset: datasets)
		{
			for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
			{
				if(adaptor.hasDataset(dataset.getId()) )
				{
					Network network = adaptor.buildFirstNeighborNetwork(dataset, entity, edgeTypes);
					if(network != null)
					{
						networks.add(network);
					}
				}				
			}
		}			
		
		return buildUnionNetwork(networks);		
	}

	public Network buildInternalNetwork(List<Dataset> datasets, List<Entity> entities,
			List<EdgeType> edgeTypes) throws AdaptorException 
	{		
		List<Network> networks = new Vector<Network>();
		
		for(Dataset dataset: datasets)
		{
			for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
			{
				if(adaptor.hasDataset(dataset.getId()) )
				{
					Network network = adaptor.buildInternalNetwork(dataset, entities, edgeTypes);
					if(network != null)
					{
						networks.add(network);
					}
				}				
			}
		}			
		
		return buildUnionNetwork(networks);		
	}
	
	
	
	private Network buildUnionNetwork(List<Network> networks) {
		Hashtable<String, Node> entityId2NodeHash =
			getNonredundantEntityId2NodeHash(networks);
			
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network("kb|network.NNN", "", graph);
		
		// Add all nodes
		for(Node node: entityId2NodeHash.values())
		{
			graph.addVertex(node);
		}
				
		// Add all edges
		for(Network nw: networks)
		{			
			Graph<Node, Edge> nwGraph = nw.getGraph();			
			for(Edge nwEdge:  nwGraph.getEdges())
			{				
				edu.uci.ics.jung.graph.util.EdgeType nwEdgeType = nwGraph.getEdgeType(nwEdge);
				Node nwSourceNode = null;
				Node nwDestNode = null;
				
				
				if(nwEdgeType == edu.uci.ics.jung.graph.util.EdgeType.DIRECTED)
				{
					nwSourceNode = nwGraph.getSource(nwEdge);
					nwDestNode = nwGraph.getDest(nwEdge);
				}
				else if(nwEdgeType == edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED){
					Collection<Node> nwNodes = nwGraph.getIncidentVertices(nwEdge);
					
					// Consider only edges that connect two nodes 
					if(nwNodes.size() != 2) continue;
					
					Iterator<Node> iterator = nwNodes.iterator();
					nwSourceNode = iterator.next(); 
					nwDestNode = iterator.next(); 
				}
				
				Node sourceNode = entityId2NodeHash.get(nwSourceNode.getEntityId());
				Node destNode = entityId2NodeHash.get(nwDestNode.getEntityId());
				
				graph.addEdge(nwEdge, sourceNode, destNode, nwEdgeType);				
			}
		}
		
		return network;
	}

	private Hashtable<String, Node> getNonredundantEntityId2NodeHash(List<Network> networks) {
		Hashtable<String, Node> entityId2NodeHash = new Hashtable<String, Node>();
		for(Network network: networks)
		{
			for(Node node: network.getGraph().getVertices())
			{
				Node refNode = entityId2NodeHash.get(node.getEntity().getId());
				if( refNode == null)
				{
					refNode = node.clone();
					entityId2NodeHash.put(refNode.getEntity().getId(), refNode);					
				}
				
				refNode.addProperties(node);
				refNode.addUserAnnotations(node);
			}
		}

		return entityId2NodeHash;
	}

	public Network buildFirstNeighborNetwork(List<Dataset> datasets,
			String geneId, List<EdgeType> edgeTypes, float cutOff) {
		Network network = buildFirstNeighborNetwork(datasets, geneId, edgeTypes, cutOff);
    	cutOffNetwork(network, cutOff);
		return network;
	}
	

	public Network buildInternalNetwork(List<Dataset> datasets,
			List<String> geneIds, List<EdgeType> edgeTypes, float cutOff) {
		Network network = buildInternalNetwork(datasets, geneIds, edgeTypes, cutOff);
    	cutOffNetwork(network, cutOff);
		return network;
	}	
	
    private void cutOffNetwork(us.kbase.networks.core.Network network, float cutOff) {
    	Graph<Node,Edge> graph = network.getGraph();
    	List<Edge> deletedEdges = new ArrayList<Edge>();
    	for( Edge e : network.getGraph().getEdges()) {
    		if( e.getConfidence() < cutOff) {
    			deletedEdges.add(e);
    		}
    	}
    	for( Edge e : deletedEdges) {
    		Pair<Node> p = graph.getEndpoints(e);
    		graph.removeEdge(e);
    		if(graph.degree(p.getFirst()) < 1) {
    			graph.removeVertex(p.getFirst());
    		}
    		if(graph.degree(p.getSecond()) < 1) {
    			graph.removeVertex(p.getSecond());
    		}
    	}
    }
	
}
