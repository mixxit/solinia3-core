package com.solinia.solinia.Managers;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.IPlayerManager;

public class CoreState {
	private boolean isInitialised = false;
	private IPlayerManager playerManager;
	private IEntityManager entityManager;
	private IConfigurationManager configurationManager;
	
	public CoreState()
	{
		isInitialised = false;
	}
	
	public void Initialise(IPlayerManager playerManager, IEntityManager entityManager, IConfigurationManager configurationManager) throws CoreStateInitException
	{
		if (isInitialised == true)
			throw new CoreStateInitException("State already initialised");
		
		this.playerManager = playerManager;
		this.entityManager = entityManager;
		this.configurationManager = configurationManager;
		isInitialised = true;
	}
	
	public IPlayerManager getPlayerManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return playerManager;
	}
	
	public IConfigurationManager getConfigurationManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return configurationManager;
	}

	public void Commit() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		System.out.println("Commit");
		try {
			playerManager.commit();
			configurationManager.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public IEntityManager getEntityManager() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return entityManager;
	}
}
