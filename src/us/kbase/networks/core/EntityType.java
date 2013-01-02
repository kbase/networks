package us.kbase.networks.core;

public enum EntityType {
	
	GENE(new String[]{".CDS.", ".peg."}), 
	PROTEIN(new String[]{}), 
	OPERON(new String[]{}), 
	PPI_COMPLEX(new String[]{"|ppi."}), 
	GENOME(new String[]{}), 
	REGULON(new String[]{".regulon."}), 
	REGULOME(new String[]{".regulome."}), 
	SUBSYSTEM(new String[]{"|subsystem."}),
	
	
	CLUSTER(new String[]{"|cluster."}), 
	BICLUSTER(new String[]{"|bicluster."}), 
	UNKNOWN(new String[]{});

	String[] entityIdPrefixes;
	
	private EntityType(String[] entityIdPrefixes)
	{
		this.entityIdPrefixes = entityIdPrefixes;
	}
	
	
	public static EntityType detect(String entityId) {
		
		for(EntityType type: values())
		{
			for(String prefix: type.entityIdPrefixes)
			{
				if(entityId.contains(prefix))
				{
					return type;
				}
			}			
		}
		if(entityId.matches("^A[Tt]\\d[Gg]\\d+$")) { // Arabidopsis thaliana
			return GENE;
		}
		else if (entityId.matches("^A[Tt]\\d[Gg]\\d+\\.\\d+$")){
			return PROTEIN;
			
		} else if (entityId.matches("^POPTR_\\d+[Ss]\\d+$")) { // POP TR
			return GENE;
		} else if (entityId.matches("^POPTR_\\d+[Ss]\\d+\\.\\d+$")) {
			return PROTEIN;
		}
		
		
		return GENE;
	}
}
