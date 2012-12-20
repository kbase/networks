package us.kbase.networks.adaptor.modelseed;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.kbase.CDMI.CDMI_API;
import us.kbase.CDMI.CDMI_EntityAPI;
import us.kbase.CDMI.CDMI_EntityAPI_tuple_120;
import us.kbase.CDMI.CDMI_EntityAPI_tuple_134;
import us.kbase.CDMI.CDMI_EntityAPI_tuple_138;
import us.kbase.CDMI.CDMI_EntityAPI_tuple_85;
import us.kbase.CDMI.fields_Model;
import us.kbase.CDMI.fields_Subsystem;
import us.kbase.CDMI.row;
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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class ModelSEEDAdaptor extends AbstractAdaptor {

	private CDMI_EntityAPI cdmi;
	private CDMI_API cdmiAPI;
	public static final String ADAPTOR_PREFIX = "modelseed";

	
	public ModelSEEDAdaptor() throws AdaptorException {
		super(null);
	}
	
	@Override
	protected void init() throws AdaptorException{
		try {
			this.cdmi = new CDMI_EntityAPI("http://bio-data-1.mcs.anl.gov/services/cdmi_api");
		} catch (MalformedURLException e) {
			throw new AdaptorException("Unable to initialize CDMI_EntityAPI", e);
		}
		try {
			this.cdmiAPI = new CDMI_API("http://bio-data-1.mcs.anl.gov/services/cdmi_api");
		} catch (MalformedURLException e) {
			throw new AdaptorException("Unable to initialize CDMI_API", e);
		}
		super.init();
	}
	
	@Override
	protected List<Dataset> loadDatasets() throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		try {
			Map<String, fields_Model> models = cdmi.all_entities_Model(0, Integer.MAX_VALUE, Arrays.asList("id"));
			List<String> modelids = new ArrayList<String>(models.keySet());
			List<CDMI_EntityAPI_tuple_134> returned = cdmi.get_relationship_Models(modelids, Arrays.asList("id", "name"), 
					new ArrayList<String>(), Arrays.asList("id"));
			Iterator<CDMI_EntityAPI_tuple_134> it = returned.iterator();

			while (it.hasNext()) {
				CDMI_EntityAPI_tuple_134 tuple = it.next();
				String modelid = tuple.e_1.id;
				String name = tuple.e_1.name;
				String genomeId = tuple.e_3.id;
				List<Taxon> taxons = new ArrayList<Taxon>();
				taxons.add(new Taxon(genomeId));
				
				String localId = IdGenerator.toLocalId(modelid);
				datasets.add(new Dataset(
						IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX, localId),
						name, 
						"Subsystems for " + name + " genome.", 
						NetworkType.METABOLIC_SUBSYSTEM, 
						DatasetSource.MODELSEED, taxons));
			}	
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new AdaptorException("Error while getting models", e);
		}

		return datasets;
	}
	
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		
		if (entity.getType() == EntityType.GENE) {
			List<CDMI_EntityAPI_tuple_138> genomeIds;
			try {
				genomeIds = cdmi.get_relationship_IsOwnedBy(Arrays.asList(entity.getId()), new ArrayList<String>(), 
						new ArrayList<String>(), Arrays.asList("id"));
			} catch (Exception e) {
				throw new AdaptorException("Error while accessing CDMI", e);
			}
			if (genomeIds.size() == 1) {
				Taxon et = new Taxon(genomeIds.get(0).e_3.id);
				datasets.addAll(getDatasets(et));
			}
		}
		
		return datasets;
	}

	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(networkType == NetworkType.METABOLIC_SUBSYSTEM)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}
	*/

	/*
	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(datasetSource == DatasetSource.MODELSEED)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}
	*/

	/*
	@Override
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		List<tuple_131> modelId;
		try {
			modelId = cdmi.get_relationship_IsModeledBy(Arrays.asList(taxon.getGenomeId()), new ArrayList<String>(), 
					new ArrayList<String>(), Arrays.asList("id", "name"));
		} catch (Exception e) {
			throw new AdaptorException("Error while accessing CDMI", e);
		}
		if (modelId.size() == 1) {
			String name = modelId.get(0).e_3.name;
			
			String datasetLocalId = IdGenerator.toLocalId(taxon.getGenomeId());
			datasets.add(new Dataset(
					IdGenerator.Dataset.toKBaseId(ADAPTOR_PREFIX, datasetLocalId),
					name, 
					"Subsystems for " + name + " genome.", 
					NetworkType.METABOLIC_SUBSYSTEM, 
					DatasetSource.MODELSEED, Arrays.asList(taxon)));
		}

		return datasets;
	}
	*/

	/*
	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		if(networkType == NetworkType.METABOLIC_SUBSYSTEM)		
			if(datasetSource == DatasetSource.MODELSEED)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}
	*/
	
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

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity) throws AdaptorException {
		return buildFirstNeighborNetwork(dataset, entity, Arrays.asList(EdgeType.GENE_CLUSTER));
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity, List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);	

		if(edgeTypes.contains(EdgeType.GENE_CLUSTER)) {
			if (entity.getType() == EntityType.GENE) {
				// PSN: Entity is immutable class
				//Node geneNode = Node.buildGeneNode(IdGenerator.Node.nextId(), entity.getId(), new Entity(entity.getId(), EntityType.GENE));
				Node geneNode = Node.buildGeneNode(IdGenerator.Node.nextId(), entity.getId(), entity);
				
				graph.addVertex(geneNode);
				for (fields_Subsystem fs : getSubsystemFieldsForGene(entity.getId())) {
					Node ssNode = Node.buildClusterNode(IdGenerator.Node.nextId(), fs.id, toSubsystemsEntity(fs.id));
					graph.addVertex(ssNode);
					Edge edge = new Edge(IdGenerator.Edge.nextId(), "Member of subsystem", dataset);
					graph.addEdge(edge, ssNode, geneNode, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}
			}
			else if (entity.getType() == EntityType.SUBSYSTEM) {
				Node ssNode = Node.buildClusterNode(IdGenerator.Node.nextId(), entity.getId(), entity);
				graph.addVertex(ssNode);
				for (String geneId : getGenesForSubsystem( toSubsystemsId(entity), dataset.getTaxons().get(0).getGenomeId())) {
					Node geneNode = Node.buildGeneNode(IdGenerator.Node.nextId(), geneId, toGeneEntity(geneId));
					Edge edge = new Edge(IdGenerator.Edge.nextId(), "Member of subsystem", dataset);
					graph.addEdge(edge, ssNode, geneNode, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
				}	
			}
		}
		return network;
		
		// $return = $obj->subsystems_to_fids($subsystems, $genomes)
	}
	
	
	private Set<String> getGenesForSubsystem(String ssId, String genomeId) throws AdaptorException {
		Set<String> geneIds = new HashSet<String>();
		try {
			Map<String, Map<String, row>> returned = cdmiAPI.subsystems_to_spreadsheets(Arrays.asList(ssId), Arrays.asList(genomeId));
			for (Map<String, row> rows : returned.values()) {
				for (row r : rows.values()) {
					for (List<String> fids : r.e_2.values()) {
						geneIds.addAll(fids);
					}
				}
			}
			
		} catch (Exception e) {
			throw new AdaptorException("Error getting subsystem genes for " + ssId, e);
		}
		return geneIds;
	}

	private Collection<fields_Subsystem> getSubsystemFieldsForGene(String geneId) throws AdaptorException {
		Map<String, fields_Subsystem> fs = new HashMap<String, fields_Subsystem>();
		try {
			List<CDMI_EntityAPI_tuple_120> tps = cdmi.get_relationship_HasFunctional(Arrays.asList(geneId), new ArrayList<String>(),
					new ArrayList<String>(), Arrays.asList("id"));
			for (CDMI_EntityAPI_tuple_120 tp : tps) {
				List<CDMI_EntityAPI_tuple_85> tps2 = cdmi.get_relationship_IsIncludedIn(Arrays.asList(tp.e_3.id), 
						new ArrayList<String>(), new ArrayList<String>(), Arrays.asList("id")); 
				for (CDMI_EntityAPI_tuple_85 tp2 : tps2) {
					fs.put(tp2.e_3.id, tp2.e_3);
				}
			}
		} catch (Exception e) {
			throw new AdaptorException("Error getting subsystem fields for " + geneId, e);
		}
		return fs.values();
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities) throws AdaptorException {
		// PSN: the way you construct network, it should be GENE_GENE
		//return buildInternalNetwork(dataset, entities, Arrays.asList(EdgeType.GENE_CLUSTER));
		return buildInternalNetwork(dataset, entities, Arrays.asList(EdgeType.GENE_GENE));
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(IdGenerator.Network.nextId(), "", graph);	
		
		// PSN: the way you construct network, it should be GENE_GENE
		//if( !edgeTypes.contains(EdgeType.GENE_CLUSTER) )
		if( !edgeTypes.contains(EdgeType.GENE_GENE) )
		{
			return network;
		}

		// find out what subsystems genes are in
		Map<String,List<Node>> ssToGeneNodes = new HashMap<String,List<Node>>();

		for (Entity entity: entities) {
			
			// PSN: according to your logic the nodes can be  genes only
			if(entity.getType() != EntityType.GENE) continue;
			
			String geneId = entity.getId();
			
			// PSN: Entity is immutable class
			//Node geneNode = Node.buildGeneNode(IdGenerator.Node.nextId(), geneId, new Entity(geneId, EntityType.GENE));
			Node geneNode = Node.buildGeneNode(IdGenerator.Node.nextId(), geneId, entity);
			
			graph.addVertex(geneNode);

			for (fields_Subsystem fs : getSubsystemFieldsForGene(geneId)) {
				List<Node> geneNodes = ssToGeneNodes.get(fs.id);
				if (geneNodes == null) {
					geneNodes = new ArrayList<Node>();
					ssToGeneNodes.put(fs.id,geneNodes);
				}
				geneNodes.add(geneNode);
			}
		}
		
		// build edges between genes in same subsystems; but don't build duplicate edges
		Map<Node, Set<Node>> genePairs = new HashMap<Node, Set<Node>>();
		
		for (String ss : ssToGeneNodes.keySet()) {
			for (Node node1 : ssToGeneNodes.get(ss)) {
				if (! genePairs.containsKey(node1)) {genePairs.put(node1, new HashSet<Node>());}
				for (Node node2 : ssToGeneNodes.get(ss)) {
					if (! genePairs.containsKey(node2)) {genePairs.put(node2, new HashSet<Node>());}
					if (node1 == node2) {continue;}
					if (genePairs.get(node1).contains(node2) || genePairs.get(node2).contains(node1)) {continue;}
					Edge edge = new Edge(IdGenerator.Edge.nextId(), "Member of same subsystem", dataset);
					graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
					genePairs.get(node1).add(node2);
				}
			}
		}
		
		return network;
	}
	
	private Entity toSubsystemsEntity(String ssId) {		
		return new Entity( "kb|subsystem." + ssId , EntityType.SUBSYSTEM);
	}

	private Entity toGeneEntity(String geneId) {		
		return new Entity( geneId , EntityType.GENE);
	}

	private String toSubsystemsId(Entity entity) {		
		return IdGenerator.toLocalId(entity.getId());
	}
	
}
