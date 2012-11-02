package us.kbase.networks.typecomp;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$buildInternalNetwork_deserializer extends JsonDeserializer<$return$buildInternalNetwork>
{
    public $return$buildInternalNetwork deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$buildInternalNetwork res = new $return$buildInternalNetwork();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$buildInternalNetwork_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.network = p.readValueAs(Network.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$buildInternalNetwork_deserializer with token " + t);

	return res;
    }
}
