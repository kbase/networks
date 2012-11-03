package us.kbase.networks.rpc.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$taxon2Datasets_deserializer extends JsonDeserializer<$args$taxon2Datasets>
{
    public $args$taxon2Datasets deserialize(JsonParser p, DeserializationContext ctx)
	throws IOException, JsonProcessingException
    {
	$args$taxon2Datasets res = new $args$taxon2Datasets();
	if (!p.isExpectedStartArrayToken())
	{
		System.out.println("Bad parse in $args$taxon2Datasets_deserializer: " + p.getCurrentToken());
		return null;
	}
	p.nextToken();
	res.taxon = p.readValueAs(String.class);
	JsonToken t = p.nextToken();
//	System.out.println("exit $args$taxon2Datasets_deserializer with token " + t);

	return res;
    }
}
