module KBaseNetwork : KBaseNetwork
{
  typedef string NetworkType;
  typedef string DatasetSource;
  typedef string Taxon;
  
  
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
    mapping<string,string> properties;
    mapping<string,string> userAnnotations;  
  } Edge;
  
  
  
  typedef structure {
    
    string id;
    string name;
    list<Edge> edges;
    list<Node> nodes;
  } Network;
  
  typedef list<Dataset> DatasetList;
  
  funcdef getDatasets() returns(DatasetList datasetList);
    
  funcdef getDatasets(NetworkType netowrkType) returns(DatasetList datasetList);
  funcdef getDatasets(DatasetSource datasetSource) returns(DatasetList datasetList);
  funcdef getDatasets(Taxon taxon) returns(DatasetList datasetList);
  
  funcdef getDatasets(NetworkType netowrkType, DatasetSource datasetSource, Taxon taxon) returns(DatasetList datasetList);

  funcdef buildNetwork(Dataset dataset) returns(Network network);  
};
