package us.kbase.networks.adaptor.mak;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
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

public class MAKAdaptor implements Adaptor{

	public static final String DATASET_ID_PREFIX = "kb|netdataset.";
	public static final String NETWORK_ID_PREFIX = "kb|net.";
	public static final String NODE_ID_PREFIX = "kb|netnode.";
	public static final String EDGE_ID_PREFIX = "kb|netedge.";
	
	public static final String EDGE_MEMBER_OF_BICLUSTER = "Member of bicluster";	
	
	
	public static final List<EdgeType> DEFAULT_EDGE_TYPES = Arrays.asList(EdgeType.GENE_CLUSTER);
	private int uniqueIndex = 0;	
	
	@Override
	public List<Dataset> getDatasets() throws AdaptorException {
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
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		
		List<Dataset> datasets = new Vector<Dataset>();
		if(networkType == NetworkType.REGULATORY_NETWORK)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		if(datasetSource == DatasetSource.MAK_BI_CLUSTER)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;	
	}

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

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();

		if(networkType == NetworkType.REGULATORY_NETWORK)		
			if(datasetSource == DatasetSource.MAK_BI_CLUSTER)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}

	@Override
	public boolean hasDataset(Dataset dataset) throws AdaptorException {
		
		boolean hasDataset = false;
		DataProvider dataProvider = new DataProvider();
		try{
			hasDataset = dataProvider.hasDataset(dataset.getId());
		}
		finally{
			dataProvider.close();
		}
		
		return hasDataset;
	}	
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId)
			throws AdaptorException {
		return buildFirstNeighborNetwork(dataset, geneId, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId,
			List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(getNetworkId(), "", graph);			
		
		// Build network
		DataProvider dataProvider = new DataProvider();
		try{			
			
			//1. Build query node
			MAKGene queryGene = dataProvider.getGene(dataset.getId(), geneId);
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
				List<MAKBicluster> biclusters = dataProvider.getBiclusters(dataset.getId(),queryGene.getKbaseId());
								
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
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds)
			throws AdaptorException {
		return buildInternalNetwork(dataset, geneIds, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
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
				makDataset.getKbaseId(),
				makDataset.getName(),
				makDataset.getDescription(),
				NetworkType.REGULATORY_NETWORK,
				DatasetSource.MAK_BI_CLUSTER,
				taxons
			);		
	}

	private Edge buildGeneBiclusterEdge(Node clusterNode, MAKGene queryGene,
			Dataset dataset) {
		Edge edge =  new Edge(getEdgeId(), EDGE_MEMBER_OF_BICLUSTER, dataset);
		return edge;
	}


	private Node buildClusterNode(MAKBicluster bicluster) {
		 Node node = Node.buildClusterNode(
				 getNodeId(), 
				 bicluster.getName() + " bicluster" , 
				 new Entity(bicluster.getKbaseId(), EntityType.BICLUSTER));
		 
		 return node;
	}


	private Node buildGeneNode(MAKGene gene) {
		Node node = Node.buildGeneNode(
				getNodeId(), 
				gene.getKbaseId(), 
				new Entity(gene.getKbaseId(), EntityType.GENE));

		return node;
	}

	private Set<EdgeType> getEdgeTypesSet(List<EdgeType> edgeTypes) {
		Set<EdgeType> edgeTypesSet = new HashSet<EdgeType>();
		edgeTypesSet.addAll(edgeTypes);
		return edgeTypesSet;
	}
	
	
	private String getNodeId() {
		return NODE_ID_PREFIX + (uniqueIndex ++);
	}
	
	private String getEdgeId() {
		return EDGE_ID_PREFIX + (uniqueIndex++);
	}
	
	private String getNetworkId() {
		return NETWORK_ID_PREFIX + (uniqueIndex++);
	}


	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}	

}
