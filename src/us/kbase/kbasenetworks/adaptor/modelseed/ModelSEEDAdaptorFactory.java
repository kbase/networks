package us.kbase.kbasenetworks.adaptor.modelseed;

import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.AdaptorFactory;

public class ModelSEEDAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		
		return new ModelSEEDAdaptor();	
	}	
}
