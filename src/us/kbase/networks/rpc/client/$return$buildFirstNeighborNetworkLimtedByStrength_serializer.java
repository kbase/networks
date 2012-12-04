package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildFirstNeighborNetworkLimtedByStrength_serializer extends JsonSerializer<$return$buildFirstNeighborNetworkLimtedByStrength>
{
    public void serialize($return$buildFirstNeighborNetworkLimtedByStrength value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.network);
	jgen.writeEndArray();
    }
}
