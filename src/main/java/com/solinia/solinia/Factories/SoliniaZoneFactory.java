package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaZoneCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class SoliniaZoneFactory {
	public static SoliniaZone Create(String zonename,String world, int bottomleftx, int bottomlefty, int bottomleftz,int toprightx, int toprighty, int toprightz, int succorx, int succory, int succorz) throws CoreStateInitException, SoliniaZoneCreationException {
		if (StateManager.getInstance().getConfigurationManager().getZone(zonename.toUpperCase()) != null)
			throw new SoliniaZoneCreationException("Zone already exists");
		
		SoliniaZone zone = new SoliniaZone();
		zone.setId(StateManager.getInstance().getConfigurationManager().getNextZoneId());
		zone.setName(zonename.toUpperCase());
		zone.setWorld(world);
		zone.setBottomLeftCornerX(bottomleftx);
		zone.setBottomLeftCornerY(bottomlefty);
		zone.setBottomLeftCornerZ(bottomleftz);
		zone.setTopRightCornerX(toprightx);
		zone.setTopRightCornerY(toprighty);
		zone.setTopRightCornerZ(toprightz);
		zone.setSuccorx(succorx);
		zone.setSuccory(succory);
		zone.setSuccorz(succorz);
		
		StateManager.getInstance().getConfigurationManager().addZone(zone);
		return zone;
	}
}
