package com.solinia.solinia.Adapters;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaPlayerAdapter {

	public static ISoliniaPlayer Adapt(Player player) throws CoreStateInitException {
		return StateManager.getInstance().getPlayerManager().getActivePlayer(player);
	}
	
	public static ISoliniaPlayer Adapt(UUID playerUUID) throws CoreStateInitException {
		return StateManager.getInstance().getPlayerManager().getActivePlayer(playerUUID);
	}
	
}
