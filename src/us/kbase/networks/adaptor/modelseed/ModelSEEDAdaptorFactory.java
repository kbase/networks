package us.kbase.networks.adaptor.modelseed;

import java.lang.reflect.Proxy;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;

public class ModelSEEDAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		
//		return new ModelSEEDAdaptor();	
		return  buildAdaptorProxy();
	}
	
	
	private Adaptor buildAdaptorProxy() throws AdaptorException
	{
		Adaptor proxyAdaptor = null; 
			
	    try {
	    	ModelSEEDAdaptor adaptor = new ModelSEEDAdaptor();
	    	AdaptorInvocationHandler invHandler = new AdaptorInvocationHandler(adaptor);
	    	proxyAdaptor = (Adaptor) Proxy.newProxyInstance(
	    					ModelSEEDAdaptor.class.getClassLoader(), 
	    					new Class[]{Adaptor.class}, 
	    					invHandler); 
	    } catch (Exception ex) {
	    	throw new AdaptorException(ex.getMessage(), ex);
	    } 	
	    return proxyAdaptor;
	}
}
