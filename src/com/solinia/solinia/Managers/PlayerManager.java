package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerManager implements IPlayerManager {
	private IRepository<ISoliniaPlayer> repository;
	private ConcurrentHashMap<UUID, Integer> playerApplyAugmentation = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Integer> playerActiveBardSong = new ConcurrentHashMap<UUID, Integer>();
	public PlayerManager(IRepository<ISoliniaPlayer> context)
	{
		this.repository = context;
	}
	
	@Override
	public void resetPlayer(Player player) throws CoreStateInitException
	{
		ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		repository.update(SoliniaPlayerFactory.CreatePlayer(player, solplayer.isMain()));
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
		SoliniaPlayerAdapter.Adapt(player).updateMaxHp();
	}
	
	@Override
	public void addPlayer(ISoliniaPlayer player)
	{
		repository.add(player);
	}
	
	private void updatePlayer(Player player, ISoliniaPlayer solPlayer) throws CoreStateInitException {
		repository.update(solPlayer);
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
		SoliniaPlayerAdapter.Adapt(player).updateMaxHp();
	}
	
	@Override
	public ISoliniaPlayer getPlayer(Player player) {
		try
		{
			if (repository.query(p ->p.getUUID().equals(player.getUniqueId())).size() == 0)
				SoliniaPlayerFactory.CreatePlayer(player,true);
			
			return repository.query(p ->p.getUUID().equals(player.getUniqueId())).get(0);
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public ISoliniaPlayer getPlayerDataOnly(UUID uniqueId) {
		if (repository.query(p ->p.getUUID().equals(uniqueId)).size() == 0)
			return null;
		
		return repository.query(p ->p.getUUID().equals(uniqueId)).get(0);
	}
	
	@Override
	public List<ISoliniaPlayer> getPlayers() {
		return repository.query(p ->p.getUUID() != null);
	}
	
	
	private List<ISoliniaPlayer> getPlayersByClass(String classname) {
		int classid = 0;
		try
		{
			for (ISoliniaClass classobj : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				if (classobj.getName().toUpperCase().equals(classname.toUpperCase()))
				{
					classid = classobj.getId();
					break;
				}
			}
		} catch (CoreStateInitException e)
		{
			return new ArrayList<ISoliniaPlayer>();
		}
		
		if (classid > 0)
		{
			final int filterclass = classid;
			return repository.query(p ->p.getUUID() != null && p.getClassId() == filterclass);
		}
		
		return new ArrayList<ISoliniaPlayer>();
		
	}

	@Override
	public List<ISoliniaPlayer> getTopLevelPlayers(String classname) {
		List<ISoliniaPlayer> playerList = new ArrayList<ISoliniaPlayer>();
		if (!classname.equals(""))
		{
			playerList = getPlayersByClass(classname);
		} else {
			playerList = getPlayers();
		}
		Collections.sort(playerList,(o1, o2) -> o1.getExperience().compareTo(o2.getExperience()));
		Collections.reverse(playerList);
		int to = 5;
		if (playerList.size() < 5)
			to = playerList.size();
		
		return playerList.subList(0, to);
	}

	@Override
	public boolean IsNewNameValid(String forename, String lastname)
	{
		boolean isForeNameValid = forename.chars().allMatch(Character::isLetter);
		boolean isLastNameValid = lastname.chars().allMatch(Character::isLetter);
		
		if (!isForeNameValid)
		{
			return false;
		}
		
		if (!isLastNameValid && !lastname.equals(""))
		{
			return false;
		}
		
		String newname = forename;
		if (!lastname.equals(""))
			newname = forename + "_" + lastname;
		
		final String nametest = newname;
		
		if (forename.length() < 3)
		{
			return false;
		}
		
		if (nametest.length() < 3 || nametest.length() > 15)
		{
			return false;
		}
		
		if (repository.query(p ->p.getFullName().toUpperCase().equals(nametest.toUpperCase())).size() != 0)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public int getCachedPlayersCount()
	{
		return repository.query(p ->p.getUUID() != null).size();
	}

	@Override
	public void commit() {
		this.repository.commit();
	}

	@Override
	public void setApplyingAugmentation(UUID playerUuid, int itemId) {
		this.playerApplyAugmentation.put(playerUuid, itemId);
	}
	
	@Override
	public Integer getApplyingAugmentation(UUID playerUuid) {
		return this.playerApplyAugmentation.get(playerUuid);
	}

	@Override
	public Integer getPlayerActiveBardSong(UUID playerUuid) {
		return playerActiveBardSong.get(playerUuid);
	}

	@Override
	public void setPlayerActiveBardSong(UUID playerUuid, Integer spellId) {
		this.playerActiveBardSong.put(playerUuid, spellId);
	}

	@Override
	public String getPlayerNameByUUID(UUID king) {
		List<ISoliniaPlayer> players = repository.query(p ->p.getUUID().equals(king));
		for(ISoliniaPlayer player : players)
		{
			return player.getFullName();
		}
		
		return null;
	}

	@Override
	public List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID) throws CoreStateInitException {
		return StateManager.getInstance().getConfigurationManager().getCharactersByPlayerUUID(playerUUID);
	}

	@Override
	public ISoliniaPlayer createNewPlayerAlt(Player player) {
		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt(player);
			StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(solPlayer);
			solPlayer = SoliniaPlayerFactory.CreatePlayer(player,false);
			
			return solPlayer;
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public ISoliniaPlayer loadPlayerAlt(Player player, UUID characterUUID) {
		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt(player);
			
			// if its the same, why bother?
			if (solPlayer.getCharacterId().equals(characterUUID))
				return solPlayer;
			
			ISoliniaPlayer altSolPlayer = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(characterUUID);
			if (altSolPlayer == null)
				return null;
			
			if (!altSolPlayer.getUUID().equals(player.getUniqueId()))
				return null;
			
			// commit current player
			StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(solPlayer);
			
			// Now clear the player and load the old one
			updatePlayer(player, altSolPlayer);
			
			return altSolPlayer;
		} catch (CoreStateInitException e) {
			return null;
		}
	}
}
