package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$getDatasets_serializer extends JsonSerializer<$args$getDatasets>
{
    public void serialize($args$getDatasets value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.ParameterList);
	jgen.writeEndArray();
    }
}
