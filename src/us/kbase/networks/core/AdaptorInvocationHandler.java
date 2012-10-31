package us.kbase.networks.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.kbase.networks.adaptor.Adaptor;

public class AdaptorInvocationHandler implements InvocationHandler{
	private final String SOURCE_GENOME_ID = "kb|g.21765";
	private final String TARGET_GENOME_ID = "kb|g.0";
	private final String GENE_IDS_MAP_RESOURCE = "ecoli_correspondence";												  
	
	private Map<String,String> source2targetGeneIdsMap = new HashMap<String, String>();
	private Map<String,String> target2sourceGeneIdsMap = new HashMap<String, String>();
	private Set<Taxon> modifiedTaxons = new HashSet<Taxon>();
	private Adaptor adaptor;
	
	
	public AdaptorInvocationHandler(Adaptor adaptor) throws IOException
	{
		this.adaptor = adaptor;
		loadGeneIdMaps();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		init();
		processArgs(args);
		
		Object result = method.invoke(adaptor, args);
		
		processResult(result);
		revertChanges();
		
		return result;
	}	
		
	private void init() {
		modifiedTaxons.clear();
		
	}

	private void loadGeneIdMaps() throws IOException {
		InputStream is = AdaptorInvocationHandler.class.getResourceAsStream(GENE_IDS_MAP_RESOURCE);
		BufferedReader br = new BufferedReader( new InputStreamReader(is));
		for(String line = br.readLine(); line != null; line = br.readLine())
		{
			String[] terms = line.split("\t");
			if(terms.length != 4)continue;
			
			String soursGeneId = terms[3];
			String targetGeneId = terms[1];
			
			source2targetGeneIdsMap.put(soursGeneId, targetGeneId);
			target2sourceGeneIdsMap.put(targetGeneId, soursGeneId);
		}
		br.close();
	}

	private void processArgs(Object[] args) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		if(args == null)
		{
			return;
		}
		
		for(int i = 0 ; i < args.length; i++)
		{
			Object arg = args[i];
			if(arg instanceof String)
			{
				arg = processSourceGeneId((String) arg);
			}
			else if(arg instanceof List<?>)
			{
				arg = processSourceList((List<?>) arg);
			}
			else if(arg instanceof Dataset)
			{
				arg = processSourceDataset((Dataset) arg);
			}
			else if(arg instanceof Taxon)
			{
				arg = processTaxon((Taxon) arg, SOURCE_GENOME_ID, TARGET_GENOME_ID); 
			}
			else if(arg instanceof Entity)
			{
				arg = new Entity(processSourceGeneId(((Entity) arg).getId()), ((Entity) arg).getType()); 
			}
			args[i] = arg;
		}
	}

	private void revertChanges() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		for(Taxon taxon: modifiedTaxons)
		{
			processTaxon(taxon, TARGET_GENOME_ID, SOURCE_GENOME_ID);
		}		
	}


	private Object processTaxon(Taxon taxon, String genomeId1, String genomeId2) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		if(taxon.getGenomeId().equals(genomeId1))
		{
		    Class<?> c = taxon.getClass();
		    Field fGenomeId = c.getDeclaredField("genomeId");
		    fGenomeId.set(taxon, genomeId2);				
		    modifiedTaxons.add(taxon);
		}
		return taxon;
	}

	private Object processSourceDataset(Dataset dataset) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		for(Taxon taxon: dataset.getTaxons())
		{
			processTaxon(taxon, SOURCE_GENOME_ID, TARGET_GENOME_ID);
		}		
		
		return dataset;
	}

	private Object processSourceList(List<?> list) {
				
		for(int i = 0 ; i < list.size(); i++)
		{
			Object obj = list.get(i);
			if(obj instanceof String)
			{
				((List<String>) list).set(i, (String) processSourceGeneId((String)obj));
			}
			
		}
		return list;		
	}

	private String processSourceGeneId(String geneId) {
		if(geneId.startsWith(SOURCE_GENOME_ID))
		{
			String targetGeneId = source2targetGeneIdsMap.get(geneId);
			if(targetGeneId != null)
			{
				geneId = targetGeneId;
			}
		}
		return geneId;
	}
	

	private void processResult(Object result) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		if(result instanceof Network)
		{
			processTargetNetwork((Network) result);
		}
		else if(result instanceof List<?>)
		{
			processTargetList((List<?>) result);
		}
		
	}

	private void processTargetList(List<?> list) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		for(Object obj: list)
		{
			if(obj instanceof Dataset)
			{
				Dataset dataset = (Dataset) obj;
				for(Taxon taxon: dataset.getTaxons())
				{
					processTaxon(taxon, TARGET_GENOME_ID, SOURCE_GENOME_ID);
				}
			}
		}
	}

	private void processTargetNetwork(Network network) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		for(Node node: network.getGraph().getVertices())
		{
			Entity entity = node.getEntity();
			String sourceGeneId;
			
			//Process entity id
			sourceGeneId = target2sourceGeneIdsMap.get(entity.getId());
			if( sourceGeneId != null)
			{
			    Class<?> c = entity.getClass();
			    Field fId = c.getDeclaredField("id");
			    fId.set(entity, sourceGeneId);
			}
			
			//Process node name
			sourceGeneId = target2sourceGeneIdsMap.get(node.getName());
			if( sourceGeneId != null)
			{
			    Class<?> c = node.getClass();
			    Field fName = c.getDeclaredField("name");
			    fName.set(node, sourceGeneId);
			}
			
			
		}
	}
	
}
