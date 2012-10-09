package us.kbase.networks.core;


public enum DatasetSource {
	
	
	REGPECISE ("kb|netsource.1", "RegPrecise", "description", "http://regprecise.lbl.gov/"), 
	REGPREDICT("kb|netsource.2", "RegPredict", "description", "http://regpredict.lbl.gov/"),
	REGTRANSBASE("kb|netsource.3", "RegTransBase", "description", "http://regtransbase.lbl.gov"),
	CMONKEY("kb|netsource.4", "cMonkey", "description", "..."),
	MARCINS_BI_CLUSTERING ("kb|netsource.5", "...", "description", "..."),
	INTACT("kb|netsource.6", "IntAct", "Plants ppi", "http://www.ebi.ac.uk/intact/"),	
	AGRIS("kb|netsource.7", "AGRIS", "Arabidopsis gene regulatory information", "http://Arabidopsis.med.ohio-state.edu/"), 
	ARANET("kb|netsource.8", "AraNet", "Transcriptomic co-expression networks for Arabidopsis thaliana", "http://aranet.mpimp-golm.mpg.de/aranet"),
	MODEL_SEED("kb|netsource.9", "Model Seed", "Metabolic models", "http://blog.theseed.org/model_seed/");
	
	
	private String id;
	private String name;
	private String description;
	private String resourceURL;
	
	private DatasetSource(String id, String name, String description, String resourceURL)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.resourceURL = resourceURL;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}


	public String getResourceURL() {
		return resourceURL;
	}

}
