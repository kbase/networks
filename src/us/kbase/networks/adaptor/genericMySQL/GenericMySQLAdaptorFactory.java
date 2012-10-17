package us.kbase.networks.adaptor.genericMySQL;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorFactory;

public  class GenericMySQLAdaptorFactory implements AdaptorFactory {

	@Override
	public Adaptor buildAdaptor() {
		String configFN = "adaptor.properties";
		Configuration conf = null;
    	try {
    		conf = new PropertiesConfiguration(configFN);
    		return new GenericMySQLAdaptor(conf);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	} 
    	return null;
	}

}
