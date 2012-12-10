package us.kbase.networks.adaptor.mak;

import java.util.Arrays;
import java.util.HashSet;
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


	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		
		List<Dataset> datasets = new Vector<Dataset>();
		if(networkType == NetworkType.REGULATORY_NETWORK)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}
	*/


	/*
	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		if(datasetSource == DatasetSource.MAK_BICLUSTER)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;	
	}
	*/

	/*
	@Override
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		DataProvider dataProvider = new DataProvider();
		try{
			List<MAKDataset> makDatasets = dataProvider.getDatasetsByGenomeKBaseId(taxon.getGenomeId());
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
	*/

	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();

		if(networkType == NetworkType.REGULATORY_NETWORK)		
			if(datasetSource == DatasetSource.MAK_BICLUSTER)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}
	*/
	
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
		
		int datasetId = Integer.parseInt( IdGenerator.toLocalId(dataset.getId()) );
		
		// Build network
		DataProvider dataProvider = new DataProvider();
		try{			
			
			//1. Build query node
			MAKGene queryGene = dataProvider.getGene(datasetId, entity.getId());
			if(queryGene == null)
			{
				return network;
			}
			Node queryNode = buildGeneNode( queryGene ); 
			graph.addVertex(queryNode);
			
			//2. Process GENE_CLUSTER edges
			Set<EdgeType> edgeTypesSet = getEdgeTypesSet(edgeTypes);
			if(edgeTypesSet.contains(EdgeType.GENE_CLUSTER))
			{
				// Collect first-neighbor clusters (regulons)
				List<MAKBicluster> biclusters = dataProvider.getBiclusters(datasetId,queryGene.getKbaseId());
								
				// Add cluster nodes and edges
				for(MAKBicluster bicluster: biclusters)
				{
					Node clusterNode = buildClusterNode(bicluster);
					graph.addVertex(clusterNode);
					Edge edge = buildGeneBiclusterEdge(clusterNode, queryGene, dataset); 
					graph.addEdge(edge, clusterNode, queryNode,
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}					
			}	
		}
		finally{
			dataProvider.close();
		}
		
		return network;						
	}


	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
			throws AdaptorException {
		return buildInternalNetwork(dataset, entities, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> edgeTypes) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
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

	private Edge buildGeneBiclusterEdge(Node clusterNode, MAKGene queryGene,
			Dataset dataset) {
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

	private Set<EdgeType> getEdgeTypesSet(List<EdgeType> edgeTypes) {
		Set<EdgeType> edgeTypesSet = new HashSet<EdgeType>();
		edgeTypesSet.addAll(edgeTypes);
		return edgeTypesSet;
	}
	

	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}

}
