package us.kbase.networks.adaptor;

import java.util.List;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public interface Adaptor {

	public List<NetworkType> getNetworkTypes();
	public List<DatasetSource> getDatasetSources();
	public List<Taxon> getTaxons();

	
	public List<Dataset> getDatasets();
	
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException;
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException;
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException;
	public List<Dataset> getDatasets(NetworkType networkType, DatasetSource datasetSource, Taxon taxon) throws AdaptorException;
	
	public boolean hasDataset(String datasetId) throws AdaptorException;	
	public Dataset getDataset(String datasetId) throws AdaptorException;
	
	public Network buildNetwork(Dataset dataset) throws AdaptorException;
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) throws AdaptorException;	
	
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity) throws AdaptorException;
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity, List<EdgeType> edgeTypes) throws AdaptorException;
	
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities) throws AdaptorException;
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities, List<EdgeType> edgeTypes) throws AdaptorException;

	public List<Dataset> getDatasets(Entity entity) throws AdaptorException;
}
