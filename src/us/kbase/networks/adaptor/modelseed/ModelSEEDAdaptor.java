package us.kbase.networks.adaptor.modelseed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public class ModelSEEDAdaptor implements Adaptor {

	@Override
	public List<Dataset> getDatasets() throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		// exec Perl script, turn results into Datasets
		Object stuff = buildPerlProcess("get_all_models");
		System.out.println(stuff);
		return datasets;
	}

	private Object buildPerlProcess(String script) throws AdaptorException {
		// assume perl is installed and available
		ProcessBuilder pb = new ProcessBuilder("perl", script);
		Map<String, String> env = pb.environment();
		if (! env.containsKey("PERL5LIB")) {
			env.put("PERL5LIB", "/Applications/KBase.app/deployment/lib");
		}
		
		String stuff = null;
		pb.directory(new File("scripts"));
		try {
			Process p = pb.start();
			int code = p.waitFor();
			if (code != 0) {
				throw new AdaptorException("Error code returned when attempting to run Perl script");
			}
			// CHANGE THIS TO READ AND PARSE JSON
			BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = outReader.readLine()) != null) {
				stuff += line;
			}
		}
		catch (InterruptedException ie) {
			throw new AdaptorException("Exception while running Perl script: " + script, ie);
		}
		catch (IOException ioe) {
			throw new AdaptorException("Exception while running Perl script: " + script, ioe);
		}
		return stuff;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(networkType == NetworkType.METABOLIC_PATHWAY)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new Vector<Dataset>();
		if(datasetSource == DatasetSource.MODELSEED)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(Taxon taxon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) {
		List<Dataset> datasets = new Vector<Dataset>();

		if(networkType == NetworkType.METABOLIC_PATHWAY)		
			if(datasetSource == DatasetSource.MODELSEED)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}

	@Override
	public boolean hasDataset(Dataset dataset) {
		// TODO Auto-generated method stub
		return false;
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

}
