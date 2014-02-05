package us.kbase.kbasenetworks.adaptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Taxon;

public abstract  class AbstractAdaptor implements Adaptor{

	protected Hashtable<String,Dataset> id2datasetHash = new Hashtable<String, Dataset>();
	protected Configuration config;

	protected AbstractAdaptor(Configuration config) throws AdaptorException
	{
		this.config = config;
		System.err.print("Initializing adaptor: " + getClass().getName() + "...");
		init();
		System.err.println("Done!");
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
	public List<Dataset> getDatasetsNetworkType(String networkType) {
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(ds.getNetworkType().equals(networkType))
			{
				datasets.add(ds);
			}
		}
		return datasets;
	}

	@Override
	public List<Dataset> getDatasetsDatasetSource(String datasetSource) {
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(ds.getDatasetSource().equals(datasetSource))
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
	public List<Dataset> getDatasets(String networkType,
			String datasetSource, Taxon taxon) {
		
		List<Dataset> datasets = new Vector<Dataset>();
		for(Dataset ds: id2datasetHash.values())
		{
			if(! ds.getDatasetSource().equals(datasetSource)) continue;
			if(! ds.getNetworkType().equals( networkType)) continue;
			
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
	public List<String> getNetworkTypes()
	{
		HashSet<String> networkTypes = new HashSet<String>();
		for(Dataset ds: getDatasets())
		{
			networkTypes.add(ds.getNetworkType());
		}
		return new ArrayList<String>(networkTypes);
	}
	
	@Override
	public List<String> getDatasetSources() {
		HashSet<String> datasetSources = new HashSet<String>();
		for(Dataset ds: getDatasets())
		{
			datasetSources.add(ds.getDatasetSource());
		}
		return new ArrayList<String>(datasetSources);
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
