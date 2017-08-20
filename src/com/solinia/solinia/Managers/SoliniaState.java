package com.solinia.solinia.Managers;

public class SoliniaState {
	private PlayerManager _playerManager;
	
	public SoliniaState()
	{
		_playerManager = new PlayerManager();
	}
	
	public PlayerManager getPlayerManager()
	{
		return _playerManager;
	}
}
