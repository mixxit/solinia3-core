package com.solina.solinia.Interfaces;

import org.bukkit.entity.Player;

public interface IPlayerManager {
	public ISoliniaPlayer getPlayer(Player player);
	public void Commit() throws Exception;
	void setRepository(IPlayerRepository repository);
}
