package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.JDBCAdaptor.GenericAdaptorFactory;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantPPIAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantRNAdaptorFactory;
import us.kbase.networks.adaptor.ppi.PPIAdaptorFactory;

public class AdaptorRepository {
	
	
	private static AdaptorRepository repository;
	private List<Adaptor> adaptors = new Vector<Adaptor>();
	
	private AdaptorRepository() throws AdaptorException
	{
		// Register all adaptors; property file in the future
		
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("regprecise.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("mak.config"));

		registerAdaptor(new ModelSEEDAdaptorFactory());

		// registerAdaptor(new PPIAdaptorFactory());	
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("ppi.config"));
		
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-cc.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-cn.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-fn.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-gp.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-ppip.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-rn.config"));
		registerAdaptor(new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("plant-ppi-ga.config"));
	}
	
	public static AdaptorRepository getAdaptorRepository() throws AdaptorException
	{
		if(repository == null)
		{
			repository = new AdaptorRepository();
		}
		return repository;
	}
	
	private void registerAdaptor(AdaptorFactory factory) throws AdaptorException
	{
		adaptors.add(factory.buildAdaptor());
	}
	
	public List<Adaptor> getDataAdaptors()
	{
		return adaptors;
	}
}
