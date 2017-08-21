package com.solinia.solinia.Managers;

public class CoreState {
	private PlayerManager _playerManager;
	
	public CoreState()
	{
		_playerManager = new PlayerManager();
	}
	
	public PlayerManager getPlayerManager()
	{
		return _playerManager;
	}
}
