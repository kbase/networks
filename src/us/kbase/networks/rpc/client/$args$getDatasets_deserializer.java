package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$getDatasets_deserializer extends JsonDeserializer<$args$getDatasets>
{
    public $args$getDatasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$getDatasets res = new $args$getDatasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$getDatasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.ParameterList = p.readValueAs(new TypeReference<List<Parameter>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$getDatasets_deserializer with token " + t);

	return res;
    }
}
