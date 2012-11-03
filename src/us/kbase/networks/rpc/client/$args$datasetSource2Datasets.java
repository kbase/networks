package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$datasetSource2Datasets_serializer.class)
@JsonDeserialize(using = $args$datasetSource2Datasets_deserializer.class)
public class $args$datasetSource2Datasets
{
    public String datasetSourceRef;
}


