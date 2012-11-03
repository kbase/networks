package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$entity2Datasets_serializer.class)
@JsonDeserialize(using = $return$entity2Datasets_deserializer.class)
public class $return$entity2Datasets
{
    public List<Dataset> datasets;
}


