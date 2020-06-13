package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaDisguiseCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaDisguise;

public class SoliniaDisguiseFactory {
	public static SoliniaDisguise Create(String disguiseName, String disguiseType) throws CoreStateInitException, SoliniaDisguiseCreationException {
		if (StateManager.getInstance().getConfigurationManager().getDisguise(disguiseName.toUpperCase()) != null)
			throw new SoliniaDisguiseCreationException("Disguise already exists: " + StateManager.getInstance().getConfigurationManager().getDisguise(disguiseName.toUpperCase()).getDisguiseName());
		
		SoliniaDisguise d = new SoliniaDisguise();
		d.setId(StateManager.getInstance().getConfigurationManager().getNextDisguiseId());
		d.setDisguiseName(disguiseName.toUpperCase());
		d.setDisguiseType(disguiseType.toUpperCase());
		StateManager.getInstance().getConfigurationManager().addDisguise(d);
		return d;
	}
}
