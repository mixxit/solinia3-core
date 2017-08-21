package com.solinia.solinia.Managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.solina.solinia.Interfaces.IPlayerManager;
import com.solina.solinia.Interfaces.IPlayerRepository;
import com.solina.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;

public class PlayerManager implements IPlayerManager {
	private ConcurrentHashMap<UUID, ISoliniaPlayer> _players = new ConcurrentHashMap<UUID, ISoliniaPlayer>();
	private IPlayerRepository _repository;
	
	public ISoliniaPlayer getPlayer(Player player) {
		if (!_players.contains(player.getUniqueId()))
			_players.put(player.getUniqueId(), SoliniaPlayerFactory.CreatePlayer(player));
		
		return _players.get(player.getUniqueId());
	}

	@Override
	public void setRepository(IPlayerRepository repository) {
		_repository = repository;
	}

	@Override
	public void Commit() throws Exception {
		if (_repository == null)
			throw new Exception("No commitable repository found");
		_repository.setPlayers(_players);
	}
}
