package us.kbase.networks.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public final class DatasetSourceSerializer extends JsonSerializer<DatasetSource> {

	@Override
	public void serialize(DatasetSource dsSource, JsonGenerator jg,
			SerializerProvider sp) throws IOException,
			JsonProcessingException {
		
		if( dsSource == null) {
			sp.defaultSerializeNull(jg);
			return;
		}
		
		jg.writeStartObject();
		
		jg.writeStringField("id", dsSource.getId());
		jg.writeStringField("name", dsSource.getName());		
		jg.writeStringField("reference", dsSource.toString());				
		jg.writeStringField("description", dsSource.getDescription());
		jg.writeStringField("resource_url", dsSource.getResourceURL());
		
		jg.writeEndObject();
	}  
}
