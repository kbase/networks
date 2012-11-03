package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$datasetSource2Datasets_deserializer extends JsonDeserializer<$return$datasetSource2Datasets>
{
    public $return$datasetSource2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$datasetSource2Datasets res = new $return$datasetSource2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$datasetSource2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasets = p.readValueAs(new TypeReference<List<Dataset>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$datasetSource2Datasets_deserializer with token " + t);

	return res;
    }
}
