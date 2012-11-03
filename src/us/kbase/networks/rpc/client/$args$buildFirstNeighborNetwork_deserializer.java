package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildFirstNeighborNetwork_deserializer extends JsonDeserializer<$args$buildFirstNeighborNetwork>
{
    public $args$buildFirstNeighborNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$buildFirstNeighborNetwork res = new $args$buildFirstNeighborNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$buildFirstNeighborNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.geneId = p.readValueAs(String.class);
	res.edgeTypes = p.readValueAs(new TypeReference<List<String>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$buildFirstNeighborNetwork_deserializer with token " + t);

	return res;
    }
}
