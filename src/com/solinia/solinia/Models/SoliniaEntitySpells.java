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

public class SoliniaEntitySpells {


	private UUID livingEntityUUID;
	private ConcurrentHashMap<Integer, SoliniaActiveSpell> activeSpells = new ConcurrentHashMap<Integer,SoliniaActiveSpell>();
	private boolean isPlayer;
	
	public SoliniaEntitySpells(LivingEntity livingEntity) {
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

	public Collection<SoliniaActiveSpell> getActiveSpells() {
		return activeSpells.values();
	}

	public boolean addSpell(Plugin plugin, SoliniaSpell soliniaSpell, LivingEntity sourceEntity, int duration) {
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
		
		SoliniaActiveSpell activeSpell = new SoliniaActiveSpell(getLivingEntityUUID(), soliniaSpell.getId(), isPlayer, sourceEntity.getUniqueId(), true, duration);
		if (duration > 0)
			activeSpells.put(soliniaSpell.getId(),activeSpell);
		
		// Initial run
		activeSpell.apply(plugin);
		getLivingEntity().getLocation().getWorld().playEffect(getLivingEntity().getLocation().add(0.5,0.5,0.5), Effect.POTION_BREAK, 5);
		getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,1, 0);
		
		if (duration > 0)
		{
			if (activeSpells.get(soliniaSpell.getId()) != null)
				activeSpells.get(soliniaSpell.getId()).setFirstRun(false);
		}
		return true;
	}
	
	public void removeSpell(Integer spellId)
	{
		// Effect has worn off
		SoliniaActiveSpell activeSpell = activeSpells.get(spellId);
		
		if (activeSpell == null)
			return;
		
		boolean updateMaxHp = false;
		boolean updateDisguise = false;
		
		// Handle any effect removals needed
		for(ActiveSpellEffect effect : activeSpell.getActiveSpellEffects())
		{
			switch(effect.getSpellEffectType())
			{
				case TotalHP:
					updateMaxHp = true;
					break;
				case STA:
					updateMaxHp = true;
					break;
				case Illusion:
				case IllusionCopy:
				case IllusionOther:
				case IllusionPersistence:
				case IllusionaryTarget:
					updateDisguise = true;
				break;
			}
		}
		
		activeSpells.remove(spellId);
		
		if (updateMaxHp == true)
		{
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
		}
		
		if (updateDisguise == true)
		{
			if (getLivingEntity() != null)
				DisguiseAPI.undisguiseToAll(getLivingEntity());
		}
	}
	
	public void removeAllSpells()
	{
		List<Integer> removeSpells = new ArrayList<Integer>();
		for(SoliniaActiveSpell activeSpell : getActiveSpells())
		{
			removeSpells.add(activeSpell.getSpellId());
		}
		
		for(Integer spellId : removeSpells)
		{
			removeSpell(spellId);
		}
	}

	public void run(Plugin plugin) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpell> updateSpells = new ArrayList<SoliniaActiveSpell>();
		
		if (!getLivingEntity().isDead())
		for(SoliniaActiveSpell activeSpell : getActiveSpells())
		{
			if (activeSpell.getTicksLeft() == 0)
			{
				removeSpells.add(activeSpell.getSpellId());
			}
			else
			{
				activeSpell.apply(plugin);
				activeSpell.setTicksLeft(activeSpell.getTicksLeft() - 1);
				updateSpells.add(activeSpell);
			}
		}
		
		for(Integer spellId : removeSpells)
		{
			removeSpell(spellId);
		}
		
		for(SoliniaActiveSpell activeSpell : updateSpells)
		{
			activeSpells.put(activeSpell.getSpellId(), activeSpell);
		}
	}
	
	// Mainly used for cures
	public void removeFirstSpellOfEffectType(SpellEffectType type) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpell> updateSpells = new ArrayList<SoliniaActiveSpell>();
		
		boolean foundToRemove = false;
		for(SoliniaActiveSpell activeSpell : getActiveSpells())
		{
			if (foundToRemove == false && activeSpell.getSpell().isEffectInSpell(type))
			{
				removeSpells.add(activeSpell.getSpellId());
				foundToRemove = true;
			} else {
				updateSpells.add(activeSpell);
			}
		}
		
		for(Integer spellId : removeSpells)
		{
			removeSpell(spellId);
		}
		
		for(SoliniaActiveSpell activeSpell : updateSpells)
		{
			activeSpells.put(activeSpell.getSpellId(), activeSpell);
		}
		
	}

	public void removeAllSpellsOfId(int spellId) {
		removeSpell(spellId);
	}

	public void removeFirstSpell() {
		List<Integer> removeSpells = new ArrayList<Integer>();
		List<SoliniaActiveSpell> updateSpells = new ArrayList<SoliniaActiveSpell>();
		
		boolean foundToRemove = false;
		for(SoliniaActiveSpell activeSpell : getActiveSpells())
		{
			if (foundToRemove == false)
			{
				removeSpells.add(activeSpell.getSpellId());
				foundToRemove = true;
			} else {
				updateSpells.add(activeSpell);
			}
		}
		
		for(Integer spellId : removeSpells)
		{
			removeSpell(spellId);
		}
		
		for(SoliniaActiveSpell activeSpell : updateSpells)
		{
			activeSpells.put(activeSpell.getSpellId(), activeSpell);
		}
	}
}
