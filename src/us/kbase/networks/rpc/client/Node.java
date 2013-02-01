package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
Represents a node in a network.

   string id
            A unique KBase identifier of a node 
   
            string name
            String representation of a node. It should be a concise but informative representation that is easy for a person to read.
            
            string entityId
            The identifier of a KBase entity (gene, protein, molecule. genome, etc) represented by a given node 
        
            NodeType type
            The type of a node
            
            mapping<string,string> properties
            Other properties of a node
            
            mapping<string,string> userAnnotations
            User annotations of a node
**/

public class Node
{
    public String id;
    public String name;
    public String entityId;
    public String type;
    public Map<String, String> properties;
    public Map<String, String> userAnnotations;
    
    @Override
    public String toString()
    {
    	return name;
    }
}


