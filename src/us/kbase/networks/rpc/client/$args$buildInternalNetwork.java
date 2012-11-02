package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$buildInternalNetwork_serializer.class)
@JsonDeserialize(using = $args$buildInternalNetwork_deserializer.class)
public class $args$buildInternalNetwork
{
    public List<Parameter> ParameterList;
}


