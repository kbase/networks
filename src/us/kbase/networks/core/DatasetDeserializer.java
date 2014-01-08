package us.kbase.networks.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class DatasetDeserializer extends JsonDeserializer<Dataset> {
	
	public Dataset deserialize(JsonNode root) {
		JsonNode idNode = root.path("id");
		JsonNode nameNode = root.path("name");
		JsonNode descNode = root.path("description");
		JsonNode networkTypeNode = root.path("networkType");
		JsonNode datasetSourceNode = root.path("sourceRef");
		JsonNode taxonNode = root.path("taxons");
		JsonNode propertiesNode = root.path("properties");
		List<Taxon> tl = new ArrayList<Taxon>();
		for(Iterator<JsonNode> it = taxonNode.iterator(); it.hasNext(); ) {
			JsonNode n = it.next();
			Taxon t =new Taxon(n.asText()); 
			tl.add(t);
		}
		
		Dataset result = new Dataset(idNode.asText(),nameNode.asText(), descNode.asText(), 
				Enum.valueOf(NetworkType.class, networkTypeNode.asText()), 
				Enum.valueOf(DatasetSource.class, datasetSourceNode.asText()), tl);
		
		for(Iterator<String> it = propertiesNode.fieldNames(); it.hasNext(); ) {
			String key = it.next();
			String value = propertiesNode.path(key).asText();
			result.addProperty(key, value);
		}
		
		return result;
		
	}
	
	
	@Override
	public Dataset deserialize(JsonParser jp, DeserializationContext dc)
			throws IOException, JsonProcessingException {
		ObjectMapper m = new ObjectMapper();
		// TODO: Error checking code later
		JsonNode root = m.readTree(jp);		
		return deserialize(root);
	}

}
