package com.solinia.solinia.Managers;

import java.sql.Timestamp;
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
		repository.update(SoliniaPlayerFactory.CreatePlayer(player));
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
		SoliniaPlayerAdapter.Adapt(player).updateMaxHp();
	}
	
	@Override
	public void addPlayer(ISoliniaPlayer player)
	{
		repository.add(player);
	}
	
	@Override
	public ISoliniaPlayer getPlayer(Player player) {
		try
		{
			if (repository.query(p ->p.getUUID().equals(player.getUniqueId())).size() == 0)
				SoliniaPlayerFactory.CreatePlayer(player);
			
			return repository.query(p ->p.getUUID().equals(player.getUniqueId())).get(0);
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public List<ISoliniaPlayer> getPlayers() {
		return repository.query(p ->p.getUUID() != null);
	}
	
	@Override
	public List<ISoliniaPlayer> getTopLevelPlayers() {
		List<ISoliniaPlayer> playerList = getPlayers();
		Collections.sort(playerList,(o1, o2) -> o1.getExperience().compareTo(o2.getExperience()));
		Collections.reverse(playerList);
		int to = 5;
		if (playerList.size() < 5)
			to = playerList.size();
		return playerList.subList(0, 4);
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
	
	
}
