package us.kbase.networks.adaptor;

import java.util.List;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public interface Adaptor {

	
	public List<Dataset> getDatasets() throws AdaptorException;
	
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException;
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException;
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException;
	public List<Dataset> getDatasets(NetworkType networkType, DatasetSource datasetSource, Taxon taxon) throws AdaptorException;
	
	public boolean hasDataset(Dataset dataset) throws AdaptorException;
	
	public Network buildNetwork(Dataset dataset) throws AdaptorException;
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) throws AdaptorException;	
	
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId) throws AdaptorException;
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId, List<EdgeType> edgeTypes) throws AdaptorException;
	
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds) throws AdaptorException;
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds, List<EdgeType> edgeTypes) throws AdaptorException;	
}
