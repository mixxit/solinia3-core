package com.solinia.solinia.Managers;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerManager implements IPlayerManager {
	private IRepository<ISoliniaPlayer> repository;
	
	public PlayerManager(IRepository<ISoliniaPlayer> context)
	{
		this.repository = context;
	}
	
	@Override
	public ISoliniaPlayer getPlayer(Player player) {
		if (repository.query(p ->p.getUUID().equals(player.getUniqueId())).size() == 0)
			repository.add(SoliniaPlayerFactory.CreatePlayer(player));
		
		return repository.query(p ->p.getUUID().equals(player.getUniqueId())).get(0);
	}
	
	@Override
	public boolean IsNewNameValid(String forename, String lastname)
	{
		boolean isForeNameValid = forename.chars().allMatch(Character::isLetter);
		boolean isLastNameValid = lastname.chars().allMatch(Character::isLetter);
		
		if (!isForeNameValid)
		{
			System.out.println("Invalid forename");
			return false;
		}
		
		if (!isLastNameValid && !lastname.equals(""))
		{
			System.out.println("Invalid lastname");
			return false;
		}
		
		String newname = forename;
		if (!lastname.equals(""))
			newname = forename + "_" + lastname;
		
		final String nametest = newname;
		
		if (forename.length() < 3)
		{
			System.out.println("Invalid forename length");
			return false;
		}
		
		if (nametest.length() < 3 || nametest.length() > 15)
		{
			System.out.println("Invalid total length");
			return false;
		}
		
		if (repository.query(p ->p.getFullName().toUpperCase().equals(nametest.toUpperCase())).size() != 0)
		{
			System.out.println("Invalid name, already in use");
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
}
