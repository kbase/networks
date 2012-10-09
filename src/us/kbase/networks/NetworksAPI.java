package us.kbase.networks;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorRepository;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;

public class NetworksAPI {
	
	private static NetworksAPI networksAPI;
	private AdaptorRepository adaptorRepository; 	
	
	private NetworksAPI(){		
		adaptorRepository = AdaptorRepository.getAdaptorRepository();
	}
		
	
	public static NetworksAPI getNetowrksAPI()
	{
		if(networksAPI == null)
		{
			networksAPI = new NetworksAPI();			
		}		
		return networksAPI;
	}
	
	public List<NetworkType> getNetworTypes()
	{
		return Arrays.asList(NetworkType.values());
	}
	
	public List<DatasetSource> getDatasetSources()
	{
		return Arrays.asList(DatasetSource.values());
	}
	
	public List<Dataset> getDatasets()
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets());
		}		
		return datasets;
	}
	
	public List<Dataset> getDatasets(NetworkType networkType)
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets(networkType));
		}		
		return datasets;
	}
	
	public List<Dataset> getDatasets(DatasetSource datasetSource)
	{
		List<Dataset> datasets = new Vector<Dataset>();
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{
			datasets.addAll(adaptor.getDatasets(datasetSource));
		}		
		return datasets;
	}		
	
			
	public Network buildNetwork(Dataset dataset)
	{
		for(Adaptor adaptor: adaptorRepository.getDataAdaptors())
		{					
			if(adaptor.hasDataset(dataset) )
			{
				return adaptor.buildNetwork(dataset);
			}
		}	
		return null;
	}
}
