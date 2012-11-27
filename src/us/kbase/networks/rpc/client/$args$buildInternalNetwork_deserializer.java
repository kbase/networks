package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildInternalNetwork_deserializer extends JsonDeserializer<$args$buildInternalNetwork>
{
    public $args$buildInternalNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$buildInternalNetwork res = new $args$buildInternalNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$buildInternalNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.geneIds = p.readValueAs(new TypeReference<List<String>>(){});
	res.edgeTypes = p.readValueAs(new TypeReference<List<String>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$buildInternalNetwork_deserializer with token " + t);

	return res;
    }
}