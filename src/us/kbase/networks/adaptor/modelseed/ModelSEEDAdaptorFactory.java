package us.kbase.networks.adaptor.modelseed;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;

public class ModelSEEDAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		return new ModelSEEDAdaptor();
	}

}
