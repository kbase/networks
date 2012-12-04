package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
Provides detailed information about the source of a dataset.

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
**/

public class DatasetSource
{
    public String id;
    public String name;
    public String reference;
    public String description;
    public String resourceURL;
}


