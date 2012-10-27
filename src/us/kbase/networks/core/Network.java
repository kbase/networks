package us.kbase.networks.core;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.uci.ics.jung.graph.Graph;

@JsonSerialize(using = NetworkSerializer.class)
@JsonDeserialize(using = NetworkDeserializer.class)
public class Network {

	private String id;
	private String name;
	private Graph<Node, Edge> graph;
	
	private Map<String,String> properties = new Hashtable<String,String>();
	private Map<String,String> userAnnotations = new Hashtable<String,String>();	
	
	public Network(String id, String name, Graph<Node, Edge> graph) {
		super();
		this.id = id;
		this.name = name;
		this.graph = graph;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Graph<Node, Edge> getGraph() {
		return graph;
	}
	
	public void addUserAnnotation(String name, String value)
	{
		userAnnotations.put(name, value);
	}
	
	public Set<String> getUserAnnotationNames()
	{
		return userAnnotations.keySet();
	}
	
	public String getUserAnnotation(String name)
	{
		return userAnnotations.get(name);
	}
	
	public void addProperty(String name, String value)
	{
		properties.put(name, value);
	}
	
	public Set<String> getPropertyNames()
	{
		return properties.keySet();
	}
	
	public String getProperty(String name)
	{
		return properties.get(name);
	}	
}
