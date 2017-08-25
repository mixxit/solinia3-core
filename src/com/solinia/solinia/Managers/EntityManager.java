package com.solinia.solinia.Managers;

import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaLivingEntity;

public class EntityManager implements IEntityManager {
	@Override
	public ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity)
	{
		return new SoliniaLivingEntity(livingentity);
	}
}
