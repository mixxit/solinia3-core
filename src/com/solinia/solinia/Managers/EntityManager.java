package com.solinia.solinia.Managers;

import org.bukkit.entity.Entity;

import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.ISoliniaEntity;
import com.solinia.solinia.Models.SoliniaEntity;

public class EntityManager implements IEntityManager {
	@Override
	public ISoliniaEntity getEntity(Entity entity)
	{
		return new SoliniaEntity(entity);
	}
}
