module KBaseNetwork : KBaseNetwork
{

  typedef string NetworkType;
  typedef string NodeType;
  typedef string DatasetSource;
  typedef string Taxon;
  typedef string Type;
  typedef string Value;
  typedef string JungEdgeType;
  
  typedef structure {
    Type type;
    Value value;
  } Parameter;

  typedef list<Parameter> ParameterList;
  
  typedef structure {
    string id;
    string name;
    string description;
    NetworkType networkType;
    DatasetSource datasetSource;
    list<Taxon> taxons;
    mapping<string,string> properties;
  } Dataset;

  typedef structure {
    string id;
  } KBaseEntity;
  
  typedef structure {
    string id;  
    string name;
    KBaseEntity entity;
    NodeType type;
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;
  } Node;
  
  typedef structure {
    string id;  
    string name;
    string nodeId1;
    string nodeId2;
    JungEdgeType jungEdgeType;
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
  
  typedef list<Dataset> DatasetList;
  
  funcdef getDatasets(ParameterList) returns(DatasetList datasetList);
  funcdef buildNetwork(ParameterList) returns(Network network);  
  funcdef buildFirstNeighborNetwork(ParameterList) returns(Network network);
  funcdef buildInternalNetwork(ParameterList) returns(Network network);
};
