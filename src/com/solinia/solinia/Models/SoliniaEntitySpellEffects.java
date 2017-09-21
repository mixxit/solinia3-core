package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.Utils;
import net.md_5.bungee.api.ChatColor;

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
	
	public LivingEntity getLivingEntity()
	{
		return (LivingEntity)Bukkit.getEntity(this.getLivingEntityUUID());
	}

	public Collection<SoliniaActiveSpellEffect> getActiveSpells() {
		return activeSpells.values();
	}

	public boolean addSpellEffect(SoliniaSpell soliniaSpell, LivingEntity sourceEntity, int duration) {
		// This spell ID is already active
		if (activeSpells.get(soliniaSpell.getId()) != null)
			return false;

		try
		{
			if(!SoliniaSpell.isValidEffectForEntity(getLivingEntity(),sourceEntity,soliniaSpell))
				return false;
		} catch (CoreStateInitException e)
		{
			return false;
		}

		// Resist spells!
		if (soliniaSpell.isResistable() && !soliniaSpell.isBeneficial())
		{
			float effectiveness = 100;
			
			// If state is active, try to use spell effectiveness (resists)
			try {
				effectiveness = soliniaSpell.getSpellEffectiveness(sourceEntity, getLivingEntity());
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (effectiveness < 100)
			{
				// Check resistances
				if (this.getLivingEntity() instanceof Player)
				{
					Player player = (Player)this.getLivingEntity();
					player.sendMessage(ChatColor.GRAY + "* You resist " + soliniaSpell.getName());
					return true;
				}
				
				if (sourceEntity instanceof Player)
				{
					Player player = (Player)sourceEntity;
					player.sendMessage(ChatColor.GRAY + "* " + soliniaSpell.getName() + " was completely resisted");
					
					/* TODO - Add hate
					if (Utils.isLivingEntityNPC((LivingEntity)getLivingEntity()))
					{
						try {
							// Resists should still cause hate
							ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
							solentity.addHate(sourceEntity,10);
						} catch (CoreStateInitException e) {
							
						}
					}
					*/
					
					return true;
				}
			}
		}
		
		SoliniaActiveSpellEffect activeEffect = new SoliniaActiveSpellEffect(getLivingEntityUUID(), soliniaSpell.getId(), isPlayer, sourceEntity.getUniqueId(), true, duration);
		if (duration > 0)
			activeSpells.put(soliniaSpell.getId(),activeEffect);
		
		// Initial run
		activeEffect.apply();
		getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 5);
		getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
		
		if (duration > 0)
			activeSpells.get(soliniaSpell.getId()).setFirstRun(false);
		return true;
	}

	public void run() {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpellEffect> updateSpells = new ArrayList<SoliniaActiveSpellEffect>();
		
		for(SoliniaActiveSpellEffect activeSpellEffect : getActiveSpells())
		{
			if (activeSpellEffect.getTicksLeft() == 0)
			{
				removeSpells.add(activeSpellEffect.getSpellId());
			}
			else
			{
				activeSpellEffect.apply();
				activeSpellEffect.setTicksLeft(activeSpellEffect.getTicksLeft() - 1);
				updateSpells.add(activeSpellEffect);
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
	
	// Mainly used for cures
	public void removeFirstSpellOfEffectType(SpellEffectType type) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpellEffect> updateSpells = new ArrayList<SoliniaActiveSpellEffect>();
		
		boolean foundToRemove = false;
		for(SoliniaActiveSpellEffect activeSpellEffect : getActiveSpells())
		{
			if (foundToRemove == false && activeSpellEffect.getSpell().isEffectInSpell(type))
			{
				removeSpells.add(activeSpellEffect.getSpellId());
				foundToRemove = true;
			} else {
				updateSpells.add(activeSpellEffect);
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
