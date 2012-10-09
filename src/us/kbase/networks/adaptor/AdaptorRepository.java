package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptorFactory;
import us.kbase.networks.adaptor.test.TestAdaptorFactory;

public class AdaptorRepository {
	
	
	private static AdaptorRepository repository;
	private List<Adaptor> adaptors = new Vector<Adaptor>();
	
	private AdaptorRepository()
	{
		// Register all adaptors; property file in the future
		
		registerAdaptor(new TestAdaptorFactory());
		registerAdaptor(new RegPreciseAdaptorFactory());
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
