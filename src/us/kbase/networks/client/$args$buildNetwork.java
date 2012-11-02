package us.kbase.networks.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$buildNetwork_serializer.class)
@JsonDeserialize(using = $args$buildNetwork_deserializer.class)
public class $args$buildNetwork
{
    public List<Parameter> ParameterList;
}


