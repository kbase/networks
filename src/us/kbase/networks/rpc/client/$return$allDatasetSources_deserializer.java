package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $return$allDatasetSources_deserializer extends JsonDeserializer<$return$allDatasetSources>
{
    public $return$allDatasetSources deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$return$allDatasetSources res = new $return$allDatasetSources();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $return$allDatasetSources_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.datasetSources = p.readValueAs(new TypeReference<List<DatasetSource>>(){});
	JsonToken t = p.nextToken();
//	System.out.println("exit $return$allDatasetSources_deserializer with token " + t);

	return res;
    }
}
