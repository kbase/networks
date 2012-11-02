package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$getDatasets_serializer.class)
@JsonDeserialize(using = $args$getDatasets_deserializer.class)
public class $args$getDatasets
{
    public List<Parameter> ParameterList;
}

