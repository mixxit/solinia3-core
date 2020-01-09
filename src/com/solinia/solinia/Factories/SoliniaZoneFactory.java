package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaZoneCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class SoliniaZoneFactory {
	public static SoliniaZone Create(String zonename,String world, int x, int y, int z, int succorx, int succory, int succorz) throws CoreStateInitException, SoliniaZoneCreationException {
		if (StateManager.getInstance().getConfigurationManager().getZone(zonename.toUpperCase()) != null)
			throw new SoliniaZoneCreationException("Zone already exists");
		
		SoliniaZone zone = new SoliniaZone();
		zone.setId(StateManager.getInstance().getConfigurationManager().getNextZoneId());
		zone.setName(zonename.toUpperCase());
		zone.setWorld(world);
		zone.setX(x);
		zone.setY(y);
		zone.setZ(z);
		zone.setSuccorx(succorx);
		zone.setSuccory(succory);
		zone.setSuccorz(succorz);
		
		StateManager.getInstance().getConfigurationManager().addZone(zone);
		return zone;
	}
}
