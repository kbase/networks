package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



/**
typedef string Type;
typedef string Value;
typedef string JungEdgeType;
typedef structure {
  Type type;
  Value value;
} Parameter;

typedef list<Parameter> ParameterList;
**/

public class DatasetSource
{
    public String id;
    public String name;
    public String reference;
    public String description;
    public String resourceURL;
}


