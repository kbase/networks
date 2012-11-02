package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildInternalNetwork_serializer extends JsonSerializer<$args$buildInternalNetwork>
{
    public void serialize($args$buildInternalNetwork value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.ParameterList);
	jgen.writeEndArray();
    }
}
