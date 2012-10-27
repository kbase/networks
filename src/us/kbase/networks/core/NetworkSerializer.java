package us.kbase.networks.core;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


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
	
			// nodes
			Graph<Node,Edge> graph = network.getGraph();
			jg.writeFieldName("nodes");
			Collection<Node> nodes = graph.getVertices();
			jg.writeStartArray();
			for(Node node : nodes) {
				jg.writeStartObject();
				
					jg.writeStringField("id", node.getId());
					jg.writeStringField("name", node.getName());
					jg.writeFieldName("entity"); 
					jg.writeStartObject();
						jg.writeStringField("id", node.getEntityId());
					jg.writeEndObject();
					jg.writeStringField("type", node.getType().toString());
					
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
			
			
			// edges
			Set<Dataset> datasetList = new HashSet<Dataset>();
			
			jg.writeFieldName("edges");
			jg.writeStartArray();
			Collection<Edge> edges = graph.getEdges();
			for(Edge edge : edges) {
				jg.writeStartObject();
				
					jg.writeStringField("id", edge.getId());
					jg.writeStringField("name", edge.getName());
					if(graph.getSource(edge) != null) {
						jg.writeStringField("nodeId1", graph.getSource(edge).getId());
						jg.writeStringField("nodeId2", graph.getDest(edge).getId());
					}
					else {
						Pair<Node> nodePair = graph.getEndpoints(edge);
						jg.writeStringField("nodeId1", nodePair.getFirst().getId());
						jg.writeStringField("nodeId2", nodePair.getSecond().getId());
					}
					jg.writeNumberField("strength", edge.getStrength());
					jg.writeNumberField("confidence", edge.getConfidence());

					datasetList.add(edge.getDataset());
					jg.writeStringField("datasetId", edge.getDataset().getId());
		
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

			// datasets
			jg.writeFieldName("datasets");
			jg.writeStartArray();
			for(Dataset dataset : datasetList) {
				dser.serialize(dataset, jg, sp);
			}
			jg.writeEndArray();

	
			jg.writeFieldName("properties");
			jg.writeStartObject();
			jg.writeStringField("graphType", graph.getClass().getName());
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
		
		
		jg.writeEndObject();
	}  
}
