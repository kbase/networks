package us.kbase.networks.adaptor.plant;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.AdaptorFactory;
import us.kbase.networks.adaptor.JDBCAdaptor.GenericAdaptor;

public  class PlantCCAdaptorFactory implements AdaptorFactory {

	public static final String configFN = "plant-cc-ga.config";
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