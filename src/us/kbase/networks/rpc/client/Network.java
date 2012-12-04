package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
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
**/

public class Network
{
    public String id;
    public String name;
    public List<Edge> edges;
    public List<Node> nodes;
    public List<Dataset> datasets;
    public Map<String, String> properties;
    public Map<String, String> userAnnotations;
}


