package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$allNetworkTypes_deserializer extends JsonDeserializer<$args$allNetworkTypes>
{
    public $args$allNetworkTypes deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$allNetworkTypes res = new $args$allNetworkTypes();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$allNetworkTypes_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$allNetworkTypes_deserializer with token " + t);

	return res;
    }
}
