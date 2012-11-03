package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$allNetworkTypes_serializer.class)
@JsonDeserialize(using = $return$allNetworkTypes_deserializer.class)
public class $return$allNetworkTypes
{
    public List<String> networkTypes;
}


