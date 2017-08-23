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
		boolean isForeNameValid = forename.matches("[A-Za-z]");
		boolean isLastNameValid = lastname.matches("[A-Za-z]");
		
		if (!isForeNameValid)
			return false;
		
		if (!isLastNameValid && !lastname.equals(""))
			return false;
		
		String newname = forename;
		if (!lastname.equals(""))
			newname = forename + "_" + lastname;
		
		final String nametest = newname;
		
		if (nametest.length() < 7 || nametest.length() > 15)
			return false;
		
		if (repository.query(p ->p.getFullName().toUpperCase().equals(nametest.toUpperCase())).size() == 0)
			return true;
		
		return false;
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
