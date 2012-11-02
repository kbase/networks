package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;



@JsonSerialize(using = $return$buildNetwork_serializer.class)
@JsonDeserialize(using = $return$buildNetwork_deserializer.class)
public class $return$buildNetwork
{
    public Network network;
}

