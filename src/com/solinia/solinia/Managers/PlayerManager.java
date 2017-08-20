package com.solinia.solinia.Managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.solina.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.SoliniaPlayerFactory;

public class PlayerManager {
	private ConcurrentHashMap<UUID, ISoliniaPlayer> players = new ConcurrentHashMap<UUID, ISoliniaPlayer>();
	
	public void RegisterPlayer(Player player) {
		if (!players.contains(player.getUniqueId()))
			players.put(player.getUniqueId(), SoliniaPlayerFactory.CreatePlayer(player));
	}
}
