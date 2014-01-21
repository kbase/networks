package us.kbase.kbasenetworks.core;

import java.util.ArrayList;
import java.util.List;

public final class Entity {
	
	private String id;
	private EntityType type;
	
	
	public static List<String> toEntityIds(List<Entity> entities)
	{
		List<String> ids = new ArrayList<String>();
		for(Entity entity: entities)
		{
			ids.add(entity.getId());
		}
		return ids;
	}
	
	public static List<Entity> toEntities(String ... entityIds) {
		List<Entity> entities = new ArrayList<Entity>();
		for(String entityId : entityIds)
		{
			entities.add( toEntity(entityId) );
		}
		return entities;
	}
	
	public static List<Entity> toEntities(List<String> entityIds) {
		return toEntities((String[]) entityIds.toArray(new String[0]));
	}			

	public static Entity toEntity(String entityId) {
		return new Entity(entityId, EntityType.detect(entityId));
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
