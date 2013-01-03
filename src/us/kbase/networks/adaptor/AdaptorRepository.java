package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.JDBCAdaptor.GenericAdaptorFactory;
import us.kbase.networks.adaptor.mak.MAKAdaptorFactory;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantCCAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantCNAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantFAAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantPPIAdaptorFactory;
import us.kbase.networks.adaptor.plant.PlantRNAdaptorFactory;
import us.kbase.networks.adaptor.ppi.PPIAdaptorFactory;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptorFactory;

public class AdaptorRepository {
	
	
	private static AdaptorRepository repository;
	private List<Adaptor> adaptors = new Vector<Adaptor>();
	
	private AdaptorRepository() throws AdaptorException
	{
		// Register all adaptors; property file in the future
		
		//registerAdaptor(new RegPreciseAdaptorFactory()); // temporarily commented out for production deployment test
		registerAdaptor(new ModelSEEDAdaptorFactory());
		registerAdaptor(new PPIAdaptorFactory());	
		registerAdaptor(new MAKAdaptorFactory());
		registerAdaptor(new PlantPPIAdaptorFactory());		
		registerAdaptor(new PlantRNAdaptorFactory());
		registerAdaptor(new GenericAdaptorFactory("plant-cc-at-ga.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-cc-pt-ga.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-cn-at-ga.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-cn-pt-ga.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-fa-at-ga.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-fa-pt-ga.config"));
				
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
