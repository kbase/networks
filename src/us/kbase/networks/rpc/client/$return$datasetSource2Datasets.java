package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$datasetSource2Datasets_serializer.class)
@JsonDeserialize(using = $return$datasetSource2Datasets_deserializer.class)
public class $return$datasetSource2Datasets
{
    public List<Dataset> datasets;
}


