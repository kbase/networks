package us.kbase.networks.adaptor.JDBCAdaptor;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;
import us.kbase.networks.adaptor.JDBCAdaptor.GenericAdaptor;

public  class GenericAdaptorFactory implements AdaptorFactory {

	public String configFN = "";
	GenericAdaptorFactory(String configFN)
	{
		this.configFN = configFN;
	}
	
	@Override
	public Adaptor buildAdaptor() {
		Configuration conf = null;
    	try {
    		conf = new PropertiesConfiguration(configFN);
    		return new GenericAdaptor(conf);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	} 
    	return null;
	}
}
