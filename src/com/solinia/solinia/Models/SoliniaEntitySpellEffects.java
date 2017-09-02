package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SoliniaEntitySpellEffects {


	private UUID livingEntityUUID;
	private ConcurrentHashMap<Integer, SoliniaActiveSpellEffect> activeSpells = new ConcurrentHashMap<Integer,SoliniaActiveSpellEffect>();
	private boolean isPlayer;
	
	public SoliniaEntitySpellEffects(LivingEntity livingEntity) {
		setLivingEntityUUID(livingEntity.getUniqueId());
		if (livingEntity instanceof Player)
			isPlayer = true;
	}

	public UUID getLivingEntityUUID() {
		return livingEntityUUID;
	}

	public void setLivingEntityUUID(UUID livingEntityUUID) {
		this.livingEntityUUID = livingEntityUUID;
	}

	public Collection<SoliniaActiveSpellEffect> getActiveSpell() {
		return activeSpells.values();
	}

	public boolean addSpellEffect(SoliniaSpell soliniaSpell, Player player, int duration) {
		// This spell ID is already active
		System.out.println("addSpellEffect is checking if the spell is already in place on the entity");
		if (activeSpells.get(soliniaSpell.getId()) != null)
			return false;
		System.out.println("Does not have spell active... adding");
		
		SoliniaActiveSpellEffect activeEffect = new SoliniaActiveSpellEffect(getLivingEntityUUID(), soliniaSpell.getId(), isPlayer, player.getUniqueId(), duration);
		if (duration > 0)
			activeSpells.put(soliniaSpell.getId(),activeEffect);
		System.out.println("Applying effect");
		activeEffect.apply();
		return true;
	}

	public void run() {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpellEffect> updateSpells = new ArrayList<SoliniaActiveSpellEffect>();
		
		for(SoliniaActiveSpellEffect activeSpellEffect : getActiveSpell())
		{
			if (activeSpellEffect.getTicksLeft() == 0)
			{
				removeSpells.add(activeSpellEffect.getSpellId());
			}
			else
			{
				activeSpellEffect.apply();
				activeSpellEffect.setTicksLeft(activeSpellEffect.getTicksLeft() - 1);
				updateSpells.add(activeSpellEffect.getSpellId(), activeSpellEffect);
			}
		}
		
		for(Integer spellId : removeSpells)
		{
			activeSpells.remove(spellId);
		}
		
		for(SoliniaActiveSpellEffect effect : updateSpells)
		{
			activeSpells.put(effect.getSpellId(), effect);
		}
	}
}
