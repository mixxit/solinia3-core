package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.DebugUtils;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.SpellUtils;
import me.libraryaddict.disguise.DisguiseAPI;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.Tuple;

public class SoliniaEntitySpells {

	private UUID livingEntityUUID;
	private ConcurrentHashMap<Short, SoliniaActiveSpell> slots = new ConcurrentHashMap<Short, SoliniaActiveSpell>();
	private boolean isPlayer;

	public SoliniaEntitySpells(LivingEntity livingEntity) {
		setLivingEntityUUID(livingEntity.getUniqueId());
		if (livingEntity instanceof Player)
			isPlayer = true;
	}
	
	private LivingEntity getBukkitLivingEntity()
	{
		Entity entity = Bukkit.getEntity(livingEntityUUID);
		if (!(entity instanceof LivingEntity))
			return null;
		
		return (LivingEntity)entity;		
	}
	
	private ISoliniaLivingEntity getSoliniaLivingEntity()
	{
		if (getBukkitLivingEntity() == null)
			return null;
		
		try
		{
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getBukkitLivingEntity());
			return solLivingEntity;
		} catch (CoreStateInitException e)
		{
			
		}
		
		return null;
	}
	
	private int getMaxTotalSlots()
	{
		ISoliniaLivingEntity solLivingEntity = getSoliniaLivingEntity();
		if (solLivingEntity == null)
			return 0;
		
		return solLivingEntity.getMaxTotalSlots();
	}

	public UUID getLivingEntityUUID() {
		return livingEntityUUID;
	}

	public void setLivingEntityUUID(UUID livingEntityUUID) {
		this.livingEntityUUID = livingEntityUUID;
	}

	public LivingEntity getLivingEntity() {
		return (LivingEntity) Bukkit.getEntity(this.getLivingEntityUUID());
	}

	public Collection<SoliniaActiveSpell> getActiveSpells() {
		return slots.values();
	}
	
	private Boolean containsSpellId(int spellId)
	{
		for (SoliniaActiveSpell activeSpell : getActiveSpells()) {
			if (activeSpell.getSpellId() == spellId)
				return true;
		}
		
		return false;
	}

	public boolean addSpell(Plugin plugin, ISoliniaSpell soliniaSpell, LivingEntity sourceEntity, int duration, boolean sendMessages, String requiredWeaponSkillType, boolean racialPassive) {
		if (sourceEntity == null)
			return false;
		
		if (this.getBukkitLivingEntity() == null)
			return false;

		try
		{
		ISoliniaLivingEntity solSourceEntity = SoliniaLivingEntityAdapter.Adapt(sourceEntity);
		if (solSourceEntity == null || solSourceEntity.getBukkitLivingEntity() == null || solSourceEntity.getBukkitLivingEntity().isDead() || this.getBukkitLivingEntity() == null || this.getBukkitLivingEntity().isDead())
			return false;
		
		DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Beginning addSpell on behalf of caster");
		DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", this.getBukkitLivingEntity(), "Beginning addSpell on behalf of source");
		// This spell ID is already active
		// TODO We should allow overwriting of higher level 
		
		// lets extend the duration if its out spell
		if (containsSpellId(soliniaSpell.getId()))
		for (SoliniaActiveSpell activeSpell : getActiveSpells()) {
			if (activeSpell.getSpellId() == soliniaSpell.getId() && activeSpell.getSourceUuid().equals(sourceEntity.getUniqueId()))
			{
				activeSpell.setTicksLeft(duration);
				DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Ticks now" + duration);
				return true;
			}
		}
		
		if (containsSpellId(soliniaSpell.getId()) && !soliniaSpell.isStackableDot())
		{
			DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Abort, had spell already and it wasnt a stackable dot");
			DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", this.getBukkitLivingEntity(), "Source spell on me was already in existence and wasnt stackable");
			return false;
		}

		if ((slots.size() + 1) > getMaxTotalSlots())
		{
			DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Abort, had no more spell slots");
			DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", this.getBukkitLivingEntity(), "Source spell on me was aborted due to no more spell slots");
			return false;
		}

		if (this.getLivingEntity() == null)
		{
			DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Target didnt exist");
			return false;
		}

		// System.out.println("Adding spell: " + soliniaSpell.getName() + " to " +
		// this.getLivingEntity().getName() + " from " + sourceEntity.getName());

		try {
			Tuple<Boolean,String> result = SoliniaSpell.isValidEffectForEntity(getLivingEntity(), sourceEntity, soliniaSpell);
			if (!result.a()) {
				//System.out.println("Spell: " + soliniaSpell.getName() + "[" + soliniaSpell.getId() + "] found to have invalid target (" + getLivingEntity().getName() + ")");
				DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Failed to apply: " + result.b());
				DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", this.getBukkitLivingEntity(), "Source spell Failed to apply: " + result.b());
				return false;
			} else {
				DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Was valid effect");
			}
		} catch (CoreStateInitException e) {
			return false;
		}
		
		List<Integer> removeSpellIds = new ArrayList<Integer>();
		for(SoliniaActiveSpell activeSpell : getActiveSpells())
		{
			// -1 no stack
			// 0 can stack
			// 1 replace 
			int stackableResponse = checkStackConflict(activeSpell.getSpell(),activeSpell.getSourceUuid(),soliniaSpell,getLivingEntity(),sourceEntity);
			if(stackableResponse == -1)
			{
				DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", sourceEntity, "Stack conflict");
				return false;
			}
			
			if (stackableResponse == 1)
			{
				removeSpellIds.add(activeSpell.getSpellId());
			}
		}
		for(Integer spellId : removeSpellIds)
		{
			try {
				StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(this.getLivingEntityUUID(), spellId, true, true);
			} catch (CoreStateInitException e) {
				return false;
			}
		}
		
		// Resist spells!
		if (soliniaSpell.isResistable() && !soliniaSpell.isBeneficial()) {
			float effectiveness = 100;

			// If state is active, try to use spell effectiveness (resists)
			try {
				ISoliniaLivingEntity solVictimEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
				effectiveness = solVictimEntity.getResistSpell(soliniaSpell, sourceEntity);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (effectiveness < 100) {
				if (this.getLivingEntity() instanceof Creature && sourceEntity instanceof LivingEntity) {
					// aggro if resisted
					Creature creature = (Creature) this.getLivingEntity();
					if (creature.getTarget() == null) {
						try {
							ISoliniaLivingEntity solCreature = SoliniaLivingEntityAdapter.Adapt(((LivingEntity)creature));
							if (solCreature != null)
							{
								solCreature.setEntityTarget((LivingEntity) sourceEntity);
								solCreature.addToHateList(sourceEntity.getUniqueId(), 1, true);
							}
						} catch (CoreStateInitException e) {

						}
					}
				}

				// Check resistances
				if (this.getLivingEntity() instanceof Player) {
					Player player = (Player) this.getLivingEntity();
					player.sendMessage(ChatColor.GRAY + "* You resist " + soliniaSpell.getName());
				}

				if (sourceEntity instanceof Player) {
					Player player = (Player) sourceEntity;
					player.sendMessage(ChatColor.GRAY + "* " + soliniaSpell.getName() + " was completely resisted");
				}

				return true;
			}
		}
		
		int rootBreakChance = 100;
		if (soliniaSpell.isRoot())
		{
			rootBreakChance = solSourceEntity.getAABonuses(SpellEffectType.RootBreakChance) + solSourceEntity.getItemBonuses(SpellEffectType.RootBreakChance) + solSourceEntity.getSpellBonuses(SpellEffectType.RootBreakChance);
		}
		
		SoliniaActiveSpell activeSpell = new SoliniaActiveSpell(getLivingEntityUUID(), soliniaSpell.getId(), isPlayer,
				sourceEntity.getUniqueId(), true, duration, soliniaSpell.getNumhits(), requiredWeaponSkillType, racialPassive, rootBreakChance);
		
		Short slot = getNextAvailableSlot();
		if (slot == null)
			return false;
		
		if (duration > 0)
		{
			putSlot(slot, activeSpell, true);
		}

		// System.out.println("Successfully queued spell: "+ soliniaSpell.getName());

		if (activeSpell.getSpell().isBardSong() && sourceEntity != null && !sourceEntity.isDead()) {
			try {
				if (solSourceEntity != null)
				{
					ChatUtils.SendHint(solSourceEntity.getBukkitLivingEntity(), HINT.STARTS_TO_SING, sourceEntity.getCustomName()+"^"+soliniaSpell.getName(), true);
					StateManager.getInstance().getEntityManager().setEntitySinging(sourceEntity.getUniqueId(), soliniaSpell.getId());
				}
			} catch (CoreStateInitException e) {
				// ignore
			}
		}

		// Initial run
		activeSpell.apply(plugin, sendMessages);
		if (!activeSpell.isRacialPassive())
			SpellUtils.playSpecialEffect(getLivingEntity(), activeSpell);
		
		try
		{
			if (!activeSpell.isRacialPassive())
				StateManager.getInstance().getEntityManager().playSpellFinishedSoundEffect(getLivingEntity(),soliniaSpell);
		//getLivingEntity().getWorld().playSound(getLivingEntity().getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 0);
		} catch (CoreStateInitException e)
		{
			
		}

		if (duration > 0) {
			if (slots.get(slot) != null)
				slots.get(slot).setFirstRun(false);
		}
		DebugUtils.DebugLog("SoliniaEntitySpells", "addSpell", this.getBukkitLivingEntity(), "Source spell was successful (duration: " + duration + ")");
		return true;
		} catch(CoreStateInitException e)
		{
			return false;
		}
	}
	
	public int checkStackConflict(ISoliniaSpell activeSpell, UUID activeSpellSourceUuid, ISoliniaSpell newSpell, LivingEntity targetEntity, LivingEntity sourceEntity)
	{
		if (!newSpell.isBuffSpell() || newSpell.getBuffduration() < 1)
			return 0;
		
		boolean effect_match = true; // Figure out if we're identical in effects on all slots.
		
		if (activeSpell.getId() != newSpell.getId())
		{
			for(SpellEffect activeEffect : activeSpell.getBaseSpellEffects())
			{
				for(SpellEffect newEffect : newSpell.getBaseSpellEffects())
				{
					// we don't want this optimization for mana burns
					if (activeEffect.getSpellEffectId() != newEffect.getSpellEffectId() || activeEffect.getSpellEffectType().equals(SpellEffectType.ManaBurn)) {
						effect_match = false;
						break;
					}
				}
			}
		} else if (activeSpell.isEffectInSpell(SpellEffectType.ManaBurn)) {
			DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " We have a Mana Burn spell that is the same, they won't stack");
			return -1;
		}
		
		DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " check begins");
		/*
		One of these is a bard song and one isn't and they're both beneficial so they should stack.
		*/
		if(newSpell.isBardSong() != activeSpell.isBardSong())
			if(!newSpell.isDetrimental() && !activeSpell.isDetrimental())
			{
				DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " are beneficial, and one is a bard song, no action needs to be taken");
				return 0;
			}
		
		boolean values_equal = false;
		boolean will_overwrite = false;
		
		for(SpellEffect activeEffect : activeSpell.getBaseSpellEffects())
		{
			for(SpellEffect newEffect : newSpell.getBaseSpellEffects())
			{
				if (newEffect.getSpellEffectType().equals(SpellEffectType.CHA) && newEffect.getBase() == 0)
					continue;
				
				// if effects are not same SPA (same effect) they stack 
				if (!newEffect.getSpellEffectType().equals(activeEffect.getSpellEffectType()))
				{
					continue;
				}
				
				// big ol' list according to the client, wasn't that nice!
				if (newSpell.isEffectIgnoredInStacking(activeEffect.getSpellEffectId()))
					continue;
				
				// negative AC affects are skipped. Ex. Sun's Corona and Glacier Breath should stack
				// There may be more SPAs we need to add here ....
				// The client does just check base rather than calculating the affect change value.
				if ((activeEffect.getSpellEffectType().equals(SpellEffectType.ArmorClass) || activeEffect.getSpellEffectType().equals(SpellEffectType.ACv2)) && newEffect.getBase() < 0)
					continue;
				
				/*
				If target is a npc and caster1 and caster2 exist
				If Caster1 isn't the same as Caster2 and the effect is a DoT then ignore it.
				*/
				if(this.isPlayer && activeSpellSourceUuid != null && targetEntity.getUniqueId() != null && !activeSpellSourceUuid.equals(targetEntity.getUniqueId())) {
					if(activeEffect.getSpellEffectType().equals(SpellEffectType.CurrentHP) && activeSpell.isDetrimental() && newSpell.isDetrimental()) {
						//Log(Logs::Detail, Logs::Spells, "Both casters exist and are not the same, the effect is a detrimental dot, moving on");
						continue;
					}
				}
				
				DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " activespell detrimental: " + activeSpell.isDetrimental());;
				DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " activespell detrimental: " + activeSpell.isDetrimental());;
				DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " newspell detrimental: " + newSpell.isDetrimental());;
				
				/*
				If the spells aren't the same
				and the effect is a dot we can go ahead and stack it
				*/
				if(activeEffect.getSpellEffectType().equals(SpellEffectType.CurrentHP) && activeSpell.getId() != newSpell.getId() && (activeSpell.isDetrimental() || newSpell.isDetrimental())) {
					DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + "The spells are not the same and it is a detrimental dot, passing");
					continue;
				}
				
				// if effects are not in the same position they stack
				if (newEffect.getSpellEffectNo() != activeEffect.getSpellEffectNo())
					continue;
				
				// Does the SPA allow stacking
				if (!newEffect.allowsStacking(activeEffect))
				{
					DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " SPA does not allow stacking");
					return 0;
				}
				
				int sp1_value = 1;
				int sp2_value = 0;

				try
				{
					if (Bukkit.getEntity(activeSpellSourceUuid) != null && Bukkit.getEntity(activeSpellSourceUuid) instanceof LivingEntity)
					{
						ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)Bukkit.getEntity(activeSpellSourceUuid));
						int instrument_mod = 0;
						if (sourceSoliniaLivingEntity != null) {
							instrument_mod = sourceSoliniaLivingEntity.getInstrumentMod(activeSpell);
						}
						
						sp1_value = activeSpell.calcSpellEffectValue(activeEffect, sourceSoliniaLivingEntity.getBukkitLivingEntity(), getLivingEntity(),
								sourceSoliniaLivingEntity.getEffectiveLevel(true), activeSpell.getNumhits(), instrument_mod);
					}
					if (sourceEntity != null)
					{
						ISoliniaLivingEntity sourceSoliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt(sourceEntity);
						
						int instrument_mod = 0;
						if (sourceSoliniaLivingEntity != null) {
							instrument_mod = sourceSoliniaLivingEntity.getInstrumentMod(newSpell);
						}
						sp2_value = newSpell.calcSpellEffectValue(newEffect, sourceEntity, getLivingEntity(),
								sourceSoliniaLivingEntity.getEffectiveLevel(true), newSpell.getNumhits(), instrument_mod);
					}
				} catch (CoreStateInitException e)
				{
					
				}
				
				if
				(
					activeEffect.getSpellEffectType().equals(SpellEffectType.AttackSpeed)||
					activeEffect.getSpellEffectType().equals(SpellEffectType.AttackSpeed2)
				)
				{
					sp1_value -= 100;
					sp2_value -= 100;
				}
				
				if(sp1_value < 0)
					sp1_value = 0 - sp1_value;
				if(sp2_value < 0)
					sp2_value = 0 - sp2_value;
				
				if(sp2_value < sp1_value) {
					DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " one spell was not as good as the other");

					return -1;	// can't stack
				}
				
				if (sp2_value != sp1_value)
				{
					DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " buff values were not equal");
					values_equal = false;
				}
				
				will_overwrite = true;
			}
		}
		
		//if we get here, then none of the values on the new spell are "worse"
		//so now we see if this new spell is any better, or if its not related at all
		if(will_overwrite) {
			if (values_equal && effect_match && !newSpell.isGroupSpell() && activeSpell.isGroupSpell()) {

				DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " appears to be the single target version of spell");

				return -1;
			}
			DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " stacking code decided that it should overwrite");
			return 1;
		}		
		DebugUtils.DebugLog("SoliniaEntitySpells", "checkStackConflictCanStack", this.getBukkitLivingEntity(), activeSpell.getName() + " and " + newSpell.getName() + " were found to stack ok");
		return 0;
	}

	@SuppressWarnings("incomplete-switch")
	public void removeSpell(Plugin plugin, Integer spellId, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		// Effect has worn off
		
		SoliniaActiveSpell activeSpell = null;
		for (SoliniaActiveSpell curSpell : getActiveSpells())
		{
			if (curSpell.getSpellId() != spellId)
				continue;
			
			activeSpell = curSpell;
			break;
		}
		
		if (activeSpell == null)
			return;

		boolean updateMaxHp = false;
		boolean updateDisguise = false;
		// CarlZalph 
		// So I looked at the github for why pets disappear when buffs applied to them wear off.  This boolean is always true, which results in the deletion of the pet for when buffs wear
		// It should be initialized to 'false' here
		// Possibly related to issue #290 ?
		boolean removeCharm = false;

		boolean resendInvisPackets = false;
		
		// Handle any effect removals needed
		for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
			switch (effect.getSpellEffectType()) {
			case SeeInvis:
				resendInvisPackets = true;
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
			case Charm:
				removeCharm = true;
				break;
			}
		}

		for (Entry<Short, SoliniaActiveSpell> slot : slots.entrySet())
		{
			if (slot.getValue().getSpellId() == spellId)
			{
				slots.remove(slot.getKey());
			}
		}
		
		try
		{
			// notify caster (or their master) of buff that it's worn off
			if (getSoliniaLivingEntity() != null && activeSpell.getSourceUuid() != null && !activeSpell.getOwnerUuid().equals(activeSpell.getSourceUuid()) && !activeSpell.getSpell().isBardSong())
			{
				Entity p = Bukkit.getEntity(activeSpell.getSourceUuid());
				if (p != null && p instanceof LivingEntity)
				{
					ISoliniaLivingEntity notify = SoliniaLivingEntityAdapter.Adapt((LivingEntity)p);
					if (notify != null && notify.getBukkitLivingEntity() != null)
					{
						if (notify.isCurrentlyNPCPet())
						{
							notify = this.getSoliniaLivingEntity().getOwnerSoliniaLivingEntity();
						}
						
						if (notify != null)
						{
							String spellname =activeSpell.getSpell().getName();
							String entityName = this.getSoliniaLivingEntity().getName();
							ChatUtils.SendHint(notify.getBukkitLivingEntity(), HINT.SPELL_WORN_OFF_OF,  spellname+ "^" +entityName, false);
						}
					}
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}

		if (updateMaxHp == true) {
			if (getLivingEntity() != null)
				if (getLivingEntity() instanceof LivingEntity)
					try {
						ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
						if (solLivingEntity != null)
							solLivingEntity.updateMaxHp();
					} catch (CoreStateInitException e) {
	
					}
		}

		if (updateDisguise == true) {
			if (getLivingEntity() != null)
				DisguiseAPI.undisguiseToAll(getLivingEntity());
		}
		
		
		// Check if bard song, may need to keep singing
		if (forceDoNotLoopBardSpell == false)
		if (activeSpell.getSpell().isBardSong()) {
			try
			{
				if (StateManager.getInstance().getEntityManager().getEntitySinging(activeSpell.getSourceUuid()) != null) 
				{
					boolean isLastSongSinging = StateManager.getInstance().getEntityManager().getEntitySinging(activeSpell.getSourceUuid()).isLastSongSinging(activeSpell.getSpellId());
					if (!isLastSongSinging || activeSpell.getSpell().getRecastTime() > 0 && Bukkit.getEntity(activeSpell.getSourceUuid()) != null && Bukkit.getEntity(activeSpell.getSourceUuid()) instanceof LivingEntity) {
						ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)Bukkit.getEntity(activeSpell.getSourceUuid()));
						ChatUtils.SendHint(solEntity.getBukkitLivingEntity(), HINT.STOPS_SINGING, solEntity.getName()+"^"+activeSpell.getSpell().getName(), true);

						if (removeNonCombatEffects == true)
							tryRemoveNonCombatEffects(activeSpell);
						
						if (solEntity.getBukkitLivingEntity() != null)
						if (solEntity.getBukkitLivingEntity().isOp())
							System.out.println("Debug: " + solEntity.getName() + "'s song comes to a close [" + activeSpell.getSpell().getName() + "]");
						
						StateManager.getInstance().getEntityManager().getEntitySinging(activeSpell.getSourceUuid()).stopSinging(activeSpell.getSpellId());
					} else {
						// Continue singing!
						if (Bukkit.getEntity(activeSpell.getOwnerUuid()) instanceof LivingEntity && Bukkit.getEntity(activeSpell.getSourceUuid()) instanceof LivingEntity && !Bukkit.getEntity(activeSpell.getOwnerUuid()).isDead() && !Bukkit.getEntity(activeSpell.getSourceUuid()).isDead()) {
							boolean itemUseSuccess = activeSpell.getSpell().tryApplyOnEntity(
									(LivingEntity) Bukkit.getEntity(activeSpell.getSourceUuid()),
									(LivingEntity) Bukkit.getEntity(activeSpell.getOwnerUuid()),!removeNonCombatEffects,activeSpell.getRequiredWeaponSkillType());
							if (!itemUseSuccess)
							{
								Bukkit.getEntity(activeSpell.getSourceUuid()).sendMessage(ChatColor.GRAY + "* Your song failed to apply to the entity!");
								if (removeNonCombatEffects == true)
									tryRemoveNonCombatEffects(activeSpell);
							}
							else
							{
								if (removeNonCombatEffects == true)
									tryRemoveNonCombatEffects(activeSpell);
								return;
							}
						}
					}
				} else {
					// skip
				}
			} catch (CoreStateInitException e)
			{
				
			}
		} else {
			if (removeNonCombatEffects == true)
				tryRemoveNonCombatEffects(activeSpell);
		}
		
		if (resendInvisPackets)
		{
			// Resend packet for everyone in range
			if (getLivingEntity() != null)
			for(Entity entity : getLivingEntity().getNearbyEntities(200, 200, 200))
			{
				if (!(entity instanceof LivingEntity))
					continue;
				
				if (!((LivingEntity)entity).hasPotionEffect(PotionEffectType.INVISIBILITY))
					continue;
				
				EntityUtils.sendEntityDataPacket((LivingEntity)entity, (Player)getLivingEntity());
			}
		}
		
		if (removeCharm == true)
		{
			tryRemoveCharm(activeSpell);
		}
	}
	
	public void tryRemoveCharm(SoliniaActiveSpell activeSpell)
	{
		try {
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(getLivingEntity());
			//System.out.println("End of charm, attempting removal of pet");
			if (solLivingEntity != null && solLivingEntity.getActiveMob() != null)
				StateManager.getInstance().getEntityManager().removePet(activeSpell.getSourceUuid(), false);
			else
				System.out.println("Could not remove pet");
		} catch (CoreStateInitException e) {

		}
	}
	
	public void tryRemoveNonCombatEffects(SoliniaActiveSpell activeSpell) {
		// THIS IS FOR REMOVING EFFECTS INSTANTLY INSTEAD OF WAITING FOR NEXT TICK

		try {
			// Listen out for mez type spells here to remove the effect instantly
			if (activeSpell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Mez)) {
				// Instantly remove effect
				if (StateManager.getInstance().getEntityManager().getMezzed(this.getBukkitLivingEntity()) != null) {
					StateManager.getInstance().getEntityManager().removeMezzed(this.getBukkitLivingEntity());
					this.getBukkitLivingEntity().sendMessage("You are not longer mezzed (removed)");
					SpellUtils.RemovePotionEffect(getLivingEntity(), PotionEffectType.CONFUSION);
				}
			}

			// Listen out for NegateIfCombat that are Feign Death status
			if (activeSpell.getSpell().getSpellEffectTypes().contains(SpellEffectType.NegateIfCombat)
					&& activeSpell.getSpell().getSpellEffectTypes().contains(SpellEffectType.FeignDeath)) {
				// Instantly remove effect
				if (StateManager.getInstance().getEntityManager()
						.isFeignedDeath(this.getBukkitLivingEntity().getUniqueId())) {
					StateManager.getInstance().getEntityManager()
							.setFeignedDeath(this.getBukkitLivingEntity().getUniqueId(), false);
					this.getBukkitLivingEntity().sendMessage("You are not longer feigned (NegateIfCombat)");
				}
			}

			// Listen out for NegateIfCombat that are Stun status
			if (activeSpell.getSpell().getSpellEffectTypes().contains(SpellEffectType.NegateIfCombat)
					&& activeSpell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Stun)) {
				// Instantly remove effect
				if (StateManager.getInstance().getEntityManager().getStunned(this.getBukkitLivingEntity()) != null) {
					StateManager.getInstance().getEntityManager().removeStunned(this.getBukkitLivingEntity());
					this.getBukkitLivingEntity().sendMessage("You are not longer stunned (NegateIfCombat)");
					SpellUtils.RemovePotionEffect(getLivingEntity(), PotionEffectType.CONFUSION);
				}
			}
		} catch (CoreStateInitException e) {

		}
	}

	public void removeAllSpells(Plugin plugin, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		removeAllSpellsExcept(plugin, forceDoNotLoopBardSpell, removeNonCombatEffects, new ArrayList<SpellEffectType>());
	}
	public void removeAllSpellsExcept(Plugin plugin, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects, List<SpellEffectType> exclude) 
	{
		removeAllSpellsExcept(plugin, forceDoNotLoopBardSpell, removeNonCombatEffects, exclude, true);
	}
		
	public void removeAllSpellsExcept(Plugin plugin, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects, List<SpellEffectType> exclude, boolean removeBeneficial) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		for (SoliniaActiveSpell activeSpell : getActiveSpells()) {
			if (activeSpell.getSpell().isBeneficial())
				continue;

			boolean isExcluded = false;
			
			for(SpellEffectType typeToExclude : exclude)
			{
				if (activeSpell.getSpell().getSpellEffectTypes().contains(typeToExclude))
				{
					isExcluded = true;
					break;
				}
			}
			
			if (!isExcluded)
				removeSpells.add(activeSpell.getSpellId());
		}

		for (Integer spellId : removeSpells) {
			try
			{
				removeSpell(plugin, spellId, forceDoNotLoopBardSpell, removeNonCombatEffects);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void run(Plugin plugin, boolean sendMessages) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		HashMap<Short, SoliniaActiveSpell> updateSpellsSlots = new HashMap<Short, SoliniaActiveSpell>();

		if (getLivingEntity() == null)
			return;
		
		if (!getLivingEntity().isDead())
			for (Entry<Short, SoliniaActiveSpell> slot : this.slots.entrySet()) {
				SoliniaActiveSpell activeSpell = slot.getValue();
				if (activeSpell.getTicksLeft() == 0) {
					removeSpells.add(activeSpell.getSpellId());
				} else {
					activeSpell.buffTick();
					activeSpell.apply(plugin,sendMessages);
					if (activeSpell.getTicksLeft() >= 1)
					{
						activeSpell.setTicksLeft(activeSpell.getTicksLeft() - 1);
					}
					updateSpellsSlots.put(slot.getKey(), activeSpell);
				}
			}

		for (Integer spellId : removeSpells) {
			removeSpell(plugin, spellId, false, false);
		}

		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : updateSpellsSlots.entrySet()) {
			putSlot(activeSpellSlot.getKey(), activeSpellSlot.getValue(),false);
		}
	}
	
	private void putSlot(Short slot, SoliniaActiveSpell activeSpell, boolean checkExists)
	{
		if (checkExists == true && containsSpellId(activeSpell.getSpellId()))
		{
			System.out.println("Error: tried to put a spell [" + activeSpell.getSpellId() + "] in a slot [" + slot + "] that we already have! Cancelling...");
			return;
		}
		slots.put(slot, activeSpell);
	}

	private Short getNextAvailableSlot() {
		for (Short slot = 0; slot < getMaxTotalSlots(); slot++)
		{
			if (slots.containsKey(slot))
				continue;
			
			return slot;
		}
		
		return null;
	}
	
	public SoliniaActiveSpell getFirstActiveSpellOfEffectType(SpellEffectType type) {
		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : slots.entrySet()) {
			if (activeSpellSlot.getValue().getSpell().isEffectInSpell(type)) {
				return activeSpellSlot.getValue();
			}
		}

		return null;
	}

	// Mainly used for cures
	public void removeFirstSpellOfEffectType(Plugin plugin, SpellEffectType type, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		HashMap<Short, SoliniaActiveSpell> updateSpells = new HashMap<Short, SoliniaActiveSpell>();

		boolean foundToRemove = false;
		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : slots.entrySet()) {
			if (foundToRemove == false && activeSpellSlot.getValue().getSpell().isEffectInSpell(type)) {
				removeSpells.add(activeSpellSlot.getValue().getSpellId());
				foundToRemove = true;
			} else {
				updateSpells.put(activeSpellSlot.getKey(), activeSpellSlot.getValue());
			}
		}

		for (Integer spellId : removeSpells) {
			removeSpell(plugin, spellId, forceDoNotLoopBardSpell, removeNonCombatEffects);
		}

		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : updateSpells.entrySet()) {
			putSlot(activeSpellSlot.getKey(), activeSpellSlot.getValue(), false);
		}

	}
	
	// Mainly used for cures
	public void removeSpellsOfEffectType(Plugin plugin, SpellEffectType type, boolean forceDoNotLoopBardSpell,
			boolean removeNonCombatEffects) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		HashMap<Short, SoliniaActiveSpell> updateSpells = new HashMap<Short, SoliniaActiveSpell>();

		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : slots.entrySet()) {
			if (activeSpellSlot.getValue().getSpell().isEffectInSpell(type)) {
				removeSpells.add(activeSpellSlot.getValue().getSpellId());
			} else {
				updateSpells.put(activeSpellSlot.getKey(), activeSpellSlot.getValue());
			}
		}

		for (Integer spellId : removeSpells) {
			removeSpell(plugin, spellId, forceDoNotLoopBardSpell, removeNonCombatEffects);
		}

		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : updateSpells.entrySet()) {
			putSlot(activeSpellSlot.getKey(), activeSpellSlot.getValue(), false);
		}

	}

	public void removeAllSpellsOfId(Plugin plugin, int spellId, boolean forceDoNotLoopBardSpell, boolean removeNonCombatEffects) {
		removeSpell(plugin, spellId, forceDoNotLoopBardSpell, removeNonCombatEffects);
	}

	public void removeFirstSpell(Plugin plugin, boolean forceDoNotLoopBardSpell) {
		List<Integer> removeSpells = new ArrayList<Integer>();
		HashMap<Short, SoliniaActiveSpell> updateSpells = new HashMap<Short, SoliniaActiveSpell>();

		boolean foundToRemove = false;
		for (Entry<Short,SoliniaActiveSpell> activeSpellSlot : slots.entrySet()) {
			if (foundToRemove == false) {
				removeSpells.add(activeSpellSlot.getValue().getSpellId());
				foundToRemove = true;
			} else {
				updateSpells.put(activeSpellSlot.getKey(), activeSpellSlot.getValue());
			}
		}

		for (Integer spellId : removeSpells) {
			removeSpell(plugin, spellId, forceDoNotLoopBardSpell, forceDoNotLoopBardSpell);
		}

		for (Entry<Short, SoliniaActiveSpell> activeSpellSlot : updateSpells.entrySet()) {
			putSlot(activeSpellSlot.getKey(), activeSpellSlot.getValue(), false);
		}
	}
}
