package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$networkType2Datasets_serializer extends JsonSerializer<$return$networkType2Datasets>
{
    public void serialize($return$networkType2Datasets value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.datasets);
	jgen.writeEndArray();
    }
}
