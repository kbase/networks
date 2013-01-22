package us.kbase.networks.adaptor.jdbc;

public class QueryPropertyArguments{
	String properyPrefix;
	String[] values;
	
	public QueryPropertyArguments(String properyPrefix, String[] values) {
		this.properyPrefix = properyPrefix;
		this.values = values;
	}
}
