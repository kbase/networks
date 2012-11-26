/* 
	Module KBaseNetworks version 1.0
	This module provides access to various types of network-related datasets accross all domains of KBase in the unified format.

	All methods in this module can be classified into two types: 
	i. getting general information about datasets currently available via KBaseNetowrks API
	ii. building various types of Network object
	
	Some definition of a KBase network would be desirable here.... 
*/

module KBaseNetworks : KBaseNetworks
{

	/*
		A reference to a DatasetSource. The value can be one of the following:
		
			REGPRECISE - Curated database of transcriptional regulons reconstructed by the comparative genomic approach in a wide variety of prokaryotic genomes (http://regprecise.lbl.gov) 
			REGTRANSBASE - Curated database of regulatory interactions in prokaryotes captured the knowledge in published scientific literature (http://regtransbase.lbl.gov)
			CMONKEY - Integrated biclustering of heterogeneous genome-wide datasets for the inference of global regulatory networks (http://baliga.systemsbiology.net/drupal/content/cmonkey)
			MAK_BI_CLUSTER - Massive associative k-biclustering
			INTACT - Plants PPI (http://www.ebi.ac.uk/intact)
			AGRIS - Arabidopsis gene regulatory information (http://Arabidopsis.med.ohio-state.edu) 
			PLANET - Combined Sequence and Expression Comparisons across Plant Networks Derived from Seven Species (http://aranet.mpimp-golm.mpg.de)
			MODELSEED - Metabolic models (http://www.theseed.org/models)
			MO - A resource for browsing and comparing microbial genomes (http://www.microbesonline.org)
			ECOCYC - EcoCyc is a scientific database for the bacterium Escherichia coli K-12 MG1655 (http://www.ecocyc.org)
			PPI - Other PPI datasets
	*/		
	typedef string DatasetSourceRef;
	
	/*
		Type of network. The value can be one of the following:
		
			REGULATORY_NETWORK - an edge in a network represents a regulatory interaction
			PROT_PROT_INTERACTION - an edge in a network represents a protein-protein interaction
			METABOLIC_SUBSYSTEM - an edge in a network indicates that two genes are associated on the metabolic map
			FUNCTIONAL_ASSOCIATION - an edge in a network indicates that two genes are functionally associated. Functional association is calculated by integrating various data types.	
	*/
	typedef string NetworkType;
	
	/*
		Type of node in a network. The value can be one of the following:
		
			GENE - a node represents gene 
			PROTEIN - a node represents protein
			CLUSTER - anode represents a cluster of genes or proteins
	*/	
	typedef string NodeType;
	
	/*
		Type of edge in a network. The value can be one of the following:
		
		GENE_GENE - an edge represents interaction between two genes
		GENE_CLUSTER - an edge represents interaction between gene and gene cluster
		PROTEIN_PROTEIN - an edge represents interaction between two proteins
		PROTEIN_CLUSTER - an edge represents interaction between protein and protein cluster
		CLUSTER_CLUSTER - an edge represents interaction between two clusters
	*/
	typedef string EdgeType;
	
	/*
		NCBI taxonomy id.
	*/	
	typedef string Taxon;
	
	
	/*
		Helper type to represent boolean values
	*/	
	typedef string Boolean; 
 
  
	/*
		Provides a detailed information about the source of a dataset.
		
		string id		
		A unique KBase identifier of a dataset source
		
    	string name    	
    	A convenient name of a dataset source
    	
    	DatasetSourceRef reference        	
    	Reference to a dataset source type
    	
    	string description    	
    	General description of a dataset source
    	
    	string resourceURL		
		URL of the public web resource hosting the data represented by this dataset source
		
	*/	
  	typedef structure {
    	string id;
    	string name;
    	DatasetSourceRef reference;    
    	string description;
    	string resourceURL;
  	} DatasetSource;  
  
  
  	/*
  		Represents a particular dataset.
  		
    	string id    	
    	A unique KBase identifier of a dataset 
    	
    	string name    	
    	The name of a dataset
    	
    	string description    	
    	Description of a dataset
    	
    	NetworkType networkType
    	Type of network that can be generated from a given dataset

    	DatasetSourceRef sourceReference
    	Reference to a dataset source
    	    	
    	list<Taxon> taxons
    	A list of NCBI taxonomy ids of all organisms which genomic features (genes, proteins, etc) are used in a given dataset 
    	
    	mapping<string,string> properties    	
    	Other properties  		  		
  	*/
 	typedef structure {
    	string id;
    	string name;
    	string description;
    	NetworkType networkType;
    	DatasetSourceRef sourceReference;
    	list<Taxon> taxons;
    	mapping<string,string> properties;
  	} Dataset;
  


