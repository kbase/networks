package us.kbase.networks.adaptor.jdbc;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

import us.kbase.networks.adaptor.AdaptorException;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class GenericAdaptorConfiguration {
	private Configuration config;
	private ComboPooledDataSource cpds = null;	

	private  Hashtable<String, QueryConfig> sqlTag2configHash = new Hashtable<String,QueryConfig>();

	public GenericAdaptorConfiguration(Configuration config) throws AdaptorException 
	{
		this.config = config;
		setConnectionPool();
		
		for(Iterator<String> it = config.getKeys(); it.hasNext();)
		{
			String sqlTag = it.next();			
			if(sqlTag.startsWith(Term.SQL_Statement_Prefix)){
				sqlTag2configHash.put(sqlTag, new QueryConfig(config, sqlTag));
			}
		}
	}

	private void setConnectionPool() throws AdaptorException{
		if(cpds != null) return;

		try {
			cpds = new ComboPooledDataSource(config.getString("storename"));
			cpds.setTestConnectionOnCheckout(true);
			Iterator<String> it = config.getKeys("c3po");
			while(it.hasNext()){
				String key = it.next();
				String method_name = key.replaceAll("c3po.", "");
				String value = config.getString(key);
				try { 
					int ivalue = Integer.parseInt(value);
					java.lang.reflect.Method method = cpds.getClass().getMethod(method_name, Integer.TYPE);
					method.invoke(cpds,  ivalue);
				} catch (NumberFormatException e){
					java.lang.reflect.Method method = cpds.getClass().getMethod(method_name, String.class);
					method.invoke(cpds, value);
				}
			}
		} catch (Exception e) {
			throw new AdaptorException(e.getMessage(), e);
		}
	}		
	
	public  QueryConfig getQueryConfig(String sqlSuffix)
	{
		return sqlTag2configHash.get(Term.SQL_Statement_Prefix + sqlSuffix);		
	}

	public C3P0ProxyConnection getConnection() throws SQLException {
		return (C3P0ProxyConnection) cpds.getConnection();
	}		
}
