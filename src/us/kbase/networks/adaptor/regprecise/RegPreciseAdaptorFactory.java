package us.kbase.networks.adaptor.regprecise;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;

public class RegPreciseAdaptorFactory implements AdaptorFactory{

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		return new RegPreciseAdaptor();
	}

}
