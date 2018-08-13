package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CastingSpell {
	int spellId = 0;	
	UUID livingEntityUUID;
	int itemId = 0;
	public int timeLeftMilliseconds = 0;
	
	public CastingSpell(UUID uuid, int spellId, int itemId)
	{
		this.livingEntityUUID = uuid;

		try
		{
			if (spellId > 0)
			{
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellId);
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
				
				if (solLivingEntity == null)
				{
					timeLeftMilliseconds = spell.getCastTime();
				} else {
					timeLeftMilliseconds = solLivingEntity.getActSpellCasttime(spell, spell.getCastTime());
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		this.spellId = spellId;
		this.itemId = itemId;
	}
	
	private LivingEntity getLivingEntity()
	{
		return (LivingEntity)Bukkit.getEntity(livingEntityUUID);
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
