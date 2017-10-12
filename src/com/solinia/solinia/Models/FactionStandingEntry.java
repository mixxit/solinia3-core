package com.solinia.solinia.Models;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;

public class FactionStandingEntry {
	private int factionId;
	private int Value;
	public int getFactionId() {
		return factionId;
	}
	public void setFactionId(int factionId) {
		this.factionId = factionId;
	}
	public int getValue() {
		return Value;
	}
	public void setValue(int value) {
		Value = value;
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
