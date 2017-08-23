package com.solinia.solinia.Interfaces;

import org.bukkit.entity.Player;

public interface IPlayerManager {
	public ISoliniaPlayer getPlayer(Player player);
	public int getCachedPlayersCount();
	public void commit();
}
