package us.kbase.networks.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildNetwork_deserializer extends JsonDeserializer<$args$buildNetwork>
{
    public $args$buildNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$buildNetwork res = new $args$buildNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$buildNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.ParameterList = p.readValueAs(new TypeReference<List<Parameter>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$buildNetwork_deserializer with token " + t);

	return res;
    }
}
