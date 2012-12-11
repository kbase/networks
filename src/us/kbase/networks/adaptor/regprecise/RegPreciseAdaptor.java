package us.kbase.networks.adaptor.regprecise;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import us.kbase.networks.adaptor.AbstractAdaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.IdGenerator;
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

import com.lbl.regprecise.dao.hibernate.ConstrainedDataProvider;
import com.lbl.regprecise.dao.hibernate.StatusConstrainedDataProvider;
import com.lbl.regprecise.dto.KBaseGene2GeneDTO;
import com.lbl.regprecise.dto.KBaseGeneDTO;
import com.lbl.regprecise.dto.KBaseRegulatorDTO;
import com.lbl.regprecise.dto.KBaseRegulonDTO;
import com.lbl.regprecise.dto.RegulomeStatDTO;
import com.lbl.regprecise.ent.Term;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class RegPreciseAdaptor extends AbstractAdaptor{

	public static final String NODE_PROPERTY_REGULATION_TYPE = "Regulation type";
	public static final String NODE_PROPERTY_LOCUS_TAG = "LocusTag";
	public static final String NODE_PROPERTY_REGULATOR = "Regulator";
	
	public static final String EDGE_MEMBER_OF_REGULON = "Member of regulon";
	public static final String EDGE_REGULATED_BY = "Regulated by";
	public static final String EDGE_CO_REGUALTED = "Co-regualted";	
	
	public static final String ADAPTOR_PREFIX = "regprecise";
	
	public static final List<EdgeType> DEFAULT_EDGE_TYPES = Arrays.asList(EdgeType.GENE_CLUSTER);	

	
	public RegPreciseAdaptor() throws AdaptorException
	{
		super(null);
	}
	
	
	@Override
	protected List<Dataset> loadDatasets() {
		List<Dataset> datasets = new Vector<Dataset>();
		ConstrainedDataProvider dataProvider = getDataProvider();
		try{
			List<RegulomeStatDTO> regulomes = dataProvider.getRegulomeStatDTOs();
			for(RegulomeStatDTO regulome: regulomes)
			{
				Dataset dataset = buildDataset(regulome);
				datasets.add(dataset);
			}
		}
		finally{
			dataProvider.close();
		}
		return datasets;
	}


	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity) {
		return buildFirstNeighborNetwork(dataset, entity, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity,
			List<EdgeType> edgeTypes) {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);	
		
		// Check if either GENE_GENE or GENE_CLUSTER edge type was requested 
		Set<EdgeType> edgeTypesSet = getEdgeTypesSet(edgeTypes);
		if( !edgeTypesSet.contains(EdgeType.GENE_GENE) && !edgeTypesSet.contains(EdgeType.GENE_CLUSTER) )
		{
			return network;
		}
		
		// Build network
		ConstrainedDataProvider dataProvider = getDataProvider();
		try{			
			
			//1. Build query node
			KBaseGeneDTO queryGene = getQueryGene(dataProvider, entity.getId());
			if(queryGene == null)
			{
				return network;
			}
			Node queryNode = buildGeneNode( queryGene ); 
			graph.addVertex(queryNode);
			
			//2. Process GENE_CLUSTER edges
			if(edgeTypesSet.contains(EdgeType.GENE_CLUSTER))
			{
				// Collect first-neighbor clusters (regulons)
				List<KBaseRegulonDTO> regulons = getRegulons(dataProvider, queryGene);
					
				System.out.println("Query gene: " + queryGene.getKbaseId() 
						+ " has regulons " + (regulons != null? "" + regulons.size() : regulons) 
						+ " in the dataset " + dataset.getId());
				// Add cluster nodes and edges
				for(KBaseRegulonDTO regulon: regulons)
				{
					Node regulonNode = buildRegulonNode(regulon);
					graph.addVertex(regulonNode);
					Edge edge = buildGeneRegulonEdge(regulon, queryGene, dataset); 
					graph.addEdge(edge, regulonNode, queryNode,
							edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}					
			}
			
			//3. Process GENE_GENE edges
			if(edgeTypesSet.contains(EdgeType.GENE_GENE))
			{
				// Process regulators
				List<KBaseRegulatorDTO> regulators = getRegulators(dataProvider, queryGene);
				if(regulators != null)
				for(KBaseRegulatorDTO regulator: regulators)
				{
					if(regulator.getKbaseId() == null) continue;
					Node regulatorNode = buildRegulatorNode(regulator);
					graph.addVertex(regulatorNode);
					Edge edge = buildGeneRegulatorEdge(regulator, queryGene, dataset); 
					graph.addEdge(edge, regulatorNode, queryNode, 
							edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);					
				}			
				
				//Process genes from the same regulon
				List<KBaseGeneDTO> targetGenes = getCoregulatedGenes(dataProvider, queryGene);
				if(targetGenes != null)
				for(KBaseGeneDTO targetGene: targetGenes)
				{
					if(targetGene.getKbaseId() == null) continue;
					Node targetGeneNode = buildGeneNode(targetGene);
					graph.addVertex(targetGeneNode);
					Edge edge = buildGeneGeneEdge(targetGene, queryGene, dataset); 
					graph.addEdge(edge, targetGeneNode, queryNode,
							edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);					
				}
			}							
		}
		finally{
			dataProvider.close();
		}
		
		return network;						
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities) {
		return buildInternalNetwork(dataset, entities, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> edgeTypes) {
		
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);	
		
		// Check if either GENE_GENE or GENE_CLUSTER edge type was requested 
		Set<EdgeType> edgeTypesSet = getEdgeTypesSet(edgeTypes);
		if( !edgeTypesSet.contains(EdgeType.GENE_GENE) )
		{
			return network;
		}
		
		// Build network
		ConstrainedDataProvider dataProvider = getDataProvider();
		try{			
			
			List<String> geneIds = Entity.toEntityIds(entities);
			
			//1. Build query nodes
			List<KBaseGeneDTO> queryGenes = dataProvider.getRegulatedGenes(geneIds);
			Hashtable<String,Node> queryNodesHash = new Hashtable<String,Node>();
			for(KBaseGeneDTO queryGene: queryGenes)
			{
				Node queryNode = buildGeneNode( queryGene );
				queryNodesHash.put(queryGene.getKbaseId(), queryNode);
				graph.addVertex(queryNode);
			}
			
			
			//2. Process GENE_GENE edges
			if(edgeTypesSet.contains(EdgeType.GENE_GENE))
			{				
				List<KBaseGene2GeneDTO> genePairs = dataProvider.getCoregulatedGenePairs(geneIds);				
				for(KBaseGene2GeneDTO genePair: genePairs)
				{
//					System.out.println(genePair.getGeneKBaseId1() + "\t" + genePair.getGeneKBaseId2() + "\t" + genePair.getRegulatorName());
					Node node1 = queryNodesHash.get(genePair.getGeneKBaseId1());
					Node node2 = queryNodesHash.get(genePair.getGeneKBaseId2());
					Edge edge = buildGeneGeneEdge(genePair, dataset); 
					graph.addEdge(edge, node1, node2,
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
	public Network buildNetwork(Dataset dataset) {
		return buildNetwork(dataset, DEFAULT_EDGE_TYPES);
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private Dataset buildDataset(RegulomeStatDTO regulome)
	{
		List<Taxon> taxons = Arrays.asList(new Taxon(regulome.getGenomeKBaseId()));
		return 
			new Dataset(
				getDatasetId(regulome.getRegulomeId()),
				regulome.getGenomeName(),
				"Regulome for " + regulome.getGenomeName() + " genome.",
				NetworkType.REGULATORY_NETWORK,
				DatasetSource.REGPRECISE,
				taxons
			);		
	}
	
	/*
	private Dataset buildDataset(Regulome regulome) {
		List<Taxon> taxons = Arrays.asList(new Taxon(regulome.getGenome().getKbaseId()));		
		return 
			new Dataset(
					getDatasetId(regulome.getId()),
					regulome.getGenome().getName(),
					"Regulome for " + regulome.getGenome().getName() + " genome.",
					NetworkType.REGULATORY_NETWORK,
					DatasetSource.REGPRECISE,
					taxons
		);		
	}
	*/
	
	private ConstrainedDataProvider getDataProvider()
	{
//		System.out.print("RegPreciseAdaptor: Get connection...");
		ConstrainedDataProvider dataProvider = new StatusConstrainedDataProvider(Term.TERM_REGULOG_STATE_PUBLIC, Term.TERM_COLLECTION_STATE_PUBLIC);
//		System.out.println("Done!"); 
		
		return dataProvider; 
	}

		
	private String getDatasetId(int regulomeId) {
		return IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX, "" + regulomeId);
	}		

	/*
	private Regulome getRegulome(ConstrainedDataProvider dataProvider, Dataset dataset)
	{
		String regulomeId = getRegulomeId(dataset.getId());		
		return dataProvider.getRegulomeByKbaseId(regulomeId);
	}
	*/

	
	private Set<EdgeType> getEdgeTypesSet(List<EdgeType> edgeTypes) {
		Set<EdgeType> edgeTypesSet = new HashSet<EdgeType>();
		edgeTypesSet.addAll(edgeTypes);
		return edgeTypesSet;
	}

	private Node buildGeneNode(KBaseGeneDTO gene) {
		
		Node node = Node.buildGeneNode(
				IdGenerator.Node.nextId(), 
				gene.getName(), 
				new Entity(gene.getKbaseId(), EntityType.GENE));
		if(gene.getLocusTag() != null)
		{
			node.addProperty(NODE_PROPERTY_LOCUS_TAG, gene.getLocusTag());
		}
		return node;
	}
	
	private Node buildRegulonNode(KBaseRegulonDTO regulon) {
		 Node node = Node.buildClusterNode(
				 IdGenerator.Node.nextId(), 
				 regulon.getRegulatorName() + " regulon" , 
				 new Entity(regulon.getKbaseId(), EntityType.REGULON));
		 
		 node.addProperty(NODE_PROPERTY_REGULATION_TYPE, regulon.getRegulationType());
		 return node;
	}

	private Node buildRegulatorNode(KBaseRegulatorDTO regulator) {
		Node node = Node.buildGeneNode(
				IdGenerator.Node.nextId(), 
				regulator.getName(),
				new Entity(regulator.getKbaseId(), EntityType.GENE));
		
		node.addProperty(NODE_PROPERTY_REGULATOR, "");
		if(regulator.getLocusTag() != null)
		{
			node.addProperty(NODE_PROPERTY_LOCUS_TAG, regulator.getLocusTag());
		}
		
		return node;
	}

	private Edge buildGeneRegulonEdge(KBaseRegulonDTO regulon, KBaseGeneDTO queryGene,
			Dataset dataset) {
		Edge edge =  new Edge(IdGenerator.Edge.nextId(), EDGE_MEMBER_OF_REGULON, dataset);
		//TODO add edge properties
		return edge;
	}	
	
	private Edge buildGeneRegulatorEdge(KBaseRegulatorDTO regulator, KBaseGeneDTO queryGene,
			Dataset dataset) {
		Edge edge =  new Edge(IdGenerator.Edge.nextId(), EDGE_REGULATED_BY, dataset);
		//TODO add edge properties		
		return edge;
	}
	
	private Edge buildGeneGeneEdge(KBaseGeneDTO targetGene, KBaseGeneDTO queryGene, Dataset dataset) {
		Edge edge =  new Edge(IdGenerator.Edge.nextId(), EDGE_CO_REGUALTED, dataset);
		//TODO add edge properties		
		return edge;
	}
	
	private Edge buildGeneGeneEdge(KBaseGene2GeneDTO genePair, Dataset dataset) {
		Edge edge =  new Edge(IdGenerator.Edge.nextId(), EDGE_CO_REGUALTED + ": " + genePair.getRegulatorName(), dataset);
		//TODO add edge properties		
		return edge;
	}
	
	
	private List<KBaseGeneDTO> getCoregulatedGenes(ConstrainedDataProvider dataProvider, KBaseGeneDTO queryGene) {
		
		List<String> geneIds = Arrays.asList(queryGene.getKbaseId());
		Map<String, List<KBaseGeneDTO>> kbaseId2targetGenesMap = 
			dataProvider.getCoregualtedGenes(geneIds);
		
		return kbaseId2targetGenesMap.get(queryGene.getKbaseId());
	}

	private List<KBaseRegulatorDTO> getRegulators(ConstrainedDataProvider dataProvider, KBaseGeneDTO queryGene) {
		List<String> geneIds = Arrays.asList(queryGene.getKbaseId());
		Map<String, List<KBaseRegulatorDTO>> kbaseId2regulatorsMap = 
			dataProvider.getRegulators(geneIds);
		
		return kbaseId2regulatorsMap.get(queryGene.getKbaseId());
	}

	private List<KBaseRegulonDTO> getRegulons(ConstrainedDataProvider dataProvider, KBaseGeneDTO queryGene) {
		List<String> geneIds = Arrays.asList(queryGene.getKbaseId());
		Map<String, List<KBaseRegulonDTO>> kbaseId2regulonsMap = 
			dataProvider.getRegulons(geneIds);
		
		return kbaseId2regulonsMap.get(queryGene.getKbaseId());
	}

	private KBaseGeneDTO getQueryGene(ConstrainedDataProvider dataProvider,
			String geneId) {
		List<String> geneIds = Arrays.asList(geneId);
		List<KBaseGeneDTO> genes = dataProvider.getRegulatedGenes(geneIds);
		return genes.size() > 0 ? genes.get(0) : null;
	}

	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		// TODO Auto-generated method stub
		return null;
	}	
}
