package com.solina.solinia.Interfaces;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface IPlayerRepository {

	void setPlayers(ConcurrentHashMap<UUID, ISoliniaPlayer> players);
}
