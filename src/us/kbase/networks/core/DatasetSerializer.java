package us.kbase.networks.core;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;


public final class DatasetSerializer extends JsonSerializer<Dataset> {

	@Override
	public void serialize(Dataset ds, JsonGenerator jg,
			SerializerProvider sp) throws IOException,
			JsonProcessingException {
		
		if( ds == null) {
			sp.defaultSerializeNull(jg);
			return;
		}
		
		jg.writeStartObject();
		
		jg.writeStringField("id", ds.getId());
		jg.writeStringField("name", ds.getName());
		jg.writeStringField("description", ds.getDescription());
		jg.writeStringField("networkType", ds.getNetworkType().toString());
		jg.writeStringField("datasetSource", ds.getDatasetSource().toString());

		jg.writeFieldName("taxon");
		jg.writeStartArray();
		List<Taxon> tl = ds.getTaxons();
		for(Taxon t : tl) {
			jg.writeString(t.getGenomeId());
		}
		jg.writeEndArray();

		jg.writeFieldName("properties");
		jg.writeStartObject();
		Set<String> pn = ds.getPropertyNames();
		for(Iterator<String> pit = pn.iterator();pit.hasNext(); ) {
			String key = pit.next();
			jg.writeStringField(key, ds.getProperty(key) );
		}
		jg.writeEndObject();
		
		jg.writeEndObject();
	}  
}
