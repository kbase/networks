package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $args$taxon2Datasets_serializer.class)
@JsonDeserialize(using = $args$taxon2Datasets_deserializer.class)
public class $args$taxon2Datasets
{
    public String taxon;
}


