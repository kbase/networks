package us.kbase.networks.adaptor.ppi;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

/**
  Class to create Adaptor for PPI data in KBase Networks API

  @version 1.0, 10/16/12
  @author JMC
*/
public class PPIAdaptorFactory implements AdaptorFactory {
    @Override public Adaptor buildAdaptor() {
	return new PPIAdaptor();
    }
}
