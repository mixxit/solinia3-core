package com.solinia.solinia.Managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.INPCEntityProvider;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaEntitySpellEffects;
import com.solinia.solinia.Models.SoliniaLivingEntity;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Utils.Utils;

public class EntityManager implements IEntityManager {
	INPCEntityProvider npcEntityProvider;
	private ConcurrentHashMap<UUID, SoliniaEntitySpellEffects> entitySpellEffects = new ConcurrentHashMap<UUID, SoliniaEntitySpellEffects>();

	public EntityManager(INPCEntityProvider npcEntityProvider) {
		this.npcEntityProvider = npcEntityProvider;
	}

	@Override
	public ISoliniaLivingEntity getLivingEntity(LivingEntity livingentity)
	{
		return new SoliniaLivingEntity(livingentity);
	}
	
	@Override
	public INPCEntityProvider getNPCEntityProvider()
	{
		return npcEntityProvider;
	}

	@Override
	public boolean addActiveEntityEffect(LivingEntity targetentity, SoliniaSpell soliniaSpell, Player player) {
		
		if (entitySpellEffects.get(targetentity.getUniqueId()) == null)
			entitySpellEffects.put(targetentity.getUniqueId(), new SoliniaEntitySpellEffects(targetentity));
		
		int duration = Utils.getDurationFromSpell(soliniaSpell);
		System.out.println("addActiveEntityEffect: " + soliniaSpell.getName() + " " + " to " + targetentity.getUniqueId() + " for duration " + duration);
		
		return entitySpellEffects.get(targetentity.getUniqueId()).addSpellEffect(soliniaSpell, player, duration);
	}

	@Override
	public void spellTick() {
		for(SoliniaEntitySpellEffects entityEffects : entitySpellEffects.values())
		{
			entityEffects.run();
		}
	}

}
