package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Models.DebuggerSettings;

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
	public List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID) throws CoreStateInitException;
	public ISoliniaPlayer createNewPlayerAlt(Plugin plugin, Player player, boolean includeChangeTimerLimit);
	ISoliniaPlayer loadPlayerAlt(Plugin plugin, Player player, UUID characterUUID);
	Timestamp getPlayerLastChangeChar(UUID playerUUID);
	Timestamp getPlayerLastUnstuck(UUID playerUUID);
	void setPlayerLastChangeChar(UUID playerUUID, Timestamp timestamp);
	public List<ISoliniaPlayer> getCharacters() throws CoreStateInitException;
	ISoliniaPlayer getPlayerAndDoNotCreate(UUID playerUUID);
	Timestamp getPlayerLastSteed(UUID playerUUID);
	void setPlayerLastSteed(UUID playerUUID, Timestamp timestamp);
	IRepository<ISoliniaPlayer> getPlayersRepository();
	public void grantPlayerAttendenceBonus();
	public void resetPersonality(Player player) throws CoreStateInitException;
	public List<ISoliniaPlayer> getTopVotingPlayers();
	public void toggleDebugger(UUID uniqueId, String classToDebug, String methodToDebug, String focusId);
	ConcurrentHashMap<UUID, DebuggerSettings> getDebugger();
	public void checkPlayerModVersions();
	String playerModVersion(Player player);
	public void setPlayerVersion(UUID uniqueId, String string);
	void checkPlayerModVersion(Player player);
	void setPlayerLastUnstuck(UUID playerUUID, Timestamp timestamp);
}
