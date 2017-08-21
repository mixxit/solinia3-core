package com.solinia.solinia.Interfaces;

import org.bukkit.entity.Player;

public interface IPlayerManager {
	public ISoliniaPlayer getPlayer(Player player);
	public void commit() throws Exception;
	public void setRepository(IPlayerRepository repository);
	public void loadFromRepository() throws Exception;
	public int getCachedPlayersCount();
}
