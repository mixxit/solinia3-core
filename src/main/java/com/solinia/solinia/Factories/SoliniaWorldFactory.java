package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaWorldCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaWorld;

public class SoliniaWorldFactory {
	public static SoliniaWorld Create(String name) throws CoreStateInitException, SoliniaWorldCreationException {
		if (!StateManager.getInstance().getConfigurationManager().isWorldNameFree(name.toUpperCase()))
			throw new SoliniaWorldCreationException("World already exists");
		
		SoliniaWorld entry = new SoliniaWorld();
		entry.setId(StateManager.getInstance().getConfigurationManager().getNextWorldId());
		entry.setName(name.toUpperCase());
		
		StateManager.getInstance().getConfigurationManager().addWorld(entry);
		return entry;
	}
}
