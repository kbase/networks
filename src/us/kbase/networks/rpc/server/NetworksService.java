package us.kbase.networks.rpc.server;

import java.util.List;

import us.kbase.networks.NetworksAPI;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.rpc.client.Dataset;
import us.kbase.networks.rpc.client.Network;
import us.kbase.networks.rpc.client.Parameter;

public class NetworksService {

	private NetworksAPI api; 
	
	public NetworksService() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();
	}
	
	public List<Dataset> getDatasets(List<Parameter> parameters) throws AdaptorException
	{
		List<Dataset> datasets = null;
		if(parameters == null || parameters.size() == 0)
		{
			 List<us.kbase.networks.core.Dataset> serverDatasets =  api.getDatasets();
			 // Serialize server datasets to json
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
