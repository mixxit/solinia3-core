package com.solinia.solinia.Adapters;

import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaLivingEntityAdapter {

	public static ISoliniaLivingEntity Adapt(LivingEntity entity) throws CoreStateInitException {
		return StateManager.getInstance().getEntityManager().getLivingEntity(entity);
	}
}
