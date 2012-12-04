package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildInternalNetworkLimitedByStrength_deserializer extends JsonDeserializer<$args$buildInternalNetworkLimitedByStrength>
{
    public $args$buildInternalNetworkLimitedByStrength deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$buildInternalNetworkLimitedByStrength res = new $args$buildInternalNetworkLimitedByStrength();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$buildInternalNetworkLimitedByStrength_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.geneIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.edgeTypes = p.readValueAs(new TypeReference<List<String>>(){});
	res.cutOff = p.readValueAs(Float.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$buildInternalNetworkLimitedByStrength_deserializer with token " + t);

	return res;
    }
}
