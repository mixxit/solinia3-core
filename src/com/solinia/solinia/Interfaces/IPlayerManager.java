package com.solinia.solinia.Interfaces;

import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;

public interface IPlayerManager {
	public ISoliniaPlayer getPlayer(Player player);
	public int getCachedPlayersCount();
	public void commit();
	boolean IsNewNameValid(String forename, String lastname);
	void resetPlayer(Player player) throws CoreStateInitException;
	void addPlayer(ISoliniaPlayer player);
}