	/*
		Represents a node in a network.
		
   		string id
    	A unique KBase identifier of a node 
   		
    	string name
    	String representation of o node. It should be a concise but informative representation that is easy for a person to read.
    	
    	string entityId
    	The identifier of a KBase entity (gene, protein, molecule. genome, etc) represented by a given node 
    	    	
    	NodeType type
    	The type of a node
    	
    	mapping<string,string> properties
    	Other properties of a node
    	
    	mapping<string,string> userAnnotations
    	User annotations of a node		
	*/  
  	typedef structure {
   		string id;  
    	string name;
    	string entityId;
    	NodeType type;
    	mapping<string,string> properties;
    	mapping<string,string> userAnnotations;
  	} Node;
  
  	/*
  		Represents an edge in a network.
  		
    	string id
    	A unique KBase identifier of an edge 
    	
    	string name
		String representation of an edge. It should be a concise but informative representation that is easy for a person to read.
    	
    	string nodeId1
    	Identifier of the first node (source node, if the edge is directed) connected by a given edge 
    	
    	string nodeId2
    	Identifier of the second node (target node, if the edge is directed) connected by a given edge
    	
    	Boolean	directed
    	Specify whether the edge is directed or not. "true" if it is directed, "false" if it is not directed
    	
    	float  confidence
    	Value from 0 to 1 representing a probability that the interaction represented by a given edge is a true interaction
    	
    	float  strength
    	Value from 0 to 1 representing a strength of an interaction represented by a given edge
    	
    	string datasetId
    	The identifier of a dataset that provided an interaction represented by a given edge
    	    	
    	mapping<string,string> properties
    	Other edge properties
    	
    	mapping<string,string> userAnnotations
    	User annotations of an edge    	    		
  	*/
  	typedef structure {
    	string id;  
    	string name;
    	string nodeId1;
    	string nodeId2;
    	Boolean	directed;
    	float  confidence;
    	float  strength;
    	string datasetId;
    	mapping<string,string> properties;
    	mapping<string,string> userAnnotations;  
  	} Edge;
  

	/*
		Represents a network.
		
    	string id
    	A unique KBase identifier of a network 
    	
    	string name
		String representation of a network. It should be a concise but informative representation that is easy for a person to read.
    	
    	
    	list<Edge> edges
    	A list of all edges in a network
    	
    	list<Node> nodes
    	A list of all nodes in a network
    	
    	list<Dataset> datasets
    	A list of all datasets used to build a network
    	    	
    	mapping<string,string> properties
    	Other properties of a network
    	
    	mapping<string,string> userAnnotations
    	User annotations of a network  
	*/  
  	typedef structure {    
    	string id;
    	string name;
    	list<Edge> edges;
    	list<Node> nodes;
    	list<Dataset> datasets;
    	mapping<string,string> properties;
    	mapping<string,string> userAnnotations;  
  	} Network;

  
  
 	/*
 		Returns a list of all network-related datasets that can be used to create a network. 
 	*/
	funcdef allDatasets() returns(list<Dataset> datasets);
  
  
   	/*
 		Returns a list of all dataset sources available in KBase via KBaseNetworks API. 
 	*/  
	funcdef allDatasetSources() returns(list<DatasetSource> datasetSources);
	
 	/*
 		Returns a list of all types of networks that can be created.  
 	*/	
	funcdef allNetworkTypes() returns(list<NetworkType> networkTypes);


