package us.kbase.kbasenetworks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.AdaptorRepository;
import us.kbase.kbasenetworks.adaptor.IdGenerator;
import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.DatasetSource;
import us.kbase.kbasenetworks.core.Edge;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.NetworkType;
import us.kbase.kbasenetworks.core.Node;
import us.kbase.kbasenetworks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

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
		HashSet<NetworkType> networkTypeSet = new HashSet<NetworkType>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			networkTypeSet.addAll(adaptor.getNetworkTypes());
		}		
		List<NetworkType> networkTypes = new ArrayList<NetworkType>(networkTypeSet);
		Collections.sort(networkTypes,  new Comparator<NetworkType>(){

			@Override
			public int compare(NetworkType nt1, NetworkType nt2) {
				return nt1.name().compareTo(nt2.name()); 
			}
		}); 
		
		return networkTypes;
	}
	
	public List<DatasetSource> getDatasetSources()
	{
		HashSet<DatasetSource> datasetSourceSet = new HashSet<DatasetSource>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasetSourceSet.addAll(adaptor.getDatasetSources());
		}
		
		List<DatasetSource> datasetSources = new ArrayList<DatasetSource>(datasetSourceSet);
		Collections.sort(datasetSources,  new Comparator<DatasetSource>(){

			@Override
			public int compare(DatasetSource ds1, DatasetSource ds2) {
				return ds1.name().compareTo(ds2.name()); 
			}
		}); 
		
		return datasetSources;
	}
	
	public List<Taxon> getTaxons()
	{
		HashSet<Taxon> taxons = new HashSet<Taxon>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			taxons.addAll(adaptor.getTaxons());
		}		
		return new ArrayList<Taxon>(taxons);		
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
	
	public List<Dataset> getDatasets(us.kbase.kbasenetworks.core.NetworkType networkType) throws AdaptorException
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
			
	public Network buildNetwork(String datasetId) throws AdaptorException
	{
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{					
			if(adaptor.hasDataset(datasetId) )
			{
				Dataset dataset = adaptor.getDataset(datasetId);
				return adaptor.buildNetwork(dataset);
			}
		}	
		return null;
	}
	
	public Network buildFirstNeighborNetwork(List<String> datasetIds, List<Entity> entitis,
			List<String> edgeTypes) throws AdaptorException 
	{		
		List<Network> networks = new Vector<Network>();
		for(String datasetId: datasetIds)
		{
			for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
			{
				if(adaptor.hasDataset(datasetId) )
				{
					for(Entity entity: entitis)
					{
						Dataset dataset = adaptor.getDataset(datasetId);
						Network network = adaptor.buildFirstNeighborNetwork(dataset, entity, edgeTypes);
						if(network != null)
						{
							networks.add(network);
						}
					}
				}				
			}
		}			
		
		return buildUnionNetwork("First neighbour network", networks);			
	}

	
	
	public Network buildFirstNeighborNetwork(List<String> datasetIds, Entity entity,
			List<String> edgeTypes) throws AdaptorException 
	{		
		List<Network> networks = new Vector<Network>();
		for(String datasetId: datasetIds)
		{
			for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
			{
				if(adaptor.hasDataset(datasetId) )
				{
					Dataset dataset = adaptor.getDataset(datasetId);
					Network network = adaptor.buildFirstNeighborNetwork(dataset, entity, edgeTypes);
					if(network != null)
					{
						networks.add(network);
					}
				}				
			}
		}			
		
		return buildUnionNetwork("First neighbour network", networks);		
	}

	public Network buildInternalNetwork(List<String> datasetIds, List<Entity> entities,
			List<String> edgeTypes) throws AdaptorException 
	{		
		List<Network> networks = new Vector<Network>();
		
		for(String datasetId: datasetIds)
		{
			for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
			{
				if(adaptor.hasDataset(datasetId) )
				{
					Dataset dataset = adaptor.getDataset(datasetId);
					Network network = adaptor.buildInternalNetwork(dataset, entities, edgeTypes);
					if(network != null)
					{
						networks.add(network);
					}
				}				
			}
		}			
		
		return buildUnionNetwork("Internal network",networks);		
	}
	
	
	
	private Network buildUnionNetwork(String name, List<Network> networks) {
		Hashtable<String, Node> entityId2NodeHash =
			getNonredundantEntityId2NodeHash(networks);
			
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), name, graph);
		
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

	public Network buildFirstNeighborNetwork(List<String> datasetIds,
			Entity entity, List<String> edgeTypes, float cutOff) throws AdaptorException {
		Network network = buildFirstNeighborNetwork(datasetIds, entity, edgeTypes);
    	cutOffNetwork(network, cutOff);
		return network;
	}
	
	public Network buildFirstNeighborNetwork(List<String> datasetIds,
			List<Entity> entities, List<String> edgeTypes, double cutOff) throws AdaptorException {
		Network network = buildFirstNeighborNetwork(datasetIds, entities, edgeTypes);
    	cutOffNetwork(network, cutOff);
		return network;
	}	

	public Network buildInternalNetwork(List<String> datasetIds,
			List<Entity> entities, List<String> edgeTypes, double cutOff) throws AdaptorException {
		Network network = buildInternalNetwork(datasetIds, entities, edgeTypes);
    	cutOffNetwork(network, cutOff);
		return network;
	}	
	
    private void cutOffNetwork(Network network, double cutOff) {
    	Graph<Node,Edge> graph = network.getGraph();
    	List<Edge> deletedEdges = new ArrayList<Edge>();
    	for( Edge e : network.getGraph().getEdges()) {
    		//if( e.getConfidence() < cutOff) {
    		if( e.getStrength() < cutOff) {
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
