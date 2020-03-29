package com.solinia.solinia.Adapters;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaWorld;

public class SoliniaWorldAdapter {
	public static SoliniaWorld Adapt(String worldName) throws CoreStateInitException
	{
		return StateManager.getInstance().getConfigurationManager().getWorld(worldName);
	}
}
