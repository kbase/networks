package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$buildFirstNeighborNetwork_serializer.class)
@JsonDeserialize(using = $args$buildFirstNeighborNetwork_deserializer.class)
public class $args$buildFirstNeighborNetwork
{
    public List<Parameter> ParameterList;
}


