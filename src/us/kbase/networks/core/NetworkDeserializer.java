package us.kbase.networks.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.jung.graph.Graph;

public final class NetworkDeserializer extends JsonDeserializer<Network> {
	
	DatasetDeserializer datasetDeserializer = new DatasetDeserializer();
	
	public Map<String,String> deserializeMap(JsonNode node) {
		Map<String,String> result = new HashMap<String,String>();
		for (Iterator<String> it = node.fieldNames(); it.hasNext();) {
			String key = it.next();
			String value = node.path(key).asText();
			result.put(key, value);
		}
		return result;
	}	
	
	public Node deserializeNode(JsonNode root) throws IOException {
		JsonNode idNode = root.path("id");
		JsonNode nameNode = root.path("name");
		JsonNode entityNode = root.path("entity");
		JsonNode entityIdNode = entityNode.path("id");
		JsonNode typeNode = entityNode.path("type");
		JsonNode propertiesNode = root.path("properties");
		JsonNode userAnnotationsNode = root.path("userAnnotations");
		
		Node node = null;
		switch(NodeType.valueOf(typeNode.textValue())) {
			case GENE:
				node = Node.buildGeneNode(idNode.asText(), nameNode.asText(), new Entity(entityIdNode.asText()));
				break;
			case PROTEIN:
				node = Node.buildProteinNode(idNode.asText(), nameNode.asText(), new Entity(entityIdNode.asText()));
				break;
			case CLUSTER:
				node = Node.buildClusterNode(idNode.asText(), nameNode.asText(), new Entity(entityIdNode.asText()));
				break;
			default:
				throw new IOException("No appropriate NodeType : " + typeNode.textValue());      // TODO: add new exception type later
		}
		Map<String,String> properties = deserializeMap(propertiesNode);
		Map<String,String> userAnnotations = deserializeMap(userAnnotationsNode);
		for(String key : properties.keySet()) {
			node.addProperty(key, properties.get(key));
		}
		for(String key : userAnnotations.keySet()) {
			node.addUserAnnotation(key, userAnnotations.get(key));
		}		
		return node;
	}	
	
	@Override
	public Network deserialize(JsonParser jp, DeserializationContext dc)
			throws IOException, JsonProcessingException {
		ObjectMapper m = new ObjectMapper();
		// TODO: Error checking code later
		JsonNode root = m.readTree(jp);
		JsonNode idNode = root.path("id");
		JsonNode nameNode = root.path("name");
		JsonNode nodesNode = root.path("nodes");
		JsonNode edgesNode = root.path("edges");
		JsonNode datasetsNode = root.path("datasets");
		JsonNode propertiesNode = root.path("properties");
		JsonNode userAnnotationsNode = root.path("userAnnotations");

		Map<String,String> properties = deserializeMap(propertiesNode);
		Map<String,String> userAnnotations = deserializeMap(userAnnotationsNode);
		if(properties.get("graphType") == null) {
			throw new IOException("graphType has to be defined in Network as properties");
		}
		
		// Graph<Node,Edge>
		Class<?> graphClass;
		try {
			graphClass = Class.forName(properties.get("graphType"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage());
		}
		Graph<Node,Edge> graph = null;
		try {
			graph = (Graph<Node, Edge>) graphClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new IOException(e.getMessage());
		}
		
		
		// Deserialize Nodes
		Map<String, Node> nm = new HashMap<String, Node>();
		for (Iterator<JsonNode> it = nodesNode.iterator(); it.hasNext();) {
			JsonNode n = it.next();
			Node node = deserializeNode(n);
			graph.addVertex(node);
			nm.put(node.getId(),node);
		}

		Map<String,Dataset> dm = new HashMap<String,Dataset>();
		for (Iterator<JsonNode> it = datasetsNode.iterator(); it.hasNext();) {
			JsonNode n = it.next();
			Dataset d  = datasetDeserializer.deserialize(n); 
			dm.put(d.getId(), d);
		}
		
		// Deserialize Edges
		for (Iterator<JsonNode> it = edgesNode.iterator(); it.hasNext();) {
			JsonNode edgeNode                = it.next();
			JsonNode idEdgeNode              = edgeNode.path("id");
			JsonNode nameEdgeNode            = edgeNode.path("name");
			JsonNode nodeId1Node             = edgeNode.path("nodeId1");
			JsonNode nodeId2Node             = edgeNode.path("nodeId2");
			JsonNode confidenceNode          = edgeNode.path("confidence");
			JsonNode strengthNode            = edgeNode.path("strength");
			JsonNode datasetIdNode           = edgeNode.path("datasetId");
			JsonNode propertiesEdgeNode      = edgeNode.path("properties");
			JsonNode userAnnotationsEdgeNode = edgeNode.path("userAnnotations");
			
			if(!dm.containsKey(datasetIdNode.asText())) {
				throw new IOException("Couldn't find proper dataset from the dataset list of Network : " + datasetIdNode.asText());
			}
			Edge edge = new Edge(idEdgeNode.asText(), nameEdgeNode.asText(), dm.get(dm.get(datasetIdNode.asText())));
			edge.setConfidence((float)confidenceNode.asDouble());
			edge.setStrength((float)strengthNode.asDouble());
			
			Map<String,String> propertiesEdge = deserializeMap(propertiesEdgeNode);
			Map<String,String> userAnnotationsEdge = deserializeMap(userAnnotationsEdgeNode);
			for(String key : propertiesEdge.keySet()) {
				edge.addProperty(key, propertiesEdge.get(key));
			}
			for(String key : userAnnotationsEdge.keySet()) {
				edge.addUserAnnotation(key, userAnnotationsEdge.get(key));
			}		

			if(!nm.containsKey(nodeId1Node.asText())) {
				throw new IOException("Couldn't find proper node id from the dataset list of Network : " + nodeId1Node.asText());
			}
			if(!nm.containsKey(nodeId2Node.asText())) {
				throw new IOException("Couldn't find proper node id from the dataset list of Network : " + nodeId2Node.asText());
			}
			
			graph.addEdge(edge, nm.get(nodeId1Node.asText()), nm.get(nodeId2Node.asText()));
		}
		
		Network result = new Network(idNode.asText(), nameNode.asText(), graph);
		
		for(String key : properties.keySet()) {
			result.addProperty(key, properties.get(key));
		}
		for(String key : userAnnotations.keySet()) {
			result.addUserAnnotation(key, userAnnotations.get(key));
		}

		return result;
	}

}