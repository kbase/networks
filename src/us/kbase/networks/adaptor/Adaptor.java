package us.kbase.networks.adaptor;

import java.util.List;

import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public interface Adaptor {

	
	public List<Dataset> getDatasets();
	
	public List<Dataset> getDatasets(NetworkType networkType);
	public List<Dataset> getDatasets(DatasetSource datasetSource);
	public List<Dataset> getDatasets(Taxon taxon);
	public List<Dataset> getDatasets(NetworkType networkType, DatasetSource datasetSource, Taxon taxon);
	
	public boolean hasDataset(Dataset dataset);
	
	public Network buildNetwork(Dataset dataset);
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes);	
	
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId);
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId, List<EdgeType> edgeTypes);
	
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds);
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds, List<EdgeType> edgeTypes);	
}
