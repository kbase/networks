package us.kbase.networks.rpc.server;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.kbase.networks.NetworksAPI;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;
import us.kbase.networks.rpc.client.Dataset;
import us.kbase.networks.rpc.client.DatasetSource;
import us.kbase.networks.rpc.client.Network;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class NetworksService {

	private NetworksAPI api; 
	private static ObjectMapper m = new ObjectMapper();
	private static JsonFactory jf = new JsonFactory();
	
	public static void main(String[] args) throws Exception {

		NetworksService nw = new NetworksService();
		nw.allDatasetSources();
		
		
/*		
		List<Dataset> datasets = new NetworksService().entity2Datasets("kb|g.21765.CDS.967");
		for(Dataset ds: datasets)
		{
			System.out.println(ds.id + "\t" + ds.sourceReference);
		}
*/		
	}
	
	
	public NetworksService() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();
	}
	
	/** 
	 * All datasets, datasources, and network types available in KBase 
	 */	
	public List<Dataset> allDatasets() throws Exception
    {
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets();				
		return toClientDatasets(serverDatasets); 		
    }



	public List<DatasetSource> allDatasetSources() throws Exception
    {
		List<us.kbase.networks.core.DatasetSource> serverDatasetSources =  api.getDatasetSources();
		return toClientDatasetSources(serverDatasetSources);
    }

	public List<String> allNetworkTypes() throws Exception
    {
		List<us.kbase.networks.core.NetworkType> serverNetworkTypes =  api.getNetworkTypes();
		return toClientNetworkTypes(serverNetworkTypes);
    }


	/** 
     * Datasets of a given type available in KBase
     **/
    public List<Dataset> datasetSource2Datasets(String datasetSourceRef) throws Exception
    {
    	us.kbase.networks.core.DatasetSource datasetSource = 
    		Enum.valueOf(us.kbase.networks.core.DatasetSource.class, datasetSourceRef);
    	
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(datasetSource);				
		return toClientDatasets(serverDatasets); 		
    }


    public List<Dataset> taxon2Datasets(String genomeId) throws Exception
    {
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(new Taxon(genomeId));				
		return toClientDatasets(serverDatasets); 		
    }


    public List<Dataset> networkType2Datasets(String networkTypeRef) throws Exception
    {
    	us.kbase.networks.core.NetworkType networkType = 
    		Enum.valueOf(us.kbase.networks.core.NetworkType.class, networkTypeRef);
    	
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(networkType);				
		return toClientDatasets(serverDatasets); 		
    }


    public List<Dataset> entity2Datasets(String entityId) throws Exception
    {
    	//TODO question: how to work with entity type.... ideally, it should automatically identified from entityId
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(new Entity(entityId, EntityType.GENE));				
		return toClientDatasets(serverDatasets); 	
    }


    /**
     * Buid network methods
     **/
    public Network buildFirstNeighborNetwork(List<String> datasetIds, String geneId, List<String> edgeTypeRefs) throws Exception
    {
    	List<us.kbase.networks.core.EdgeType> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	List<us.kbase.networks.core.Dataset> datasets = getDatasets(datasetIds);
    	
    	us.kbase.networks.core.Network network = api.buildFirstNeighborNetwork(datasets, geneId, edgeTypes);
    	
    	return toClientNetwork(network);
    }


	public Network buildInternalNetwork(List<String> datasetIds, List<String> geneIds, List<String> edgeTypeRefs) throws Exception
    {
    	List<us.kbase.networks.core.EdgeType> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	List<us.kbase.networks.core.Dataset> datasets = getDatasets(datasetIds);
    	
    	us.kbase.networks.core.Network network = api.buildInternalNetwork(datasets, geneIds, edgeTypes);
    	
    	return toClientNetwork(network);
    }

    public Network buildNetwork(String datasetId) throws Exception
    {
    	us.kbase.networks.core.Dataset dataset =  getDatasets(Arrays.asList(datasetId)).get(0);
    		
    	us.kbase.networks.core.Network network = api.buildNetwork(dataset);
    	
    	return toClientNetwork(network);
    }

    /**
     * Private methods
     **/
    private String toJson(Object obj) throws IOException
    {
		StringWriter sw = new StringWriter();
		JsonGenerator jg = jf.createJsonGenerator(sw);
		jg.useDefaultPrettyPrinter();
		m.writeValue(jg, obj);
		sw.close();
		
		return sw.toString();
    }
    
	@SuppressWarnings("unchecked")
	private List<Dataset> toClientDatasets(
			List<us.kbase.networks.core.Dataset> serverDatasets) throws JsonParseException, JsonMappingException, IOException {
		
		return 	(List<Dataset>) m.readValue( toJson(serverDatasets) , new TypeReference<List<Dataset>>() {}); 
	}
    
    @SuppressWarnings("unchecked")
	private List<DatasetSource> toClientDatasetSources(
			List<us.kbase.networks.core.DatasetSource> serverDatasetSources) throws JsonParseException, JsonMappingException, IOException {
    	
		return 	(List<DatasetSource>) m.readValue( toJson(serverDatasetSources) , new TypeReference<List<DatasetSource>>() {}); 
	}
	
    @SuppressWarnings("unchecked")
	private List<String> toClientNetworkTypes(List<NetworkType> serverNetworkTypes) throws JsonParseException, JsonMappingException, IOException {
    	
		return 	(List<String>) m.readValue( toJson(serverNetworkTypes) , new TypeReference<List<String>>() {}); 
	}

	private Network toClientNetwork(us.kbase.networks.core.Network network) throws JsonParseException, JsonMappingException, IOException {
		return 	(Network) m.readValue( toJson(network) , Network.class); 
	}

	private List<us.kbase.networks.core.Dataset> getDatasets(List<String> datasetIds) {
		List<us.kbase.networks.core.Dataset> datasets = new ArrayList<us.kbase.networks.core.Dataset>();
		for(String datasetId: datasetIds)
		{
			us.kbase.networks.core.Dataset ds = new us.kbase.networks.core.Dataset(datasetId, null, null, null, null, (List<Taxon>)null);
			datasets.add(ds);
		}
		return datasets;
	}

    private List<EdgeType> getEdgeTypes(List<String> edgeTypeRefs) {
    	if(edgeTypeRefs == null) {
    		return null;
    	}
    	
    	List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
    	
    	for(String edgeTypeRef: edgeTypeRefs)
    	{
        	EdgeType edgeType = Enum.valueOf(EdgeType.class, edgeTypeRef);
        	if(edgeType != null)
        	{
        		edgeTypes.add(edgeType);
        	}    		
    	}
    	
		return edgeTypes;
	}    
    
/*
 * Unfortunately does not work... 
 *     
	@SuppressWarnings("unchecked")
	private <T> List<T> toClientList(String json, T tt) throws JsonParseException, JsonMappingException, IOException {
		return (List<T>) m.readValue(json, new TypeReference<List<T>>() {});
	}    

	@SuppressWarnings("unchecked")
	private <T> T toClientType(String json, T tt) throws JsonParseException, JsonMappingException, IOException {
		return (T) m.readValue(json, new TypeReference<T>() {});
	}
*/	    
	
}
