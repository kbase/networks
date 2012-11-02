package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildNetwork_deserializer extends JsonDeserializer<$return$buildNetwork>
{
    public $return$buildNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$buildNetwork res = new $return$buildNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$buildNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.network = p.readValueAs(Network.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$buildNetwork_deserializer with token " + t);

	return res;
    }
}
