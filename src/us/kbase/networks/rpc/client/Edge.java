package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
Represents an edge in a network.

            string id
            A unique KBase identifier of an edge 
            
            string name
                String representation of an edge. It should be a concise but informative representation that is easy for a person to read.
            
            string nodeId1
            Identifier of the first node (source node, if the edge is directed) connected by a given edge 
            
            string nodeId2
            Identifier of the second node (target node, if the edge is directed) connected by a given edge
            
            Boolean        directed
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
**/

public class Edge
{
    public String id;
    public String name;
    public String nodeId1;
    public String nodeId2;
    public String directed;
    public Float confidence;
    public Float strength;
    public String datasetId;
    public Map<String, String> properties;
    public Map<String, String> userAnnotations;
    
    @Override
    public String toString()
    {
    	return name;
    }
    
}


