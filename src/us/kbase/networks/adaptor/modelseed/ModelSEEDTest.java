package us.kbase.networks.adaptor.modelseed;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public class ModelSEEDTest {
	Adaptor adaptor = new ModelSEEDAdaptorFactory().buildAdaptor();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void shouldReturnListOfDatasets() throws AdaptorException {
		assertNotNull("should return a list of Datasets", adaptor.getDatasets());
	}
	
	@Test
	public void shouldReturnDataSetForEcoli() throws AdaptorException {
		Taxon ecoli = new Taxon("kb|g.0");
		List<Dataset> datasets = adaptor.getDatasets(NetworkType.METABOLIC_PATHWAY, DatasetSource.MODELSEED, ecoli);
		assertNotNull("should return a list of Datasets", datasets);
		assertTrue("list should contain at least one dataset", datasets.size() > 0);
		for (Dataset dataset : datasets) {
			assertTrue("dataset taxons should contain kb|g.0 for E. coli", dataset.getTaxons().contains(ecoli));
		}
		
	}

}
