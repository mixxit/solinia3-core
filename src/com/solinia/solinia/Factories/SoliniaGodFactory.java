package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaGod;

public class SoliniaGodFactory {
	public static ISoliniaGod CreateGod(String name) throws CoreStateInitException {
		SoliniaGod entry = new SoliniaGod();
		entry.setId(StateManager.getInstance().getConfigurationManager().getNextGodId());
		entry.setName(name);
		return StateManager.getInstance().getConfigurationManager().addGod(entry);
	}
}
