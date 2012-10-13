package us.kbase.networks.adaptor.modelseed;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

public class ModelSEEDAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() {
		return new ModelSEEDAdaptor();
	}

}
