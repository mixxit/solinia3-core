package com.solinia.solinia.Factories;

import org.bukkit.entity.Player;

import com.solina.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.SoliniaPlayer;

public class SoliniaPlayerFactory {

	public static ISoliniaPlayer CreatePlayer(Player player) {
		// A player is different to a players entity
		SoliniaPlayer soliniaPlayer = new SoliniaPlayer();
		soliniaPlayer.setUUID(player.getUniqueId());
		soliniaPlayer.setForename(player.getName());
		soliniaPlayer.setLastname("");
		return soliniaPlayer;
	}

}