   	/*
   		Returns a list of all datasets from a given dataset source.
   		
   		DatasetSourceRef datasetSourceRef
   		A reference to a dataset source   		   		
   	*/
  	funcdef datasetSource2Datasets(DatasetSourceRef datasetSourceRef) returns(list<Dataset> datasets);
  	
  	/*
   		Returns a list of all datasets that can be used to build a network for a particular genome represented by NCBI taxonomy id. 
  		
  		Taxon taxon
  		NCBI taxonomy id
  	*/
  	funcdef taxon2Datasets(Taxon taxon) returns(list<Dataset> datasets);
  	
  	/*
  	   	Returns a list of all datasets that can be used to build a netowrk of a given type.
  	   	
  	   	NetworkType networkType
  	   	The type of network
  	
  	*/
  	funcdef networkType2Datasets(NetworkType networkType) returns(list<Dataset> datasets);
  	
	/*
  	   	Returns a list of all datasets that have at least one interection for a given KBase entity (gene, protein, molecule, genome, etc) 
		
	*/  	
  	funcdef entity2Datasets(string entityId) returns(list<Dataset> datasets);

  
	/*
  	   	Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
  	   	a "source" node and all other nodes that have at least one interaction with the "source" node. Only interactions of given types are 
  	   	considered.    
  	   	
  	   	list<string> datasetIds
  	   	List of dataset identifiers to be used for building a network
  	   	
  		string geneId
  		Identifier of a gene to be used as a source node   	
  	   			
  	   	list<EdgeType> edgeTypes
  	   	List of possible edge types to be considered for building a network
  	   			
	*/    
  	funcdef buildFirstNeighborNetwork(list<string> datasetIds, string geneId, list<EdgeType> edgeTypes) returns(Network network);
  	
	/*
  	   	Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
  	   	a "source" node and all other nodes that have at least one interaction with the "source" node. Only interactions of given types are 
  	   	considered. Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.   
  	   	
  	   	list<string> datasetIds
  	   	List of dataset identifiers to be used for building a network
  	   	
  		string geneId
  		Identifier of a gene to be used as a source node   	
  	   			
  	   	list<EdgeType> edgeTypes
  	   	List of possible edge types to be considered for building a network
  	   	
  	   	float cutOff
  	   	The threshold on the strength of edges to be considered for building a network
  	   			
	*/  	  	
  	funcdef buildFirstNeighborNetworkLimtedByStrength(list<string> datasetIds, string geneId, list<EdgeType> edgeTypes, float cutOff) returns(Network network);
  	
  	
	/*
  	   	Returns an "internal" network constructed basing on a given list of datasets. Internal network contains the only nodes defined by the geneIds parameter, 
  	   	and edges representing interactions between these nodes.  Only interactions of given types are considered.    
  	   	
  	   	list<string> datasetIds
  	   	List of dataset identifiers to be used for building a network
  	   	
  		list<string> geneIds
  		Identifiers of genes of interest for building a network 	
  	   			
  	   	list<EdgeType> edgeTypes
  	   	List of possible edge types to be considered for building a network
  	   			
	*/    	
  	funcdef buildInternalNetwork(list<string> datasetIds, list<string> geneIds, list<EdgeType> edgeTypes) returns(Network network);
  	
  	
	/*
  	   	Returns an "internal" network constructed basing on a given list of datasets. Internal network contains the only nodes defined by the geneIds parameter, 
  	   	and edges representing interactions between these nodes.  Only interactions of given types are considered. 
  	   	Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.     
  	   	
  	   	list<string> datasetIds
  	   	List of dataset identifiers to be used for building a network
  	   	
  		list<string> geneIds
  		Identifiers of genes of interest for building a network 	
  	   			
  	   	list<EdgeType> edgeTypes
  	   	List of possible edge types to be considered for building a network
  	   	
  	   	float cutOff
  	   	The threshold on the strength of edges to be considered for building a network
  	   			
	*/     	
  	funcdef buildInternalNetworkLimitedByStrength(list<string> datasetIds, list<string> geneIds, list<EdgeType> edgeTypes, float cutOff) returns(Network network);

};
