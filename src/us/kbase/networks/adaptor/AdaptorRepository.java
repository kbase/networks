package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.mak.MAKAdaptorFactory;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;
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
		
		registerAdaptor(new RegPreciseAdaptorFactory());
		
		registerAdaptor(new ModelSEEDAdaptorFactory());
/*		
		registerAdaptor(new PPIAdaptorFactory());	
		registerAdaptor(new MAKAdaptorFactory());
		registerAdaptor(new PlantPPIAdaptorFactory());		
		registerAdaptor(new PlantFAAdaptorFactory());		
		registerAdaptor(new PlantRNAdaptorFactory());
*/				
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
