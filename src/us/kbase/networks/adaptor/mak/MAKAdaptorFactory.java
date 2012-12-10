package us.kbase.networks.adaptor.mak;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;

public class MAKAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		return new MAKAdaptor();
	}

}
