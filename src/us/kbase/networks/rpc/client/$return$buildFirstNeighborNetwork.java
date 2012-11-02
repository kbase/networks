package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$buildFirstNeighborNetwork_serializer.class)
@JsonDeserialize(using = $return$buildFirstNeighborNetwork_deserializer.class)
public class $return$buildFirstNeighborNetwork
{
    public Network network;
}


