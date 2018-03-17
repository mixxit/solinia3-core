package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaFaction;

public class SoliniaFactionFactory {

	public static ISoliniaFaction CreateFaction(String factionname, int base, boolean operatorCreated) throws CoreStateInitException {
		SoliniaFaction faction = new SoliniaFaction();
		faction.setId(StateManager.getInstance().getConfigurationManager().getNextFactionId());
		faction.setName(factionname);
		faction.setBase(base);
		faction.setOperatorCreated(operatorCreated);
		return StateManager.getInstance().getConfigurationManager().addFaction(faction);
	}
}
