package us.kbase.kbasenetworks.adaptor.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryProperty{
	String propertyPrefix;
	List<QueryPropertyPlaceholder> propertyPlaceholders = new ArrayList<QueryPropertyPlaceholder>();
	
	public QueryProperty(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	public void setStringParams(PreparedStatement pst, String ... values) throws SQLException {			
		
		for(QueryPropertyPlaceholder placeholder: propertyPlaceholders)
		{
			for(int i = 0, index = placeholder.argumentStartIndex; i < placeholder.argumentCount; i++, index++)
			{
				pst.setString(index, values[i]);
				
			}
		}			
	}
	
	public void cleanArgumentInfo()
	{
		for(QueryPropertyPlaceholder propertyPlaceholder : propertyPlaceholders)
		{
			propertyPlaceholder.cleanArgumentInfo();
		}
	}
}
	