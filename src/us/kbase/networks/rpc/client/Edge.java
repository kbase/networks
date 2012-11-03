package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



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
}


