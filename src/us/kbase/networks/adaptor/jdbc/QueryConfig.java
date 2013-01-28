package us.kbase.networks.adaptor.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import us.kbase.networks.adaptor.AdaptorException;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

public class QueryConfig{
	static final String scalarTag = "?";
	static final String arrayTag = "%s";
			
	Configuration config;

	String sqlTag;
	String propertySuffix;
	String sql;
	
	String preparedSql;
	Hashtable<String, QueryProperty> propertyPefix2QueryPropertyHash = new Hashtable<String, QueryProperty>();
			
	
	public QueryConfig(Configuration config, String sqlTag) throws AdaptorException 
	{
		try{
			this.config = config;
			this.sqlTag = sqlTag;
			this.propertySuffix = sqlTag.substring(Term.SQL_Statement_Prefix.length());
			
			buildConfiguration();
			this.preparedSql = "";			
		}catch(Exception e)
		{
			throw new AdaptorException(" Property suffix = " + propertySuffix
					+ "; SQL=" + sql
					+ "; " + e.getMessage() , e);
		}
	}
	
	private void buildConfiguration() throws SQLException
	{
		propertyPefix2QueryPropertyHash.clear();
		for(String propertyPrefix: Term.Prefix_QueryIndexes)
		{
			registerQueryProperty(propertyPrefix);				
		}
		
		sql = config.getString(sqlTag);
		updatePlaceholderScalarInfo();
	}
	
	private void registerQueryProperty(String propertyPrefix) {
		String indexStr = config.getString(propertyPrefix  + propertySuffix, "");
		if(indexStr.length() == 0) return;
		
		QueryProperty queryProperty = new QueryProperty(propertyPrefix);
		for(String val: indexStr.split(":"))
		{
			QueryPropertyPlaceholder placeholder = new QueryPropertyPlaceholder( Integer.parseInt(val), true );
			queryProperty.propertyPlaceholders.add(placeholder);
		}
		propertyPefix2QueryPropertyHash.put(propertyPrefix, queryProperty);			
	}
	
	private void updatePlaceholderScalarInfo() {
		
		List<QueryPropertyPlaceholder> placeholders = getPropertyPlaceholders();
		Collections.sort(placeholders);
		
		Iterator<QueryPropertyPlaceholder> placeholderIterator = placeholders.iterator();
		int posOffset = 0;
		for(int posScalar = sql.indexOf(scalarTag), posArray = sql.indexOf(arrayTag); 
			posScalar >= 0 || posArray >= 0; 
			posScalar = sql.indexOf(scalarTag, posOffset), posArray = sql.indexOf(arrayTag, posOffset))
		{			
			QueryPropertyPlaceholder placeholder = placeholderIterator.next();
			if(posScalar >= 0 && ( posArray == -1 || posScalar < posArray) )
			{
				placeholder.isScalar = true;
				posOffset = posScalar + 1;
			} else{
				placeholder.isScalar = false;
				posOffset = posArray + 1;
			}
		}				
	}
	
	public PreparedStatement buildPreparedStatement(C3P0ProxyConnection connection) throws SQLException, AdaptorException
	{
		return buildPreparedStatement(connection, new QueryPropertyArguments[]{});
	}
	
	public PreparedStatement buildPreparedStatement(C3P0ProxyConnection connection, QueryPropertyArguments ... propertiesArguments) throws SQLException, AdaptorException
	{
		boolean isPrepared = prepareConfiguration(propertiesArguments);
		if(!isPrepared) return null;
		PreparedStatement pst = connection.prepareStatement(preparedSql);
		
		for(QueryPropertyArguments propertyArguments: propertiesArguments){
			
			QueryProperty queryProperty = getQueryProperty(propertyArguments.properyPrefix);
			if(queryProperty != null) queryProperty.setStringParams(pst, propertyArguments.values);							
		}
		return pst;
	}		

	private QueryProperty getQueryProperty(String propertyPrefix)
	{
		return propertyPefix2QueryPropertyHash.get(propertyPrefix);
	}	
	
	
	private boolean prepareConfiguration(QueryPropertyArguments ... propertiesArguments) throws AdaptorException {
		boolean isPrepared = prepareProperyPlaceholders(propertiesArguments);
		if(!isPrepared) return false;
		
		prepareSql();
		return true;
	}

	private void cleanArgumentInfo()
	{
		for(QueryProperty property: propertyPefix2QueryPropertyHash.values())
		{
			property.cleanArgumentInfo();
		}
	}		
	
	private boolean prepareProperyPlaceholders(QueryPropertyArguments ... propertiesArguments) throws AdaptorException {
		
		// Set the number of arguments for each placeholder
		cleanArgumentInfo();
		setArgumentCounts(propertiesArguments);

		
		List<QueryPropertyPlaceholder> placeholders = getPropertyPlaceholders();
		Collections.sort(placeholders);
		//TODO: Check that all values are present and not duplicated...
		
		boolean hasAllArguments = true;
		int placeholderOffeset = 0; 
		for(QueryPropertyPlaceholder placeholder: placeholders)
		{
			if(placeholder.argumentCount > 0)
			{
				placeholder.argumentStartIndex = placeholder.index + placeholderOffeset;
				placeholderOffeset += placeholder.argumentCount - 1;
			}
			else{
				hasAllArguments = false;
			}
		}	
		
		return hasAllArguments;
	}
	
	private void setArgumentCounts(QueryPropertyArguments ... propertiesArguments) throws AdaptorException {
		for(QueryPropertyArguments propertyArguments: propertiesArguments)
		{
			QueryProperty property = getQueryProperty(propertyArguments.properyPrefix);
			if(property == null) continue;
			for(QueryPropertyPlaceholder placeholder: property.propertyPlaceholders)
			{
				if(placeholder.isScalar && propertyArguments.values.length > 1)
				{
					throw new AdaptorException(property.propertyPrefix 
							+ " is scalar, but " + propertyArguments.values.length
							+ " arguments are passed");
				}
				placeholder.argumentCount = propertyArguments.values.length;
			}
		}			
	}

	private List<QueryPropertyPlaceholder> getPropertyPlaceholders() {
		return getPropertyPlaceholders(true, true);
	}
	
	private List<QueryPropertyPlaceholder> getPropertyPlaceholders(boolean isScalar, boolean isArray) {
		List<QueryPropertyPlaceholder> placeholders = new ArrayList<QueryPropertyPlaceholder>();
		for(QueryProperty property: propertyPefix2QueryPropertyHash.values())
		{
			for(QueryPropertyPlaceholder placeholder: property.propertyPlaceholders)
			{
				if( (isScalar && placeholder.isScalar) || (isArray && !placeholder.isScalar) )
				{
					placeholders.add(placeholder);
				}
			}
		}
		return placeholders;
	}

	private void prepareSql() {

		preparedSql = sql;
		List<QueryPropertyPlaceholder> arrayPlaceholders = getPropertyPlaceholders(false, true);
		Collections.sort(arrayPlaceholders);

		if(arrayPlaceholders.size() > 0)
		{
			preparedSql = String.format(sql, (Object[]) getPlaceholderPatterns(arrayPlaceholders)); 
		}
	}		
	
	private String[] getPlaceholderPatterns(List<QueryPropertyPlaceholder> arrayPlaceholders) {
		String[] placeholderStrs = new String[arrayPlaceholders.size()];
		for(int i = 0; i < arrayPlaceholders.size(); i++)
		{
			System.out.println("From  getPlaceholderPatterns: " + propertySuffix);
			placeholderStrs[i] = arrayPlaceholders.get(i).toStringPattern();
		}
		return placeholderStrs;		
	}
}
