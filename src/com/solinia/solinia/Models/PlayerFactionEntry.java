package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;

public class PlayerFactionEntry {
	private int factionId;
	private int value;
	
	public int getFactionId() {
		return factionId;
	}
	public void setFactionId(int factionId) {
		this.factionId = factionId;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public ISoliniaFaction getFaction()
	{
		try
		{
			return StateManager.getInstance().getConfigurationManager().getFaction(getFactionId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
}
