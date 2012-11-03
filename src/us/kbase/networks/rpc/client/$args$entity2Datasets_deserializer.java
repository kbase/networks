package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$entity2Datasets_deserializer extends JsonDeserializer<$args$entity2Datasets>
{
    public $args$entity2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$entity2Datasets res = new $args$entity2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$entity2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.entityId = p.readValueAs(String.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$entity2Datasets_deserializer with token " + t);

	return res;
    }
}
