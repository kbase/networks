package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$allDatasets_deserializer extends JsonDeserializer<$args$allDatasets>
{
    public $args$allDatasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$allDatasets res = new $args$allDatasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$allDatasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$allDatasets_deserializer with token " + t);

	return res;
    }
}
