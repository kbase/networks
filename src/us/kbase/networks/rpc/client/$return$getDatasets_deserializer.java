package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$getDatasets_deserializer extends JsonDeserializer<$return$getDatasets>
{
    public $return$getDatasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$getDatasets res = new $return$getDatasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$getDatasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetList = p.readValueAs(new TypeReference<List<Dataset>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$getDatasets_deserializer with token " + t);

	return res;
    }
}
