package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$allDatasetSources_deserializer extends JsonDeserializer<$args$allDatasetSources>
{
    public $args$allDatasetSources deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$allDatasetSources res = new $args$allDatasetSources();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$allDatasetSources_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$allDatasetSources_deserializer with token " + t);

	return res;
    }
}
