package us.kbase.networks.core;

public final class Entity {
	
	protected String id;
	
	public Entity(String id) {
		super();
		this.id = id;
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
		//TODO
		return null;
	}	
}
