package us.kbase.networks.adaptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public abstract  class AbstractAdaptor implements Adaptor{

	protected Hashtable<String,Dataset> id2datasetHash = new Hashtable<String, Dataset>();
	protected Configuration config;

	protected AbstractAdaptor(Configuration config) throws AdaptorException
	{
		this.config = config;
		System.out.print("Initializing adaptor: " + getClass().getName() + "...");
		init();
		System.out.println("Done!");
	}
	
	protected abstract List<Dataset> loadDatasets() throws AdaptorException;
	
	
	protected void init() throws AdaptorException{
		id2datasetHash.clear();
		for(Dataset ds: loadDatasets())
		{
			id2datasetHash.put(ds.getId(), ds);
		}		
	}
	
	@Override
	public List<Dataset> getDatasets() {
		return new ArrayList<Dataset>(id2datasetHash.values());
	}
	
	
	@Override
	public boolean hasDataset(String datasetId) throws AdaptorException {
		return id2datasetHash.containsKey(datasetId);
	}

	@Override
	public Dataset getDataset(String datasetId) throws AdaptorException {
		return id2datasetHash.get(datasetId);
	}
	
	@Override
	public List<Dataset> getDatasets(NetworkType networkType) {
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(ds.getNetworkType() == networkType)
			{
				datasets.add(ds);
			}
		}
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) {
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(ds.getDatasetSource() == datasetSource)
			{
				datasets.add(ds);
			}
		}
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(Taxon taxon) {
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			for(Taxon tx: ds.getTaxons())
			{
				if(tx.equals(taxon))
				{
					datasets.add(ds);
					break;
				}
			}
		}
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) {
		
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(ds.getDatasetSource() != datasetSource) continue;
			if(ds.getNetworkType() != networkType) continue;
			
			for(Taxon tx: ds.getTaxons())
			{
				if(tx.equals(taxon))
				{
					datasets.add(ds);
					break;
				}
			}
		}
		return datasets;
	}
	
	@Override
	public List<NetworkType> getNetworkTypes()
	{
		HashSet<NetworkType> networkTypes = new HashSet<NetworkType>();
		for(Dataset ds: getDatasets())
		{
			networkTypes.add(ds.getNetworkType());
		}
		return new ArrayList<NetworkType>(networkTypes);
	}
	
	@Override
	public List<DatasetSource> getDatasetSources() {
		HashSet<DatasetSource> datasetSources = new HashSet<DatasetSource>();
		for(Dataset ds: getDatasets())
		{
			datasetSources.add(ds.getDatasetSource());
		}
		return new ArrayList<DatasetSource>(datasetSources);
	}
	
	@Override
	public List<Taxon> getTaxons() 
	{
		HashSet<Taxon> taxons = new HashSet<Taxon>();
		for(Dataset ds: getDatasets())
		{
			taxons.addAll(ds.getTaxons());
		}
		return new ArrayList<Taxon>(taxons);
	}
	
}
