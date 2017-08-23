package com.solinia.solinia.Adapters;

import org.bukkit.entity.Entity;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaEntity;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaEntityAdapter {

	public static ISoliniaEntity Adapt(Entity entityuuid) throws CoreStateInitException {
		return StateManager.getInstance().getEntityManager().getEntity(entityuuid);
	}
}
