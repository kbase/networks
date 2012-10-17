package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.mak.MAKAdaptorFactory;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;
import us.kbase.networks.adaptor.ppi.PPIAdaptorFactory;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptorFactory;

public class AdaptorRepository {
	
	
	private static AdaptorRepository repository;
	private List<Adaptor> adaptors = new Vector<Adaptor>();
	
	private AdaptorRepository()
	{
		// Register all adaptors; property file in the future
		
		registerAdaptor(new RegPreciseAdaptorFactory());
		registerAdaptor(new ModelSEEDAdaptorFactory());
		registerAdaptor(new MAKAdaptorFactory());
		registerAdaptor(new PPIAdaptorFactory());		
	}
	
	public static AdaptorRepository getAdaptorRepository()
	{
		if(repository == null)
		{
			repository = new AdaptorRepository();
		}
		return repository;
	}
	
	private void registerAdaptor(AdaptorFactory factory)
	{
		adaptors.add(factory.buildAdaptor());
	}
	
	public List<Adaptor> getDataAdaptors()
	{
		return adaptors;
	}
}
