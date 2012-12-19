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
		return GENE;
	}
}
