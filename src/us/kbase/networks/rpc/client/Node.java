package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



public class Node
{
    public String id;
    public String name;
    public String entityId;
    public String type;
    public Map<String, String> properties;
    public Map<String, String> userAnnotations;
}


