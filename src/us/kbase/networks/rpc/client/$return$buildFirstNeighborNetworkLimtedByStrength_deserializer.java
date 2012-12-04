package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildFirstNeighborNetworkLimtedByStrength_deserializer extends JsonDeserializer<$return$buildFirstNeighborNetworkLimtedByStrength>
{
    public $return$buildFirstNeighborNetworkLimtedByStrength deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$buildFirstNeighborNetworkLimtedByStrength res = new $return$buildFirstNeighborNetworkLimtedByStrength();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$buildFirstNeighborNetworkLimtedByStrength_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.network = p.readValueAs(Network.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$buildFirstNeighborNetworkLimtedByStrength_deserializer with token " + t);

	return res;
    }
}
