package us.kbase.kbasenetworks.core;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public final class Edge {
	
	private String id;		
	private String name;	
	private Dataset dataset;
	
	private float strength;
	private float confidence;
	
	private Map<String,String> properties = new Hashtable<String, String>();	
	private Map<String,String> userAnnotations = new Hashtable<String,String>();
	
	
	public Edge(String id, String name, Dataset dataset) {
		super();
		this.id = id;
		this.name = name;
		this.dataset = dataset;
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
	
	public float getStrength() {
		return strength;
	}
	
	public void setStrength(float strength) {
		this.strength = strength;
	}
	
	public float getConfidence() {
		return confidence;
	}
	
	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Dataset getDataset() {
		return dataset;
	}	
	
	public String toString()
	{
		return name;
	}
	
}
