package com.solinia.solinia.Models;

import org.bukkit.entity.LivingEntity;

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
	
	public int getValueWithEffectsOnEntity(LivingEntity livingEntity, LivingEntity sourceFactionSpellEntity) {
		int total = value;
		try
		{
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(livingEntity);
			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				if (!activeSpell.getSourceUuid().toString().equals(sourceFactionSpellEntity.getUniqueId().toString()))
					continue;
				
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.AddFaction)))
						continue;
	
					total += effect.getCalculatedValue();
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return total;
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
