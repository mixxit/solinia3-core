package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.CoreStateInitException;

public interface IPlayerManager {
	public ISoliniaPlayer getPlayer(Player player);
	public int getCachedPlayersCount();
	public void commit();
	boolean IsNewNameValid(String forename, String lastname);
	void resetPlayer(Player player) throws CoreStateInitException;
	void addPlayer(ISoliniaPlayer player);
	public void setApplyingAugmentation(UUID playerUuid, int itemId);
	Integer getApplyingAugmentation(UUID playerUuid);
	Integer getPlayerActiveBardSong(UUID playerUuid);
	void setPlayerActiveBardSong(UUID playerUuid, Integer spellId);
	List<ISoliniaPlayer> getPlayers();
	List<ISoliniaPlayer> getTopLevelPlayers(String classname);
	public String getPlayerNameByUUID(UUID king);
	ISoliniaPlayer getMainCharacterDataOnly(UUID playerUUID);
	public List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID) throws CoreStateInitException;
	public ISoliniaPlayer createNewPlayerAlt(Plugin plugin, Player player);
	ISoliniaPlayer loadPlayerAlt(Plugin plugin, Player player, UUID characterUUID);
	Timestamp getPlayerLastChangeChar(UUID playerUUID);
	void setPlayerLastChangeChar(UUID playerUUID, Timestamp timestamp);
	public List<ISoliniaPlayer> getCharacters() throws CoreStateInitException;
	ISoliniaPlayer getPlayerAndDoNotCreate(UUID playerUUID);
	Timestamp getPlayerLastSteed(UUID playerUUID);
	void setPlayerLastSteed(UUID playerUUID, Timestamp timestamp);
	IRepository<ISoliniaPlayer> getPlayersRepository();
}
