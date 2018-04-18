package com.solinia.solinia.Models;

import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CastingSpell {
	int spellId = 0;	
	UUID livingEntityUUID;
	int itemId = 0;
	public int timeLeftMilliseconds = 0;
	
	public CastingSpell(UUID uuid, int spellId, int itemId)
	{
		try
		{
			if (spellId > 0)
			{
				timeLeftMilliseconds = StateManager.getInstance().getConfigurationManager().getSpell(spellId).getCastTime();
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		this.spellId = spellId;
		this.livingEntityUUID = uuid;
		this.itemId = itemId;
	}
	
	public UUID getLivingEntityUUID()
	{
		return this.livingEntityUUID;
	}
	
	public ISoliniaSpell getSpell() {
		
		try
		{
			if (spellId > 0)
				return StateManager.getInstance().getConfigurationManager().getSpell(spellId);
		} catch (CoreStateInitException e)
		{
			
		}
		
		return null;
	}

	public ISoliniaItem getItem() {
		try
		{
			if (itemId > 0)
				return StateManager.getInstance().getConfigurationManager().getItem(itemId);
		} catch (CoreStateInitException e)
		{
			
		}
		
		return null;
	}

}
