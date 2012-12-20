package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildFirstNeighborNetworkLimtedByStrength_deserializer extends JsonDeserializer<$args$buildFirstNeighborNetworkLimtedByStrength>
{
    public $args$buildFirstNeighborNetworkLimtedByStrength deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$buildFirstNeighborNetworkLimtedByStrength res = new $args$buildFirstNeighborNetworkLimtedByStrength();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$buildFirstNeighborNetworkLimtedByStrength_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.entityIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.edgeTypes = p.readValueAs(new TypeReference<List<String>>(){});
	res.cutOff = p.readValueAs(Float.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$buildFirstNeighborNetworkLimtedByStrength_deserializer with token " + t);

	return res;
    }
}
