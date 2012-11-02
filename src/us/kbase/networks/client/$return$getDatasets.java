package us.kbase.networks.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$getDatasets_serializer.class)
@JsonDeserialize(using = $return$getDatasets_deserializer.class)
public class $return$getDatasets
{
    public List<Dataset> datasetList;
}


