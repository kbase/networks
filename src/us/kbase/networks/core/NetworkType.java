package us.kbase.networks.core;

public enum NetworkType {
	
	REGULATORY_NETOWRK ("kb|nettype.1", "Regulatory network", "some description"),
	PROT_PROT_INTERACTION("kb|nettype.2", "Protein-protein interaction", "Physical interactions between proteins"),
	METABOLIC_PATHWAY ("kb|nettype.3", "Metabolic pathways", "some description"),
	FUNCTIONAL_ASSOCIATION ("kb|nettype.4", "Functional association", "Data of various types are integrated"); 
	
	private String id;
	private String name;
	private String description;
		
	private NetworkType(String id, String name, String description){
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesccription()
	{
		return description;
	}	
}

