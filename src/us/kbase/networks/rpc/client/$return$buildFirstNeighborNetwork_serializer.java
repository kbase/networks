package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildFirstNeighborNetwork_serializer extends JsonSerializer<$return$buildFirstNeighborNetwork>
{
    public void serialize($return$buildFirstNeighborNetwork value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.network);
	jgen.writeEndArray();
    }
}
