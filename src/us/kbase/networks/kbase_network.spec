module KBaseNetwork : KBaseNetwork
{

  typedef string NetworkType;
  typedef string DatasetSource;
  typedef string Taxon;
  typedef string Type;
  typedef string Value;
  
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
    KBaseEntity entity;
    string name;
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;
  } Node;
  
  typedef structure {
    string id;  
    string nodeId1;
    string nodeId2;
    string name;
    float  confidence;
    float  strength;
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;  
  } Edge;
  
  typedef structure {
    
    string id;
    string name;
    list<Edge> edges;
    list<Node> nodes;
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;  
  } Network;
  
  typedef list<Dataset> DatasetList;
  
  funcdef getDatasets(ParameterList) returns(DatasetList datasetList);
  funcdef buildNetwork(ParameterList) returns(Network network);  
  funcdef buildFirstNeighborNetwork(ParameterList) returns(Network network);
  funcdef buildInternalNetwork(ParameterList) returns(Network network);
};
