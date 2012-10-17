package us.kbase.networks.adaptor.modelseed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

import us.kbase.CDMI.CDMI_API;
import us.kbase.CDMI.CDMI_EntityAPI;
import us.kbase.CDMI.fields_Model;
import us.kbase.CDMI.tuple_118;
import us.kbase.CDMI.tuple_132;
import us.kbase.CDMI.tuple_51;
import us.kbase.CDMI.tuple_83;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;

public class ModelSEEDAdaptor implements Adaptor {

	private CDMI_EntityAPI cdmi;
	private int uniqueIndex = 0;

	
	public ModelSEEDAdaptor() throws AdaptorException {
		super();
		try {
			this.cdmi = new CDMI_EntityAPI("http://bio-data-1.mcs.anl.gov/services/cdmi_api");
		} catch (MalformedURLException e) {
			throw new AdaptorException("Unable to initialize CDMI_EntityAPI", e);
		}
	}

	@Override
	public List<Dataset> getDatasets() throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		try {
			Map<String, fields_Model> models = cdmi.all_entities_Model(0, Integer.MAX_VALUE, Arrays.asList("id", "name"));

			for (String id : models.keySet()) {
				String name = models.get(id).name;
				List<tuple_132> genomeIds = cdmi.get_relationship_Models(Arrays.asList(id), new ArrayList<String>(), 
						new ArrayList<String>(), Arrays.asList("id"));
				List<Taxon> taxons = new ArrayList<Taxon>();
				for (tuple_132 tuple : genomeIds) {
					taxons.add(new Taxon(tuple.e_3.id));
				}
				datasets.add(new Dataset(getDatasetId(id), name, "Metabolic pathway for " + name + " genome.", NetworkType.METABOLIC_PATHWAY, 
						DatasetSource.MODELSEED, taxons));
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new AdaptorException("Error while getting models", e);
		}

		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(networkType == NetworkType.METABOLIC_PATHWAY)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		if(datasetSource == DatasetSource.MODELSEED)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		for (Dataset ds : getDatasets()) {
			for (Taxon t : ds.getTaxons()) {
				if (t.equals(taxon)) {
					datasets.add(ds);
				}
			}
		}
		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		if(networkType == NetworkType.METABOLIC_PATHWAY)		
			if(datasetSource == DatasetSource.MODELSEED)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}

	@Override
	public boolean hasDataset(Dataset dataset) throws AdaptorException {
		return getDatasets().contains(dataset);
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

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId) throws AdaptorException {
		return buildFirstNeighborNetwork(dataset, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId, List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(getNetworkId(), "", graph);	
		Node geneNode = Node.buildGeneNode(getNodeId(), geneId, new Entity(geneId));
		if( !edgeTypes.contains(EdgeType.GENE_CLUSTER) )
		{
			return network;
		}
		
		try {
			List<tuple_118> tps = cdmi.get_relationship_HasFunctional(Arrays.asList(geneId), new ArrayList<String>(),
					new ArrayList<String>(), Arrays.asList("id"));
			for (tuple_118 tp : tps) {
				List<tuple_83> tps2 = cdmi.get_relationship_IsIncludedIn(Arrays.asList(tp.e_3.id), 
						new ArrayList<String>(), new ArrayList<String>(), Arrays.asList("id")); 
				// filter duplicates
				Set<String> subsystems = new HashSet<String>();
				for (tuple_83 tp2 : tps2) {
					if (subsystems.contains(tp2.e_3.id)) continue; 
					Node ssNode = Node.buildClusterNode(getNodeId(), tp2.e_3.id, new Entity(tp2.e_3.id));
					graph.addVertex(ssNode);
					Edge edge = new Edge(getEdgeId(), "Member of subsystem", dataset);
					graph.addEdge(edge, geneNode, ssNode, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
					subsystems.add(tp2.e_3.id);
				}
			}
		} catch (Exception e) {
			throw new AdaptorException("Error in buildFirstNeighborNetwork for " + geneId, e);
		}
		
		
		return network;
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
			List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDatasetId(String id) {
		return RegPreciseAdaptor.DATASET_ID_PREFIX + id;
	}		
	
	private String getNodeId() {
		return RegPreciseAdaptor.NODE_ID_PREFIX + (uniqueIndex ++);
	}
	
	private String getEdgeId() {
		return RegPreciseAdaptor.EDGE_ID_PREFIX + (uniqueIndex++);
	}

	private String getNetworkId() {
		return RegPreciseAdaptor.NETWORK_ID_PREFIX + (uniqueIndex++);
	}	
}
