package us.kbase.networks.rpc.server;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.networks.NetworksAPI;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.rpc.client.Dataset;
import us.kbase.networks.rpc.client.Network;
import us.kbase.networks.rpc.client.Parameter;

public class NetworksService {

	private NetworksAPI api; 
	private static ObjectMapper m = new ObjectMapper();
	private static JsonFactory jf = new JsonFactory();
	
	public NetworksService() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();
	}
	
	public List<Dataset> getDatasets(List<Parameter> parameters) throws AdaptorException, IOException
	{
		List<Dataset> datasets = null;
		if(parameters == null || parameters.size() == 0)
		{
			 List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets();
			 // Serialize server datasets to json
			 
			 	// Please use StringStream to have string dump output.
				FileWriter fw = new FileWriter("testFile.json");
				JsonGenerator jg = jf.createJsonGenerator(fw);
		        jg.useDefaultPrettyPrinter();
		        m.writeValue(jg, datasets.get(0));

			 // Generate "client" dataset from json
			 //datasets = ... 
			 
		}
		else{
			// all other cases...
		}
		
		return datasets;
	}
	
	public Network buildNetwork(List<Parameter> parameters)
	{
		return null;		
	}
	
	public Network buildFirstNeighborNetwork(List<Parameter> parameters)
	{
		return null;		
	}
	
	public Network buildInternalNetwork(List<Parameter> parameters)
	{
		return null;
	}
}
