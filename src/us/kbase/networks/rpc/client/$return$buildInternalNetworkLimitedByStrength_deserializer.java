package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildInternalNetworkLimitedByStrength_deserializer extends JsonDeserializer<$return$buildInternalNetworkLimitedByStrength>
{
    public $return$buildInternalNetworkLimitedByStrength deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$buildInternalNetworkLimitedByStrength res = new $return$buildInternalNetworkLimitedByStrength();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$buildInternalNetworkLimitedByStrength_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.network = p.readValueAs(Network.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$buildInternalNetworkLimitedByStrength_deserializer with token " + t);

	return res;
    }
}
