package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPC;

public class SoliniaNPCFactory {

	public static ISoliniaNPC CreateNPC(String name, int level, int factionid) throws CoreStateInitException {
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setLevel(level);
		npc.setFactionid(factionid);
		return StateManager.getInstance().getConfigurationManager().addNPC(npc);
	}

}
