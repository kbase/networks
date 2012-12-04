package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildInternalNetworkLimitedByStrength_serializer extends JsonSerializer<$args$buildInternalNetworkLimitedByStrength>
{
    public void serialize($args$buildInternalNetworkLimitedByStrength value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.datasetIds);
	jgen.writeObject(value.geneIds);
	jgen.writeObject(value.edgeTypes);
	jgen.writeObject(value.cutOff);
	jgen.writeEndArray();
    }
}
