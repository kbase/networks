package us.kbase.networks;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import us.kbase.networks.core.Entity;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;
import us.kbase.networks.NetworksAPI;
import us.kbase.networks.adaptor.AdaptorException;

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
    	us.kbase.networks.core.DatasetSource datasetSource = null;
    	try{
    		datasetSource = 
    			Enum.valueOf(us.kbase.networks.core.DatasetSource.class, datasetSourceRef);
    	}catch(Exception e)
    	{
    		throw new Exception(datasetSourceRef + " is not a valid DatasetSource. Please use allDatasetSources() method to get a list of suuported DatasetSources.");
    	}
    	
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
    	us.kbase.networks.core.NetworkType networkType = null;
    	try{
    		networkType = Enum.valueOf(us.kbase.networks.core.NetworkType.class, networkTypeRef);
		}catch(Exception e)
		{
			throw new Exception(networkTypeRef + " is not a valid NetworkType. Please use allNetworkTypes() method to get a list of suuported NetworkTypes.");
		}
    	
    	
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(networkType);				
		return toClientDatasets(serverDatasets); 		
    }


    public List<Dataset> entity2Datasets(String entityId) throws Exception
    {
		List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets(Entity.toEntity(entityId));				
		return toClientDatasets(serverDatasets); 	
    }


    /**
     * Buid network methods
     **/
  
/*    
    public Network buildFirstNeighborNetwork(List<String> datasetIds, String entityId, List<String> edgeTypeRefs) throws Exception
    {
    	List<us.kbase.networks.core.EdgeType> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	
    	Entity entity = Entity.toEntity(entityId);    	
    	us.kbase.networks.core.Network network = api.buildFirstNeighborNetwork(datasetIds, entity, edgeTypes);
    	
    	return toClientNetwork(network);
    }
*/    

    public Network buildFirstNeighborNetwork(List<String> datasetIds, List<String> entityIds, List<String> edgeTypeRefs) throws Exception
    {
    	List<String> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	
    	List<Entity> entities = Entity.toEntities(entityIds);  	
    	us.kbase.networks.core.Network network = api.buildFirstNeighborNetwork(datasetIds, entities, edgeTypes);
    	
    	return toClientNetwork(network);
    }

 
    public Network buildFirstNeighborNetworkLimtedByStrength(List<String> datasetIds, List<String> entityIds, List<String> edgeTypeRefs, double cutOff) throws Exception
    {
    	List<String> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	List<Entity> entities = Entity.toEntities(entityIds);  	    	
    	
    	us.kbase.networks.core.Network network = api.buildFirstNeighborNetwork(datasetIds, entities, edgeTypes, cutOff);
    	
    	return toClientNetwork(network);
    }

    
	public Network buildInternalNetwork(List<String> datasetIds, List<String> entityIds, List<String> edgeTypeRefs) throws Exception
    {
    	List<String> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	List<Entity> entities = Entity.toEntities(entityIds);    	

    	us.kbase.networks.core.Network network = api.buildInternalNetwork(datasetIds, entities, edgeTypes);
    	
    	return toClientNetwork(network);
    }

	public Network buildInternalNetworkLimitedByStrength(List<String> datasetIds, List<String> entityIds, List<String> edgeTypeRefs, double cutOff) throws Exception
    {
    	List<String> edgeTypes = getEdgeTypes(edgeTypeRefs);
    	List<Entity> entities = Entity.toEntities(entityIds);    	
    	
    	us.kbase.networks.core.Network network = api.buildInternalNetwork(datasetIds, entities, edgeTypes, cutOff);
    	
    	return toClientNetwork(network);
    }
	
	
    public Network buildNetwork(String datasetId) throws Exception
    {
    	us.kbase.networks.core.Network network = api.buildNetwork(datasetId);
    	
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

/*	
	private List<us.kbase.networks.core.Dataset> getDatasets(List<String> datasetIds) {
		List<us.kbase.networks.core.Dataset> datasets = new ArrayList<us.kbase.networks.core.Dataset>();
		for(String datasetId: datasetIds)
		{
			us.kbase.networks.core.Dataset ds = new us.kbase.networks.core.Dataset(datasetId, null, null, null, null, (List<Taxon>)null);
			datasets.add(ds);
		}
		return datasets;
	}
*/
    private List<String> getEdgeTypes(List<String> edgeTypeRefs) throws Exception {
    	if(edgeTypeRefs == null) {
    		return null;
    	}
    	
    	List<String> edgeTypes = new ArrayList<String>();
    	
    	for(String edgeTypeRef: edgeTypeRefs)
    	{
    		String edgeType = null;
        	try{
        		edgeType = edgeTypeRef;        		
        	}catch(Exception e)
        	{
    			throw new Exception(edgeTypeRef + " is not a valid EdgeType.");
        	}
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
