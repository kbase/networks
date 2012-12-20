package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildFirstNeighborNetworkLimtedByStrength_serializer extends JsonSerializer<$args$buildFirstNeighborNetworkLimtedByStrength>
{
    public void serialize($args$buildFirstNeighborNetworkLimtedByStrength value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.datasetIds);
	jgen.writeObject(value.entityIds);
	jgen.writeObject(value.edgeTypes);
	jgen.writeObject(value.cutOff);
	jgen.writeEndArray();
    }
}
