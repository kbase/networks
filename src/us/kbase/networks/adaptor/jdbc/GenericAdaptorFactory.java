package us.kbase.networks.adaptor.jdbc;

import org.apache.commons.configuration.PropertiesConfiguration;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

public  class GenericAdaptorFactory implements AdaptorFactory {

	public String configFN = "";
	public GenericAdaptorFactory(String configFN)
	{
		this.configFN = configFN;
	}
	
	@Override
	public Adaptor buildAdaptor() {
		PropertiesConfiguration conf = null;
    	try {
    		PropertiesConfiguration.setDefaultListDelimiter((char)0);
    		conf = new PropertiesConfiguration(GenericAdaptorFactory.class.getResource(configFN));
    		return new GenericAdaptor(conf);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	} 
    	return null;
	}
	
}
