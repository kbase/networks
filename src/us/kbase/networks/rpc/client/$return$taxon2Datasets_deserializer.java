package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$taxon2Datasets_deserializer extends JsonDeserializer<$return$taxon2Datasets>
{
    public $return$taxon2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$taxon2Datasets res = new $return$taxon2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$taxon2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasets = p.readValueAs(new TypeReference<List<Dataset>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$taxon2Datasets_deserializer with token " + t);

	return res;
    }
}
