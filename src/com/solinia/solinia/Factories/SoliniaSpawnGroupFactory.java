package com.solinia.solinia.Factories;

import org.bukkit.Location;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaSpawnGroupCreationException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpawnGroup;

public class SoliniaSpawnGroupFactory {
	public static int Create(String spawngroupname, int npcid, Location location) throws CoreStateInitException, SoliniaSpawnGroupCreationException {
		if (StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupname.toUpperCase()) != null)
			throw new SoliniaSpawnGroupCreationException("Spawngroup already exists");
		
		ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		if (npc == null)
			throw new SoliniaSpawnGroupCreationException("NPC does not exist");
		
		SoliniaSpawnGroup sg = new SoliniaSpawnGroup();
		sg.setId(StateManager.getInstance().getConfigurationManager().getNextSpawnGroupId());
		sg.setName(spawngroupname.toUpperCase());
		sg.setNpcid(npcid);
		sg.setLocation(location);
		if (npc.isRaidboss())
			sg.setRespawntime(3600);
		else
			sg.setRespawntime(900);
		
		StateManager.getInstance().getConfigurationManager().addSpawnGroup(sg, false);
		return sg.getId();
	}
}
