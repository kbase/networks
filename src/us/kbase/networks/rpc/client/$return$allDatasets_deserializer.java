package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$allDatasets_deserializer extends JsonDeserializer<$return$allDatasets>
{
    public $return$allDatasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$allDatasets res = new $return$allDatasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$allDatasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasets = p.readValueAs(new TypeReference<List<Dataset>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$allDatasets_deserializer with token " + t);

	return res;
    }
}
