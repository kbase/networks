package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$datasetSource2Datasets_deserializer extends JsonDeserializer<$args$datasetSource2Datasets>
{
    public $args$datasetSource2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$datasetSource2Datasets res = new $args$datasetSource2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$datasetSource2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetSourceRef = p.readValueAs(String.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$datasetSource2Datasets_deserializer with token " + t);

	return res;
    }
}
