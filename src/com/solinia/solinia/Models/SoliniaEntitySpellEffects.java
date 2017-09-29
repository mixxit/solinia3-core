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
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.Utils;

import me.libraryaddict.disguise.DisguiseAPI;
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

	public boolean addSpellEffect(Plugin plugin, SoliniaSpell soliniaSpell, LivingEntity sourceEntity, int duration) {
		// This spell ID is already active
		if (activeSpells.get(soliniaSpell.getId()) != null)
			return false;

		try
		{
			if(!SoliniaSpell.isValidEffectForEntity(getLivingEntity(),sourceEntity,soliniaSpell))
			{
				System.out.println("Spell: " + soliniaSpell.getName() + " found to have invalid target");
				return false;
			}
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
		activeEffect.apply(plugin);
		getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 5);
		getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
		
		if (duration > 0)
		{
			if (activeSpells.get(soliniaSpell.getId()) != null)
				activeSpells.get(soliniaSpell.getId()).setFirstRun(false);
		}
		return true;
	}
	
	public void removeActiveSpell(Integer spellId)
	{
		// Effect has worn off
		SoliniaActiveSpellEffect activeSpellEffect = activeSpells.get(spellId);
		
		if (activeSpellEffect == null)
			return;
		
		// Handle any effect removals needed
		for(SpellEffect effect : activeSpellEffect.getSpell().getSpellEffects())
		{
			switch(effect.getSpellEffectType())
			{
				case STA:
					if (getLivingEntity() != null && getLivingEntity() instanceof Player)
					{
						try
						{
							ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getLivingEntity());
							if (solplayer != null)
							solplayer.updateMaxHp();
						} catch (CoreStateInitException e)
						{
							
						}
					}
				case Illusion:
				case IllusionCopy:
				case IllusionOther:
				case IllusionPersistence:
				case IllusionaryTarget:
					if (getLivingEntity() != null)
						DisguiseAPI.undisguiseToAll(getLivingEntity());
				break;
			}
		}
		
		activeSpells.remove(spellId);
	}
	
	public void removeAllActiveSpells()
	{
		List<Integer> removeSpells = new ArrayList<Integer>();
		for(SoliniaActiveSpellEffect activeSpellEffect : getActiveSpells())
		{
			removeSpells.add(activeSpellEffect.getSpellId());
		}
		
		for(Integer spellId : removeSpells)
		{
			removeActiveSpell(spellId);
		}
	}

	public void run(Plugin plugin) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpellEffect> updateSpells = new ArrayList<SoliniaActiveSpellEffect>();
		
		if (!getLivingEntity().isDead())
		for(SoliniaActiveSpellEffect activeSpellEffect : getActiveSpells())
		{
			if (activeSpellEffect.getTicksLeft() == 0)
			{
				removeSpells.add(activeSpellEffect.getSpellId());
			}
			else
			{
				activeSpellEffect.apply(plugin);
				activeSpellEffect.setTicksLeft(activeSpellEffect.getTicksLeft() - 1);
				updateSpells.add(activeSpellEffect);
			}
		}
		
		for(Integer spellId : removeSpells)
		{
			removeActiveSpell(spellId);
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
			removeActiveSpell(spellId);
		}
		
		for(SoliniaActiveSpellEffect effect : updateSpells)
		{
			activeSpells.put(effect.getSpellId(), effect);
		}
		
	}

	public void removeAllActiveSpellsOfId(int spellId) {
		removeActiveSpell(spellId);
	}
}
