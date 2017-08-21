package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.IPlayerRepository;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerManager implements IPlayerManager {
	private ConcurrentHashMap<UUID, ISoliniaPlayer> players = new ConcurrentHashMap<UUID, ISoliniaPlayer>();
	private IPlayerRepository repository;
	
	@Override
	public ISoliniaPlayer getPlayer(Player player) {
		if (!players.contains(player.getUniqueId()))
			players.put(player.getUniqueId(), SoliniaPlayerFactory.CreatePlayer(player));
		
		return players.get(player.getUniqueId());
	}
	
	@Override
	public int getCachedPlayersCount()
	{
		return players.size();
	}

	@Override
	public void setRepository(IPlayerRepository repository) {
		this.repository = repository;
	}

	@Override
	public void commit() throws Exception {
		if (repository == null)
			throw new Exception("No commitable repository found");
		
		List<ISoliniaPlayer> list = new ArrayList<ISoliniaPlayer>(players.values());
		repository.setPlayers(list);
	}

	@Override
	public void loadFromRepository() throws Exception {
		if (repository == null)
			throw new Exception("No commitable repository found");
		
		players.clear();
		for(ISoliniaPlayer player : repository.getPlayers())
		{
			players.put(player.getUUID(), player);
		}
		System.out.print("* Loaded " + players.size() + " player objects");
	}
}
