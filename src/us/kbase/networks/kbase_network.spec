module KBaseNetwork : KBaseNetwork
{

	
  typedef string DatasetSourceRef;
  typedef string NetworkType;
  typedef string NodeType;
  typedef string EdgeType;
  typedef string Taxon;
  typedef string Boolean; 
  
/*  
  typedef string Type;
  typedef string Value;
  typedef string JungEdgeType;
  typedef structure {
    Type type;
    Value value;
  } Parameter;

  typedef list<Parameter> ParameterList;
*/  
  
  
  typedef structure {
    string id;
    string name;
	DatasetSourceRef reference;    
    string description;
	string resourceURL;
  } DatasetSource;  
  
  typedef structure {
    string id;
    string name;
    string description;
    NetworkType networkType;
    DatasetSourceRef sourceReference;
    list<Taxon> taxons;
    mapping<string,string> properties;
  } Dataset;
  
  typedef structure {
    string id;  
    string name;
    string entityId;
    NodeType type;
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;
  } Node;
  
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
  typedef list<Dataset> DatasetList;
 */
  
  
 /* All datasets, datasources, and network types available in KBase */  
 
  funcdef allDatasets() returns(list<Dataset> datasets);
  funcdef allDatasetSources() returns(list<DatasetSource> datasetSources);
  funcdef allNetworkTypes() returns(list<NetworkType> networkTypes);
  
 /*  Datasets of a given type available in KBase */
   
  funcdef datasetSource2Datasets(DatasetSourceRef datasetSourceRef) returns(list<Dataset> datasets);
  funcdef taxon2Datasets(Taxon taxon) returns(list<Dataset> datasets);
  funcdef networkType2Datasets(NetworkType networkType) returns(list<Dataset> datasets);
  funcdef entity2Datasets(string entityId) returns(list<Dataset> datasets);
  
 /*  Buid network methods */
 
  funcdef buildFirstNeighborNetwork(list<string> datasetIds, string geneId, list<EdgeType> edgeTypes) returns(Network network);
  funcdef buildInternalNetwork(list<string> datasetIds, list<string> geneIds, list<EdgeType> edgeTypes) returns(Network network);

/* not ready yet
  
  funcdef buildNetwork(string datasetId) returns(Network network);
*/ 


/*  
  funcdef getDatasets(ParameterList) returns(DatasetList datasetList);
  funcdef buildNetwork(ParameterList) returns(Network network);  
  funcdef buildFirstNeighborNetwork(ParameterList) returns(Network network);
  funcdef buildInternalNetwork(ParameterList) returns(Network network);
*/  
};
