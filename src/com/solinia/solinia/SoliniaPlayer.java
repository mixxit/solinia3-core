package com.solinia.solinia;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.solina.solinia.Interfaces.ISoliniaPlayer;

public class SoliniaPlayer implements ISoliniaPlayer {

	private UUID _uuid;
	
	public SoliniaPlayer(Player player) {
		// TODO Auto-generated constructor stub
		_uuid = player.getUniqueId();
	}

	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return _uuid;
	}

}
