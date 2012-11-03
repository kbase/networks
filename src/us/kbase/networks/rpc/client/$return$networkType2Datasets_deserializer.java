package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$networkType2Datasets_deserializer extends JsonDeserializer<$return$networkType2Datasets>
{
    public $return$networkType2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$networkType2Datasets res = new $return$networkType2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$networkType2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasets = p.readValueAs(new TypeReference<List<Dataset>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$networkType2Datasets_deserializer with token " + t);

	return res;
    }
}
