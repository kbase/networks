package us.kbase.networks.adaptor.test;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

public class TestAdaptorFactory implements AdaptorFactory{

	@Override
	public Adaptor buildAdaptor() {
		return new TestAdaptor();
	}

}
