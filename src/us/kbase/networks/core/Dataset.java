package us.kbase.networks.core;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = DatasetSerializer.class)
@JsonDeserialize(using = DatasetDeserializer.class)
public class Dataset {

	private String id;
	private String name;
	private String description;
	
	private NetworkType networkType;
	private DatasetSource datasetSource;
	private List<Taxon> taxons;

	
	private Map<String,String> properties = new Hashtable<String,String>();


	public Dataset(String id, String name, String description,
			NetworkType networkType, DatasetSource datasetSource,
			List<Taxon> taxons) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.networkType = networkType;
		this.datasetSource = datasetSource;
		this.taxons = taxons;
	}
	
	public Dataset(String id, String name, String description,
			NetworkType networkType, DatasetSource datasetSource,
			Taxon taxon) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.networkType = networkType;
		this.datasetSource = datasetSource;
		this.taxons = new Vector<Taxon>();
		this.taxons.add(taxon);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dataset other = (Dataset) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	public String getDescription() {
		return description;
	}


	public NetworkType getNetworkType() {
		return networkType;
	}


	public DatasetSource getDatasetSource() {
		return datasetSource;
	}


	public List<Taxon> getTaxons() {
		return taxons;
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
