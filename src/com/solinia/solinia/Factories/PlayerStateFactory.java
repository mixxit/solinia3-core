package com.solinia.solinia.Factories;

import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.PlayerStateCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerState;

public class PlayerStateFactory {
	public static PlayerState Create(UUID playerId, int characterId) throws CoreStateInitException, PlayerStateCreationException {
		//Player bukkitPlayer = Bukkit.getPlayer(playerId);
		//if (bukkitPlayer == null)
		//	throw new PlayerStateCreationException("Player entity does not exist");
		
		if (StateManager.getInstance().getConfigurationManager().getPlayerState(playerId) != null)
			throw new PlayerStateCreationException("PlayerState already created");
		
		PlayerState entry = new PlayerState();
		entry.setId(playerId);
		entry.setCharacterId(characterId);
		return StateManager.getInstance().getConfigurationManager().addPlayerState(entry);
	}
}
