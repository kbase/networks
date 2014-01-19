package us.kbase.kbasenetworks.adaptor.modelseed;

import java.lang.reflect.Proxy;

import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.AdaptorFactory;

public class ModelSEEDAdaptorProxyFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() throws AdaptorException {
		Adaptor proxyAdaptor = null; 
			
	    try {
	    	ModelSEEDAdaptor adaptor = new ModelSEEDAdaptor();
	    	AdaptorInvocationHandler invHandler = new AdaptorInvocationHandler(adaptor);
	    	proxyAdaptor = (Adaptor) Proxy.newProxyInstance(
	    					ModelSEEDAdaptor.class.getClassLoader(), 
	    					new Class[]{Adaptor.class}, 
	    					invHandler); 
	    } catch (Exception ex) {
	    	ex.printStackTrace(System.err);
	    	throw new AdaptorException(ex.getMessage(), ex);
	    } 	
	    return proxyAdaptor;
	}
}
