package us.kbase.networks.client;

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
    public KBaseEntity entity;
    public String type;
    public Map<String, String> properties;
    public Map<String, String> userAnnotations;
}


