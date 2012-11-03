package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$networkType2Datasets_deserializer extends JsonDeserializer<$args$networkType2Datasets>
{
    public $args$networkType2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$networkType2Datasets res = new $args$networkType2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$networkType2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.networkType = p.readValueAs(String.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$networkType2Datasets_deserializer with token " + t);

	return res;
    }
}
