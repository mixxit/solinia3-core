package com.solinia.solinia.Managers;

import com.solina.solinia.Interfaces.IPlayerManager;
import com.solina.solinia.Interfaces.ObjectStreamPlayerRepository;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Timers.StateCommitTimer;

public class CoreState {
	private boolean _isInitialised = false;
	private IPlayerManager _playerManager;
	
	public CoreState()
	{
		_isInitialised = false;
	}
	
	public void Initialise(IPlayerManager playerManager) throws CoreStateInitException
	{
		if (_isInitialised == true)
			throw new CoreStateInitException("State already initialised");
		
		_playerManager = playerManager;
		_playerManager.setRepository(new ObjectStreamPlayerRepository());
		
		_isInitialised = true;
	}
	
	public IPlayerManager getPlayerManager() throws CoreStateInitException
	{
		if (_isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return _playerManager;
	}

	public void Commit() throws CoreStateInitException {
		if (_isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		System.out.println("Commit");
		try {
			_playerManager.Commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
