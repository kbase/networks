package us.kbase.networks.core;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public final class Node {
	
	private String id;	
	private String name;
	private Entity entity;
	private NodeType type;
			
	private Map<String,String> properties = new Hashtable<String,String>();
	private Map<String,String> userAnnotations = new Hashtable<String,String>();
	
	private Node(String id, String name, Entity entity, NodeType type) {
		super();
		this.id = id;
		this.name = name;
		this.entity = entity;
		this.type = type;
	}

	public static Node buildGeneNode(String id, String name, Entity entity)
	{
		return new Node(id, name, entity, NodeType.GENE);
	}
	
	public static Node buildProteinNode(String id, String name, Entity entity)
	{
		return new Node(id, name, entity, NodeType.PROTEIN);
	}
	
	public static Node buildClusterNode(String id, String name, Entity entity)
	{
		return new Node(id, name, entity, NodeType.CLUSTER);
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

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Entity getEntity() {
		return entity;
	}

	public NodeType getType() {
		return type;
	}
	
}