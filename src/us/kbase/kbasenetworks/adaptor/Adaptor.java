package us.kbase.kbasenetworks.adaptor;

import java.util.List;

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.Taxon;

public interface Adaptor {

	public List<String> getNetworkTypes();
	public List<String> getDatasetSources();
	public List<Taxon> getTaxons();

	
	public List<Dataset> getDatasets();
	
	public List<Dataset> getDatasetsNetworkType(String networkType) throws AdaptorException;
	public List<Dataset> getDatasetsDatasetSource(String datasetSource) throws AdaptorException;
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException;
	public List<Dataset> getDatasets(String networkType, String datasetSource, Taxon taxon) throws AdaptorException;
	
	public boolean hasDataset(String datasetId) throws AdaptorException;	
	public Dataset getDataset(String datasetId) throws AdaptorException;
	
	public Network buildNetwork(Dataset dataset) throws AdaptorException;
	public Network buildNetwork(Dataset dataset, List<String> edgeTypes) throws AdaptorException;	
	
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity) throws AdaptorException;
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity, List<String> edgeTypes) throws AdaptorException;
	
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities) throws AdaptorException;
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities, List<String> edgeTypes) throws AdaptorException;

	public List<Dataset> getDatasets(Entity entity) throws AdaptorException;
}
