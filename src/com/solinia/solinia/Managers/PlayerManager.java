package com.solinia.solinia.Managers;

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
		if (repository.query(p ->p.getUUID() == player.getUniqueId()).size() == 0)
			repository.add(SoliniaPlayerFactory.CreatePlayer(player));
		
		return repository.query(p ->p.getUUID() == player.getUniqueId()).get(0);
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
