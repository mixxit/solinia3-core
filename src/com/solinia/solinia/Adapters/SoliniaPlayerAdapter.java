package com.solinia.solinia.Adapters;

import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaPlayerAdapter {

	public static ISoliniaPlayer Adapt(Player player) throws CoreStateInitException {
		return StateManager.getInstance().getPlayerManager().getPlayer(player);
	}
	
}
