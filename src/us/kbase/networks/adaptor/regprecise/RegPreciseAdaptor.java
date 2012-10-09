package us.kbase.networks.adaptor.regprecise;

import java.util.List;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public class RegPreciseAdaptor implements Adaptor{

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId,
			List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
			List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildNetwork(Dataset dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType netowrkType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets(Taxon taxon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType netowrkType,
			DatasetSource datasetSource, Taxon taxon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasDataset(Dataset dataset) {
		// TODO Auto-generated method stub
		return false;
	}

}
