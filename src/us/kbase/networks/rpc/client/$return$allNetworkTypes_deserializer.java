package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$allNetworkTypes_deserializer extends JsonDeserializer<$return$allNetworkTypes>
{
    public $return$allNetworkTypes deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$allNetworkTypes res = new $return$allNetworkTypes();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$allNetworkTypes_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.networkTypes = p.readValueAs(new TypeReference<List<String>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$allNetworkTypes_deserializer with token " + t);

	return res;
    }
}
