package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$buildInternalNetworkLimitedByStrength_serializer.class)
@JsonDeserialize(using = $args$buildInternalNetworkLimitedByStrength_deserializer.class)
public class $args$buildInternalNetworkLimitedByStrength
{
    public List<String> datasetIds;
    public List<String> geneIds;
    public List<String> edgeTypes;
    public Float cutOff;
}


