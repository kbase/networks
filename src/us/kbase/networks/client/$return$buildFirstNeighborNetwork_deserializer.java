package us.kbase.networks.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildFirstNeighborNetwork_deserializer extends JsonDeserializer<$return$buildFirstNeighborNetwork>
{
    public $return$buildFirstNeighborNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$buildFirstNeighborNetwork res = new $return$buildFirstNeighborNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$buildFirstNeighborNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.network = p.readValueAs(Network.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$buildFirstNeighborNetwork_deserializer with token " + t);

	return res;
    }
}
