package us.kbase.networks.adaptor.regprecise;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

public class RegPreciseAdaptorFactory implements AdaptorFactory{

	@Override
	public Adaptor buildAdaptor() {
		return new RegPreciseAdaptor();
	}

}
