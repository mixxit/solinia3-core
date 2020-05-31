package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CastingSpell {
	int spellId = 0;	
	UUID livingEntityUUID;
	//int itemId = 0;
	public int timeLeftMilliseconds = 0;
	boolean useMana = true;
	boolean useReagents = true;
	boolean ignoreProfessionAndLevel = false;
	private String requiredWeaponSkillType = "";
	UUID npcTargetUUID;
	
	public CastingSpell(UUID uuid, int spellId, boolean useMana, boolean useReagents, boolean ignoreProfessionAndLevel, String requiredWeaponSkillType, UUID npcTargetUUID)
	{
		this.livingEntityUUID = uuid;
		this.requiredWeaponSkillType = requiredWeaponSkillType;

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
		this.useMana = useMana;
		this.useReagents = useReagents;
		this.ignoreProfessionAndLevel = ignoreProfessionAndLevel;
		this.npcTargetUUID = npcTargetUUID;
	}
	
	private LivingEntity getLivingEntity()
	{
		return (LivingEntity)Bukkit.getEntity(livingEntityUUID);
	}
	
	public String getRequiredWeaponSkillType()
	{
		return requiredWeaponSkillType;
	}
	
	public UUID getLivingEntityUUID()
	{
		return this.livingEntityUUID;
	}
	
	public UUID getNPCTargetUUID()
	{
		return this.npcTargetUUID;
	}
	
	public Entity getNPCTarget()
	{
		if (this.npcTargetUUID == null)
			return null;
		
		return Bukkit.getEntity(this.npcTargetUUID);
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

}
