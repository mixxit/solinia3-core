package com.solinia.solinia;

import org.bukkit.entity.Player;

public class SoliniaPlayerFactory {

	public static SoliniaPlayer CreatePlayer(Player player) {
		// A player is different to a players entity
		return new SoliniaPlayer(player);
	}

}
