package com.solinia.solinia.Factories;

import org.bukkit.Location;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaZoneCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class SoliniaZoneFactory {
	public static SoliniaZone Create(String zonename, int x, int y, int z, boolean operatorCreated) throws CoreStateInitException, SoliniaZoneCreationException {
		if (StateManager.getInstance().getConfigurationManager().getZone(zonename.toUpperCase()) != null)
			throw new SoliniaZoneCreationException("Zone already exists");
		
		SoliniaZone zone = new SoliniaZone();
		zone.setId(StateManager.getInstance().getConfigurationManager().getNextZoneId());
		zone.setName(zonename.toUpperCase());
		zone.setX(x);
		zone.setY(y);
		zone.setZ(z);
		zone.setOperatorCreated(operatorCreated);
		
		StateManager.getInstance().getConfigurationManager().addZone(zone);
		return zone;
	}
}
