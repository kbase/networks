package us.kbase.networks.core;

import java.util.ArrayList;
import java.util.List;

public final class Entity {
	
	protected String id;
	protected EntityType type;
	
	public static List<Entity> toEntities(List<String> ids, EntityType type)
	{
		List<Entity> entities = new ArrayList<Entity>();
		for(String id : ids)
		{
			entities.add(new Entity(id, type));
		}
		return entities;
	}
	
	public static List<String> toEntityIds(List<Entity> entities)
	{
		List<String> ids = new ArrayList<String>();
		for(Entity entity: entities)
		{
			ids.add(entity.getId());
		}
		return ids;
	}
	
	public static String toIdsString(List<Entity> entities, String separator)
	{
		StringBuffer sb = new StringBuffer();
		for(Entity entity: entities)
		{
			if(sb.length() > 0)
			{
				sb.append(separator);
			}
			sb.append(entity.getId());
		}
		return sb.toString();
	}
	
	public Entity(String id, EntityType type) {
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
