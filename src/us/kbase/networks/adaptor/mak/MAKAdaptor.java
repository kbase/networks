package us.kbase.networks.adaptor.mak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import us.kbase.networks.adaptor.AbstractAdaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.IdGenerator;
import us.kbase.networks.adaptor.mak.dao.ent.MAKBicluster;
import us.kbase.networks.adaptor.mak.dao.ent.MAKDataset;
import us.kbase.networks.adaptor.mak.dao.ent.MAKGene;
import us.kbase.networks.adaptor.mak.dao.hibernate.DataProvider;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class MAKAdaptor extends AbstractAdaptor{

	
	public static final String EDGE_MEMBER_OF_BICLUSTER = "Member of bicluster";		
	public static final String EDGE_MEMBERS_OF_SAME_BICLUSTER = "Members of same bicluster";		
	public static final List<EdgeType> DEFAULT_EDGE_TYPES = Arrays.asList(EdgeType.GENE_CLUSTER);
	
	public static final String ADAPTOR_PREFIX = "mak";
	
	public MAKAdaptor() throws AdaptorException{
		super(null);
	}
	
	@Override
	protected List<Dataset> loadDatasets() throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		DataProvider dataProvider = new DataProvider();
		try{
			List<MAKDataset> makDatasets = dataProvider.getDatasets();
			for(MAKDataset makDataset: makDatasets)
			{
				Dataset dataset = buildDataset(makDataset);
				datasets.add(dataset);
			}
		}
		finally{
			dataProvider.close();
		}
		return datasets;
	}
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity)
			throws AdaptorException {
		return buildFirstNeighborNetwork(dataset, entity, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity,
			List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);			
				
		// Build network
		DataProvider dataProvider = new DataProvider();
		try{
						
			if(entity.getType() == EntityType.GENE)
			{
				populateGeneFirstNeighborNetwork(dataProvider, network, dataset, entity.getId(), edgeTypes);
				
			} else if(entity.getType() == EntityType.BICLUSTER)
			{
				populateCLusterFirstNeighborNetwork(dataProvider, network, dataset, entity.getId(), edgeTypes);
				
			}
		}
		finally{
			dataProvider.close();
		}
		
		return network;						
	}

	
	

	private void populateCLusterFirstNeighborNetwork(DataProvider dataProvider,
			Network network, Dataset dataset, String clusterId,
			List<EdgeType> edgeTypes) {
				
		//1. Build query node
		MAKBicluster queryCluster = dataProvider.getBicluster(clusterId);
		if(queryCluster == null)
		{
			return;
		}
		Node queryNode = buildClusterNode( queryCluster ); 
		network.getGraph().addVertex(queryNode);		
		
		
		//2. Process GENE_CLUSTER edges
		if(edgeTypes.contains(EdgeType.GENE_CLUSTER))
		{
			// Collect members of cluster
			List<MAKGene> genes = queryCluster.getGenes();
							
			// Add gene nodes and edges
			for(MAKGene gene: genes)
			{
				Node geneNode = buildGeneNode(gene);
				network.getGraph().addVertex(geneNode);
				Edge edge = buildGeneBiclusterEdge(dataset); 
				network.getGraph().addEdge(edge, queryNode, geneNode,
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}					
		}	
	}

	private void populateGeneFirstNeighborNetwork(DataProvider dataProvider, Network network, Dataset dataset, String geneId, List<EdgeType> edgeTypes) {

		int datasetId = Integer.parseInt( IdGenerator.toLocalId(dataset.getId()) );
		
		//1. Build query node
		MAKGene queryGene = dataProvider.getGene(datasetId, geneId);
		if(queryGene == null)
		{
			return;
		}
		Node queryNode = buildGeneNode( queryGene ); 
		network.getGraph().addVertex(queryNode);
		
		//2. Process GENE_CLUSTER edges
		if(edgeTypes.contains(EdgeType.GENE_CLUSTER))
		{
			// Collect first-neighbor clusters (regulons)
			List<MAKBicluster> biclusters = dataProvider.getBiclusters(datasetId,queryGene.getKbaseId());
							
			// Add cluster nodes and edges
			for(MAKBicluster bicluster: biclusters)
			{
				Node clusterNode = buildClusterNode(bicluster);
				network.getGraph().addVertex(clusterNode);
				Edge edge = buildGeneBiclusterEdge(dataset); 
				network.getGraph().addEdge(edge, clusterNode, queryNode,
						edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
			}					
		}	
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
			throws AdaptorException {
		return buildInternalNetwork(dataset, entities, Arrays.asList(EdgeType.GENE_GENE) );
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> edgeTypes) throws AdaptorException {

		int datasetId = Integer.parseInt( IdGenerator.toLocalId(dataset.getId()) );
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "Internal network", graph);			
		
		if(!edgeTypes.contains(EdgeType.GENE_GENE)) {
			return network;
		}
		
		DataProvider dataProvider = new DataProvider();
		try{
		
			Hashtable<String, List<Node>> cluster2NodesHash = new Hashtable<String, List<Node>>();
			for(Entity entity: entities)
			{
				if(entity.getType() != EntityType.GENE) continue;
			
				Node geneNode = buildGeneNode(entity);
				graph.addVertex(geneNode);
				List<MAKBicluster> clusters = dataProvider.getBiclusters(datasetId, entity.getId());
				
				// Register clusters
				for(MAKBicluster cluster: clusters){
					List<Node> geneNodes = cluster2NodesHash.get(cluster.getKbaseId());
					if(geneNodes == null)
					{
						geneNodes = new ArrayList<Node>();
						cluster2NodesHash.put(cluster.getKbaseId(), geneNodes);						
					}
					geneNodes.add(geneNode);
				}
				
				// Add edges
				for(String clusterId: cluster2NodesHash.keySet())
				{
					List<Node> geneNodes = cluster2NodesHash.get(clusterId);
					for(int i = 0 ; i < geneNodes.size(); i++)
					{
						Node node1 = geneNodes.get(i);
						for(int j = i + 1; j < geneNodes.size(); j++)
						{
							Node node2 = geneNodes.get(j);
							Edge edge = new Edge(IdGenerator.Edge.nextId(), EDGE_MEMBERS_OF_SAME_BICLUSTER, dataset);
							graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);							
						}
					}
				}
				
			}		
		}
		finally{
			dataProvider.close();
		}		
		
		return network;
	}

	@Override
	public Network buildNetwork(Dataset dataset) throws AdaptorException {
		return buildNetwork(dataset, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes)
			throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}



	
	private Dataset buildDataset(MAKDataset makDataset) {
		List<Taxon> taxons = Arrays.asList(new Taxon(makDataset.getGenomeKBaseId()));
		
		
		return 
			new Dataset(
				IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX, "" + makDataset.getId()) ,
				makDataset.getName(),
				makDataset.getDescription(),
				NetworkType.REGULATORY_NETWORK,
				DatasetSource.MAK_BICLUSTER,
				taxons
			);		
	}

	private Edge buildGeneBiclusterEdge(Dataset dataset) {
		Edge edge =  new Edge(IdGenerator.Edge.nextId(), EDGE_MEMBER_OF_BICLUSTER, dataset);
		return edge;
	}


	private Node buildClusterNode(MAKBicluster bicluster) {
		 Node node = Node.buildClusterNode(
				 IdGenerator.Node.nextId(), 
				 bicluster.getName() + " bicluster" , 
				 new Entity(bicluster.getKbaseId(), EntityType.BICLUSTER));
		 
		 return node;
	}


	private Node buildGeneNode(MAKGene gene) {
		Node node = Node.buildGeneNode(
				IdGenerator.Node.nextId(), 
				gene.getKbaseId(), 
				new Entity(gene.getKbaseId(), EntityType.GENE));

		return node;
	}

	private Node buildGeneNode(Entity  geneEntity) {
		Node node = Node.buildGeneNode(
				IdGenerator.Node.nextId(), 
				geneEntity.getId(), 
				geneEntity);

		return node;
	}
	
	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}

}
