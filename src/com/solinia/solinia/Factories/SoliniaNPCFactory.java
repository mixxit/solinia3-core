package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaNPC;

public class SoliniaNPCFactory {

	public static void CreateNPC(String name, int level, int factionid) throws CoreStateInitException {
		SoliniaNPC npc = new SoliniaNPC();
		npc.setId(StateManager.getInstance().getConfigurationManager().getNextNPCId());
		npc.setName(name);
		npc.setLevel(level);
		npc.setFactionid(factionid);
		StateManager.getInstance().getConfigurationManager().addNPC(npc);
	}

}
