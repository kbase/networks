package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



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


