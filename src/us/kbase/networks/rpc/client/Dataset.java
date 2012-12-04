package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
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
            A list of NCBI taxonomy ids of all organisms for which genomic features (genes, proteins, etc) are used in a given dataset 
            
            mapping<string,string> properties            
            Other properties
**/

public class Dataset
{
    public String id;
    public String name;
    public String description;
    public String networkType;
    public String sourceReference;
    public List<String> taxons;
    public Map<String, String> properties;
}


