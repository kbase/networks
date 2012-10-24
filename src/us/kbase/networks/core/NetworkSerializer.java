package us.kbase.networks.core;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.uci.ics.jung.graph.Graph;


public final class NetworkSerializer extends JsonSerializer<Network> {
	DatasetSerializer dser = new DatasetSerializer();

	@Override
	public void serialize(Network network, JsonGenerator jg,
			SerializerProvider sp) throws IOException,
			JsonProcessingException {
		
		if( network == null) {
			sp.defaultSerializeNull(jg);
			return;
		}
		
		jg.writeStartObject();
		
		jg.writeStringField("id", network.getId());
		jg.writeStringField("name", network.getName());

		Graph<Node,Edge> graph = network.getGraph();
		jg.writeFieldName("nodes");
		jg.writeStartArray();
		Collection<Node> nodes = graph.getVertices();
		for(Node node : nodes) {
			jg.writeStartObject();
			
				jg.writeStringField("id", node.getId());
				jg.writeStringField("name", node.getName());
				jg.writeFieldName("entity"); 
				jg.writeStartObject();
					jg.writeStringField("id", node.getEntityId());
				jg.writeEndObject();
				
				// TODO: Not included in client type spec
				// It could be properties later
				//jg.writeStringField("nodeType", node.getType().toString());
	
				jg.writeFieldName("properties");
				jg.writeStartObject();
					Set<String> pn = node.getPropertyNames();
					for(Iterator<String> pit = pn.iterator();pit.hasNext(); ) {
						String key = pit.next();
						jg.writeStringField(key, node.getProperty(key) );
					}
				jg.writeEndObject();
				
				jg.writeFieldName("userAnnotations");
				jg.writeStartObject();
					Set<String> uan = node.getUserAnnotationNames();
					for(Iterator<String> pit = uan.iterator();pit.hasNext(); ) {
						String key = pit.next();
						jg.writeStringField(key, node.getUserAnnotation(key) );
					}
				jg.writeEndObject();
			
			jg.writeEndObject();
		}
		jg.writeEndArray();
		
		jg.writeFieldName("edges");
		jg.writeStartArray();
		Collection<Edge> edges = graph.getEdges();
		for(Edge edge : edges) {
			jg.writeStartObject();
			
				jg.writeStringField("id", edge.getId());
				jg.writeStringField("name", edge.getName());
				jg.writeStringField("nodeId1", graph.getSource(edge).getId());
				jg.writeStringField("nodeId2", graph.getDest(edge).getId());

				// TODO: Not included in client type spec
				// jg.writeFieldName("dataset");
				// dser.serialize(edge.getDataset(), jg, sp);

				// TODO: Not included in client type spec
				// It could be properties later
				//jg.writeStringField("nodeType", node.getType().toString());
	
				jg.writeFieldName("properties");
				jg.writeStartObject();
					Set<String> pn = edge.getPropertyNames();
					for(Iterator<String> pit = pn.iterator();pit.hasNext(); ) {
						String key = pit.next();
						jg.writeStringField(key, edge.getProperty(key) );
					}
				jg.writeEndObject();
				
				jg.writeFieldName("userAnnotations");
				jg.writeStartObject();
					Set<String> uan = edge.getUserAnnotationNames();
					for(Iterator<String> pit = uan.iterator();pit.hasNext(); ) {
						String key = pit.next();
						jg.writeStringField(key, edge.getUserAnnotation(key) );
					}
				jg.writeEndObject();
			
			jg.writeEndObject();
		}
		jg.writeEndArray();
		

		/* TODO: The following is not on client side objects
		 * We may add them in type spec and use the followings
		jg.writeFieldName("properties");
		jg.writeStartObject();
		Set<String> pn = network.getPropertyNames();
		for(Iterator<String> pit = pn.iterator();pit.hasNext(); ) {
			String key = pit.next();
			jg.writeStringField(key, network.getProperty(key) );
		}
		jg.writeEndObject();

		jg.writeFieldName("userAnnotations");
		jg.writeStartObject();
		Set<String> uan = network.getUserAnnotationNames();
		for(Iterator<String> pit = uan.iterator();pit.hasNext(); ) {
			String key = pit.next();
			jg.writeStringField(key, network.getUserAnnotation(key) );
		}
		jg.writeEndObject();
		*/
		
		
		jg.writeEndObject();
	}  
}
