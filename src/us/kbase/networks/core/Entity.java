package us.kbase.networks.core;

public final class Entity {
	
	protected String id;
	protected EntityType type;
	
	public Entity(String id, EntityType type) {
		super();
		this.id = id;
		this.type = type;
	}

	@Override
	public boolean equals(Object entity)
	{
		if(entity instanceof Entity)
		{
			return ((Entity) entity).getId().equals(id);
		}
		return false;
	}	
	
	public String getId()
	{
		return id;
	}
	
	public EntityType getType()
	{
		return type;
	}	
}
