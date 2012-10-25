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

import edu.uci.ics.jung.graph.SparseMultigraph;

public final class NetworkDeserializer extends JsonDeserializer<Network> {

	DatasetDeserializer dd = new DatasetDeserializer();
	@Override
	public Network deserialize(JsonParser jp, DeserializationContext dc)
			throws IOException, JsonProcessingException {
		ObjectMapper m = new ObjectMapper();
		// TODO: Error checking code later
		JsonNode root = m.readTree(jp);
		JsonNode idNode = root.path("id");
		JsonNode nameNode = root.path("name");
		JsonNode descNode = root.path("description");
		JsonNode networkTypeNode = root.path("networkType");
		JsonNode datasetSourceNode = root.path("datasetSource");
		JsonNode taxonNode = root.path("taxon");
		JsonNode propertiesNode = root.path("properties");
		List<Taxon> tl = new ArrayList<Taxon>();
		for (Iterator<JsonNode> it = taxonNode.iterator(); it.hasNext();) {
			JsonNode n = it.next();
			Taxon t = new Taxon(n.asText());
			tl.add(t);
		}

		// TODO: temporary solution
		Network result = new Network(idNode.asText(), nameNode.asText(),
				new SparseMultigraph<Node, Edge>());
		for (Iterator<String> it = propertiesNode.fieldNames(); it.hasNext();) {
			String key = it.next();
			String value = propertiesNode.path(key).asText();
			result.addProperty(key, value);
		}

		return result;
	}

}
