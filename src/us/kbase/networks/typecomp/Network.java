package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



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


