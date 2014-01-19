package us.kbase.kbasenetworks.core;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(using = DatasetSourceSerializer.class)
public enum DatasetSource {

	REGPRECISE ("kb|netsource.1", "RegPrecise", "description", "http://regprecise.lbl.gov/"), 
	REGPREDICT("kb|netsource.2", "RegPredict", "description", "http://regpredict.lbl.gov/"),
	REGTRANSBASE("kb|netsource.3", "RegTransBase", "description", "http://regtransbase.lbl.gov"),
	CMONKEY("kb|netsource.4", "cMonkey", "description", "..."),
	MAK_BICLUSTER ("kb|netsource.5", "MAKv1.0", "Massive associative k-biclustering", "..."),
	INTACT("kb|netsource.6", "IntAct", "Plants ppi", "http://www.ebi.ac.uk/intact/"),	
	AGRIS("kb|netsource.7", "AGRIS", "Arabidopsis gene regulatory information", "http://Arabidopsis.med.ohio-state.edu/"), 
	PLANET("kb|netsource.8", "PlaNet", "Combined Sequence and Expression Comparisons across Plant Networks Derived from Seven Species", "http://aranet.mpimp-golm.mpg.de/"),
	MODELSEED("kb|netsource.9", "ModelSEED", "Metabolic models", "http://www.theseed.org/models/"),
	MO("kb|netsource.10", "Microbes Online", "A resource for browsing and comparing microbial genomes", "http://www.microbesonline.org/"),
	ECOCYC("kb|netsource.11", "EcoCyc", "EcoCyc is a scientific database for the bacterium Escherichia coli K-12 MG1655", "http://www.ecocyc.org/"),
	PPI("kb|netsource.12", "PPI", "Other PPI datasets", ""),
	GRAMENE("kb|netsource.13","Gramene","Pathways, G2P, Ontologies, ...", "http://www.gramene.org/"),
	ARANET("kb|netsource.14","AraNet","Functional Association", "http://www.functionalnet.org/aranet/"),
	POPNET("kb|netsource.15","PopNet","Functional Association projected from AraNet", "http://www.functionalnet.org/"),
	PLANTCYC("kb|netsource.16","PlantCyc","Plant Metabolic Pathway DB", "hhttp://www.plantcyc.org/"),
	GEO("kb|netsource.17","GEO","Gene Expression Omnibus", "http://www.ncbi.nlm.nih.gov/geo/"),
	MG_RAST("kb|netsource.18","MG-RAST","Metagenomics Analysis Server", "http://metagenomics.anl.gov"),
	KEGG("kb|netsource.19","KEGG","Kyoto Encyclopedia of Genes and Genomes", "http://www.genome.jp/kegg/");
	

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
