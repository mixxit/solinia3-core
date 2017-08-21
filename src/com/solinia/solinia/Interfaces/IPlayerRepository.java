package com.solinia.solinia.Interfaces;

import java.util.List;

public interface IPlayerRepository {

	public void setPlayers(List<ISoliniaPlayer> players);

	public List<ISoliniaPlayer> getPlayers();
}
