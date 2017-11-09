package com.solinia.solinia.Models;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityDamageSource;
import net.minecraft.server.v1_12_R1.EntityTameableAnimal;
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalOwnerHurtByTarget;
import net.minecraft.server.v1_12_R1.PathfinderGoalOwnerHurtTarget;

public class SoliniaLivingEntity implements ISoliniaLivingEntity {
	LivingEntity livingentity;
	private int level = 1;
	private int npcid;

	public SoliniaLivingEntity(LivingEntity livingentity) {
		this.livingentity = livingentity;

		String metaid = "";
		if (livingentity != null)
			for (MetadataValue val : livingentity.getMetadata("mobname")) {
				metaid = val.asString();
			}

		for (MetadataValue val : livingentity.getMetadata("npcid")) {
			metaid = val.asString();
		}

		if (metaid != null)
			if (!metaid.equals(""))
				installNpcByMetaName(metaid);
	}

	private void installNpcByMetaName(String metaid) {
		if (isPlayer())
			return;

		if (!metaid.contains("NPCID_"))
			return;

		int npcId = Integer.parseInt(metaid.substring(6));
		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcId);

			if (npc == null)
				return;

			setLevel(npc.getLevel());
			setNpcid(npc.getId());
		} catch (CoreStateInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getDamageCaps(int base_damage)
	{
		// this is based on a client function that caps melee base_damage
		int level = getLevel();
		int stop_level = Utils.getMaxLevel() + 1;
		if (stop_level <= level)
			return base_damage;
		int cap = 0;
		if (level >= 125) {
			cap = 7 * level;
		}
		else if (level >= 110) {
			cap = 6 * level;
		}
		else if (level >= 90) {
			cap = 5 * level;
		}
		else if (level >= 70) {
			cap = 4 * level;
		}
		else if (level >= 40) {
			if (getClassObj() != null)
			switch (getClassObj().getName()) {
			case "CLERIC":
			case "DRUID":
			case "SHAMAN":
				cap = 80;
				break;
			case "NECROMANCER":
			case "WIZARD":
			case "MAGICIAN":
			case "ENCHANTER":
				cap = 40;
				break;
			default:
				cap = 200;
				break;
			}
			else
				cap = 200;
		}
		else if (level >= 30) {
			if (getClassObj() != null)
			switch (getClassObj().getName()) {
			case "CLERIC":
			case "DRUID":
			case "SHAMAN":
				cap = 26;
				break;
			case "NECROMANCER":
			case "WIZARD":
			case "MAGICIAN":
			case "ENCHANTER":
				cap = 18;
				break;
			default:
				cap = 60;
				break;
			}
			else
				cap = 60;
		}
		else if (level >= 20) {
			if (getClassObj() != null)
			switch (getClassObj().getName()) {
			case "CLERIC":
			case "DRUID":
			case "SHAMAN":
				cap = 20;
				break;
			case "NECROMANCER":
			case "WIZARD":
			case "MAGICIAN":
			case "ENCHANTER":
				cap = 12;
				break;
			default:
				cap = 30;
				break;
			}
			else
				cap = 30;
		}
		else if (level >= 10) {
			if (getClassObj() != null)
			switch (getClassObj().getName()) {
			case "CLERIC":
			case "DRUID":
			case "SHAMAN":
				cap = 12;
				break;
			case "NECROMANCER":
			case "WIZARD":
			case "MAGICIAN":
			case "ENCHANTER":
				cap = 10;
				break;
			default:
				cap = 14;
				break;
			}
			else
				cap = 14;
		}
		else {
			if (getClassObj() != null)
			switch (getClassObj().getName()) {
			case "CLERIC":
			case "DRUID":
			case "SHAMAN":
				cap = 9;
				break;
			case "NECROMANCER":
			case "WIZARD":
			case "MAGICIAN":
			case "ENCHANTER":
				cap = 6;
				break;
			default:
				cap = 10; // this is where the 20 damage cap comes from
				break;
			}
			else
				cap = 10;
		}

		return Math.min(cap, base_damage);
	}
	
	public boolean usingValidWeapon()
	{
		if (getBukkitLivingEntity().getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(getBukkitLivingEntity().getEquipment().getItemInMainHand());
				if (soliniaitem != null) 
				{
					if (soliniaitem.getAllowedClassNames().size() > 0) {
						if (getClassObj() == null) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}

						if (!soliniaitem.getAllowedClassNames().contains(getClassObj().getName())) {
							getBukkitLivingEntity().sendMessage(ChatColor.GRAY + "Your class cannot use this item");
							return false;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	@Override
	public boolean Attack(ISoliniaLivingEntity defender, EntityDamageEvent event, boolean arrowHit, Solinia3CorePlugin plugin)
	{
		int baseDamage = (int)event.getDamage(DamageModifier.BASE);

		if (usingValidWeapon() == false)
		{
			event.setCancelled(true);
			return false;
		} else {
			if (getBukkitLivingEntity().getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999) {
				try
				{
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(getBukkitLivingEntity().getEquipment().getItemInMainHand());
				
				// TODO move this
				if (soliniaitem.getBaneUndead() > 0 && defender.isUndead())
					baseDamage += soliniaitem.getBaneUndead();
				} catch (CoreStateInitException e)
				{
					event.setCancelled(true);
					return false;
				}
			}
		}
		
		if (baseDamage < 1)
			baseDamage = 1;
		
		if (defender == null) {
			event.setCancelled(true);
			return false;
		}

		if (defender.getBukkitLivingEntity().isDead() || this.getBukkitLivingEntity().isDead() || this.getBukkitLivingEntity().getHealth() < 0)
		{
			event.setCancelled(true);
			return false;
		}

		if (isInulvnerable()) 
		{
			event.setCancelled(true);
			return false;
		}

		if (isFeigned())
		{
			event.setCancelled(true);
			return false;
		}

		ItemStack weapon = this.getBukkitLivingEntity().getEquipment().getItemInHand();

		DamageHitInfo my_hit = new DamageHitInfo();
		my_hit.skill = Utils.getSkillForMaterial(weapon.getType().toString()).getSkillname();
		if (arrowHit)
		{
			my_hit.skill = "ARCHERY";
		}
		
		// Now figure out damage
		my_hit.damage_done = 1;
		my_hit.min_damage = 0;
		int mylevel = getLevel();
		int hate = 0;

		my_hit.base_damage = baseDamage;
		// amount of hate is based on the damage done
		if (hate == 0 && my_hit.base_damage > 1)
			hate = my_hit.base_damage;

		if (my_hit.base_damage > 0) {
			my_hit.base_damage = getDamageCaps(my_hit.base_damage);

			tryIncreaseSkill(my_hit.skill, 1);
			tryIncreaseSkill("OFFENSE", 1);

			int ucDamageBonus = 0;

			if (getClassObj() != null && getClassObj().isWarriorClass() && getLevel() >= 28)
			{
				ucDamageBonus = getWeaponDamageBonus(weapon);
				my_hit.min_damage = ucDamageBonus;
				hate += ucDamageBonus;
			}
			
			// TODO Sinister Strikes

			int hit_chance_bonus = 0;
			my_hit.offense = getOffense(my_hit.skill); // we need this a few times
			my_hit.tohit = getTotalToHit(my_hit.skill, hit_chance_bonus);

			doAttack(defender, my_hit);
		}

		defender.addToHateList(getBukkitLivingEntity().getUniqueId(), hate);

		///////////////////////////////////////////////////////////
		////// Send Attack Damage
		///////////////////////////////////////////////////////////
		
		// TODO Skill Procs

		if (getBukkitLivingEntity().isDead()) 
		{
			event.setCancelled(true);
			return false;
		}

		if (my_hit.damage_done > 0)
		{
			triggerDefensiveProcs(defender, my_hit.damage_done, arrowHit);
			
			try
			{
				event.setDamage(DamageModifier.ABSORPTION,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			try
			{
				event.setDamage(DamageModifier.ARMOR,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			try
			{
				event.setDamage(DamageModifier.BASE, my_hit.damage_done);
			} catch (UnsupportedOperationException e)
			{
				
			}
			try
			{
				event.setDamage(DamageModifier.BLOCKING,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			try
			{
				event.setDamage(DamageModifier.HARD_HAT,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			try
			{
				event.setDamage(DamageModifier.MAGIC,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			try
			{
				event.setDamage(DamageModifier.RESISTANCE,0);
			} catch (UnsupportedOperationException e)
			{
				
			}
			
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			
			if (getBukkitLivingEntity() instanceof Player) {
				((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent("You hit " + defender.getBukkitLivingEntity().getCustomName() + " for "
								+ df.format(event.getDamage()) + " "
								+ df.format(getBukkitLivingEntity().getHealth() - event.getDamage()) + "/"
								+ df.format(getBukkitLivingEntity().getMaxHealth()) + " " + my_hit.skill + " damage"));
				
				// Only players get this
				if (getDoubleAttackCheck()) {
					if (getBukkitLivingEntity() instanceof Player) {
						((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You double attack!");
						tryIncreaseSkill("DOUBLEATTACK",
								1);
					}
					defender.getBukkitLivingEntity().damage(my_hit.damage_done, this.getBukkitLivingEntity());
				}
				
				try {
					if (getBukkitLivingEntity().getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999) {
						try
						{
							ISoliniaItem soliniaitem = SoliniaItemAdapter.Adapt(getBukkitLivingEntity().getEquipment().getItemInMainHand());
							if (soliniaitem != null)
							{
							// Check if item has any proc effects
								if (soliniaitem.getWeaponabilityid() > 0
										&& event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
									ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager()
											.getSpell(soliniaitem.getWeaponabilityid());
									if (procSpell != null) {
										// Chance to proc
										int procChance = getProcChancePct();
										int roll = Utils.RandomBetween(0, 100);
			
										if (roll < procChance) {
			
											// TODO - For now apply self and group to attacker, else attach to target
											switch (Utils.getSpellTargetType(procSpell.getTargettype())) {
											case Self:
												procSpell.tryApplyOnEntity(plugin, this.getBukkitLivingEntity(), this.getBukkitLivingEntity());
												break;
											case Group:
												procSpell.tryApplyOnEntity(plugin, this.getBukkitLivingEntity(), this.getBukkitLivingEntity());
												break;
											default:
												procSpell.tryApplyOnEntity(plugin, this.getBukkitLivingEntity(), defender.getBukkitLivingEntity());
											}
			
										}
									}
								}
							}
						} catch (SoliniaItemException e) {
							// skip
						}
					}
					
					// Check if attacker has any WeaponProc effects
					SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(this.getBukkitLivingEntity());

					if (effects != null) {
						for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
							ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
									.getSpell(activeSpell.getSpellId());
							if (spell == null)
								continue;

							if (!spell.isWeaponProc())
								continue;

							for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
								if (spelleffect.getSpellEffectType().equals(SpellEffectType.WeaponProc)) {
									if (spelleffect.getBase() < 0)
										continue;

									ISoliniaSpell procSpell = StateManager.getInstance().getConfigurationManager()
											.getSpell(spelleffect.getBase());
									if (spell == null)
										continue;

									// Chance to proc
									int procChance = getProcChancePct();
									int roll = Utils.RandomBetween(0, 100);

									if (roll < procChance) {
										boolean itemUseSuccess = procSpell.tryApplyOnEntity(plugin, this.getBukkitLivingEntity(),
												defender.getBukkitLivingEntity());

										if (procSpell.getMana() > 0)
											if (itemUseSuccess) {
												if (getBukkitLivingEntity() instanceof Player) {
													SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).reducePlayerMana(procSpell.getMana());
												}
											}
									}
								}
							}
						}
					}
				} catch (CoreStateInitException e) {

				}
			}
			
			if (defender.getBukkitLivingEntity() instanceof Player)
			{
				((Player)defender.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent("You were hit by " + getBukkitLivingEntity().getCustomName() + " for "
								+ df.format(event.getDamage()) + " " + my_hit.skill + " damage"));
			}
			
			return true;
		} else {
			event.setCancelled(true);
			return false;
		}
	}

	private void triggerDefensiveProcs(ISoliniaLivingEntity defender, int damage, boolean arrowHit) {
		if (damage < 0)
			return;
		
		if (arrowHit)
			return;
		
		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(defender.getBukkitLivingEntity());
			if (effects == null)
				return;
			
			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager()
						.getSpell(activeSpell.getSpellId());

				if (spell == null)
					continue;

				if (!spell.isDamageShield())
					continue;

				for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
					if (spelleffect.getSpellEffectType().equals(SpellEffectType.DamageShield)) {
						// hurt enemy with damage shield
						if (spelleffect.getCalculatedValue() < 0) {
							EntityDamageSource source = new EntityDamageSource("thorns",
									((CraftEntity) defender.getBukkitLivingEntity()).getHandle());
							source.setMagic();
							source.ignoresArmor();

							((CraftEntity) this.getBukkitLivingEntity()).getHandle().damageEntity(source,
									spelleffect.getCalculatedValue() * -1);
							// attacker.damage(spelleffect.getBase() * -1);

							DecimalFormat df = new DecimalFormat();
							df.setMaximumFractionDigits(2);
							
							if (defender instanceof Player) {
								((Player) defender.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
										new TextComponent("Your damage shield hit " + this.getBukkitLivingEntity().getName() + " for "
												+ df.format(spelleffect.getCalculatedValue() * -1) + "["
												+ df.format(this.getBukkitLivingEntity().getHealth()
														- (spelleffect.getCalculatedValue() * -1))
												+ "/" + df.format(this.getBukkitLivingEntity().getMaxHealth()) + "]"));
							}
						}
					}
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}
	}

	private DamageHitInfo doAttack(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;
		
		// for riposte
		int originalDamage = hit.damage_done;
		
		hit = defender.avoidDamage(this, hit);
		if (hit.avoided == true) 
		{
			// TODO Strike through
			
			if (hit.riposted == true && originalDamage > 0)
			{
				if (defender.isPlayer()) {
					((Player) defender.getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You riposte the attack!");
					defender.tryIncreaseSkill("RIPOSTE",1);
				}
				
				if (isPlayer())
				{
					((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "+ defender.getBukkitLivingEntity().getCustomName() + " ripostes your attack!");
					((Player) getBukkitLivingEntity()).damage(originalDamage, getBukkitLivingEntity());
				}
			}
			
			if (hit.dodged == true)
			{
				if (defender.isPlayer()) {
					((Player) defender.getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You dodge the attack!");
					defender.tryIncreaseSkill("DODGE",1);
				}
				
				if (isPlayer())
				{
					((Player) getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* "+ defender.getBukkitLivingEntity().getCustomName() + " dodges your attack!");
				}
			}
		}

		if (hit.damage_done >= 0) {
			if (defender.checkHitChance(this, hit)) {
				hit = defender.meleeMitigation(this, hit);
				if (hit.damage_done > 0) {
					hit = applyDamageTable(hit);
					hit = commonOutgoingHitSuccess(defender, hit);
				}
			}
			else {
				if (getBukkitLivingEntity() instanceof Player)
				{
					((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("You tried to hit " + defender.getBukkitLivingEntity().getCustomName() + ", but missed!"));
				}
				if (defender.getBukkitLivingEntity() instanceof Player)
				{
					((Player) defender.getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(getBukkitLivingEntity().getCustomName() + " tried to hit you, but missed!"));
					try
					{
						ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)defender.getBukkitLivingEntity());
						solplayer.tryIncreaseSkill("DEFENSE", 1);
					} catch (CoreStateInitException e)
					{
						// skip
					}
				}
				hit.damage_done = 0;
			}
		}
		
		return hit;
	}

	private DamageHitInfo commonOutgoingHitSuccess(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;

		if (hit.skill.equals("ARCHERY"))
			hit.damage_done /= 2;

		if (hit.damage_done < 1)
			hit.damage_done = 1;

		// TODO Archery head shots

		int extra_mincap = 0;
		int min_mod = hit.base_damage * getMeleeMinDamageMod_SE(hit.skill) / 100;
		// TODO Backstab
		
		hit.min_damage += getSkillDmgAmt(hit.skill);

		// TODO shielding mod2
		// TODO item melee mitigation

		hit.damage_done = applyMeleeDamageMods(hit.skill, hit.damage_done, defender);
		min_mod = Math.max(min_mod, extra_mincap);

		hit = tryCriticalHit(defender, hit);

		hit.damage_done += hit.min_damage;
		
		// this appears where they do special attack dmg mods
		int spec_mod = 0;
		
		// TODO RAMPAGE
		
		if (spec_mod > 0)
			hit.damage_done = (hit.damage_done * spec_mod) / 100;

		hit.damage_done += (hit.damage_done * defender.getSkillDmgTaken(hit.skill) / 100) + (defender.getFcDamageAmtIncoming(this, 0, true, hit.skill));
		
		return hit;
	}

	private int getMeleeMinDamageMod_SE(String skill) {
		int dmg_mod = 0;

		//dmg_mod = itembonuses.MinDamageModifier[skill] + spellbonuses.MinDamageModifier[skill] +
		//	itembonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1] + spellbonuses.MinDamageModifier[EQEmu::skills::HIGHEST_SKILL + 1];

		if(dmg_mod < -100)
			dmg_mod = -100;

		return dmg_mod;
	}

	private int applyMeleeDamageMods(String skill, int damage_done, ISoliniaLivingEntity defender) {
		int dmgbonusmod = 0;

		//dmgbonusmod += GetMeleeDamageMod_SE(skill);

		if (defender != null) {
			if (defender.isPlayer() && defender.getClassObj() != null && defender.getClassObj().getName().equals("WARRIOR"))
				dmgbonusmod -= 5;
			// 168 defensive
			//dmgbonusmod += (defender->spellbonuses.MeleeMitigationEffect + itembonuses.MeleeMitigationEffect + aabonuses.MeleeMitigationEffect);
		}

		damage_done += damage_done * dmgbonusmod / 100;
		return damage_done;
	}

	private int getSkillDmgAmt(String skill) {
		int skill_dmg = 0;
		return skill_dmg;
	}

	private DamageHitInfo tryCriticalHit(ISoliniaLivingEntity defender, DamageHitInfo hit) {
		if (defender == null)
			return hit;
		
		if (hit.damage_done < 1)
			return hit;

		String className = "UNKNOWN";
		if (getClassObj() != null)
		{
			className = getClassObj().getName();
		}
		
		// TODO Slay Undead AA

		// 2: Try Melee Critical
		
		boolean innateCritical = false;
		int critChance = Utils.getCriticalChanceBonus(this, hit.skill);
		if ((className.equals("WARRIOR") || className.equals("BERSERKER")) && getLevel() >= 12)
			innateCritical = true;
		else if (className.equals("RANGER") && getLevel() >= 12 && hit.skill.equals("ARCHERY"))
			innateCritical = true;
		else if (className.equals("ROGUE") && getLevel() >= 12 && hit.skill.equals("THROWING"))
			innateCritical = true;

		// we have a chance to crit!
		if (innateCritical || critChance > 0) {
			int difficulty = 0;
			if (hit.skill.equals("ARCHERY"))
				difficulty = 3400;
			else if (hit.skill.equals("THROWING"))
				difficulty = 1100;
			else
				difficulty = 8900;

			//attacker.sendMessage("You have a chance to cause a critical (Diffulty dice roll: " + difficulty);
			int roll = Utils.RandomBetween(1, difficulty);
			//attacker.sendMessage("Critical chance roll ended up as: " + roll);

			int dex_bonus = getDexterity();
			if (dex_bonus > 255)
				dex_bonus = 255 + ((dex_bonus - 255) / 5);
			dex_bonus += 45; 
			//attacker.sendMessage("Critical dex bonus was: " + dex_bonus);

			// so if we have an innate crit we have a better chance, except for ber throwing
			if (!innateCritical || (className.equals("BERSERKER") && hit.skill.equals("THROWING")))
				dex_bonus = dex_bonus * 3 / 5;

			if (critChance > 0)
				dex_bonus += dex_bonus * critChance / 100;
			

			//attacker.sendMessage("Checking if your roll: " + roll + " is less than the dexbonus: " + dex_bonus);

			// check if we crited
			if (roll < dex_bonus) {

				//  TODO: Finishing Blow
				
				// step 2: calculate damage
				hit.damage_done = Math.max(hit.damage_done, hit.base_damage) + 5;
				//attacker.sendMessage("Taking the maximum out of damageDone: " + damageDone + " vs baseDamage: " + baseDamage + " adding 5 to it");

				double og_damage = hit.damage_done;
				int crit_mod = 170 + Utils.getCritDmgMod(hit.skill);
				if (crit_mod < 100) {
					crit_mod = 100;
				}
				//attacker.sendMessage("Crit mod was: " + crit_mod);

				hit.damage_done = hit.damage_done * crit_mod / 100;
				//attacker.sendMessage("DamageDone was calculated at: " + damageDone);
				
				// TODO Spell bonuses && holyforge
				double totalCritBonus = (hit.damage_done - hit.base_damage);
				
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);
				
				// Berserker
				if (isBerserk()) {
					hit.damage_done += og_damage * 119 / 100;
					//attacker.sendMessage("You are also berserker: damageDone now is: " + damageDone);
					//attacker.sendMessage("* Your berserker status causes a critical blow!");
					
					totalCritBonus = (hit.damage_done - hit.base_damage);
					
					if (getBukkitLivingEntity() instanceof Player) {
						((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent("* Your berserker status causes additional critical blow damage ["+df.format(totalCritBonus)+"]!"));
					}
					
					return hit;
				}

				if (getBukkitLivingEntity() instanceof Player) {
					((Player) getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent("You scored additional critical damage! [" + df.format(totalCritBonus) + "]"));
				}
				
				//attacker.sendMessage("* Your score a critical hit (" + damageDone + ")!");
				return hit;
			}
			
		}
		
		return hit;
	}

	private DamageHitInfo applyDamageTable(DamageHitInfo hit) {

		if (hit.offense < 115)
			return hit;

		if (hit.damage_done < 2)
			return hit;

		// 0 = max_extra
		// 1 = chance
		// 2 = minusfactor
		int[] damage_table = getDamageTable();

		if (Utils.RandomBetween(0, 100) < (damage_table[1]))
			return hit;

		int basebonus = hit.offense - damage_table[2];
		basebonus = Math.max(10, basebonus / 2);
		int extrapercent = Utils.RandomBetween(0,basebonus);
		int percent = Math.min(100 + extrapercent, damage_table[0]);
		hit.damage_done = (hit.damage_done * percent) / 100;

		if (getClassObj() != null)
		{
			if (getClassObj().isWarriorClass() && getLevel() > 54)
				hit.damage_done++;
		}
		
		return hit;
	}
	
	public int[] getDamageTable()
	{
		int[][] dmg_table = {
			{ 210, 49, 105 }, // 1-50
			{ 245, 35,  80 }, // 51
			{ 245, 35,  80 }, // 52
			{ 245, 35,  80 }, // 53
			{ 245, 35,  80 }, // 54
			{ 245, 35,  80 }, // 55
			{ 265, 28,  70 }, // 56
			{ 265, 28,  70 }, // 57
			{ 265, 28,  70 }, // 58
			{ 265, 28,  70 }, // 59
			{ 285, 23,  65 }, // 60
			{ 285, 23,  65 }, // 61
			{ 285, 23,  65 }, // 62
			{ 290, 21,  60 }, // 63
			{ 290, 21,  60 }, // 64
			{ 295, 19,  55 }, // 65
			{ 295, 19,  55 }, // 66
			{ 300, 19,  55 }, // 67
			{ 300, 19,  55 }, // 68
			{ 300, 19,  55 }, // 69
			{ 305, 19,  55 }, // 70
			{ 305, 19,  55 }, // 71
			{ 310, 17,  50 }, // 72
			{ 310, 17,  50 }, // 73
			{ 310, 17,  50 }, // 74
			{ 315, 17,  50 }, // 75
			{ 315, 17,  50 }, // 76
			{ 325, 17,  45 }, // 77
			{ 325, 17,  45 }, // 78
			{ 325, 17,  45 }, // 79
			{ 335, 17,  45 }, // 80
			{ 335, 17,  45 }, // 81
			{ 345, 17,  45 }, // 82
			{ 345, 17,  45 }, // 83
			{ 345, 17,  45 }, // 84
			{ 355, 17,  45 }, // 85
			{ 355, 17,  45 }, // 86
			{ 365, 17,  45 }, // 87
			{ 365, 17,  45 }, // 88
			{ 365, 17,  45 }, // 89
			{ 375, 17,  45 }, // 90
			{ 375, 17,  45 }, // 91
			{ 380, 17,  45 }, // 92
			{ 380, 17,  45 }, // 93
			{ 380, 17,  45 }, // 94
			{ 385, 17,  45 }, // 95
			{ 385, 17,  45 }, // 96
			{ 390, 17,  45 }, // 97
			{ 390, 17,  45 }, // 98
			{ 390, 17,  45 }, // 99
			{ 395, 17,  45 }, // 100
			{ 395, 17,  45 }, // 101
			{ 400, 17,  45 }, // 102
			{ 400, 17,  45 }, // 103
			{ 400, 17,  45 }, // 104
			{ 405, 17,  45 }  // 105
		};

		int[][] mnk_table = {
			{ 220, 45, 100 }, // 1-50
			{ 245, 35,  80 }, // 51
			{ 245, 35,  80 }, // 52
			{ 245, 35,  80 }, // 53
			{ 245, 35,  80 }, // 54
			{ 245, 35,  80 }, // 55
			{ 285, 23,  65 }, // 56
			{ 285, 23,  65 }, // 57
			{ 285, 23,  65 }, // 58
			{ 285, 23,  65 }, // 59
			{ 290, 21,  60 }, // 60
			{ 290, 21,  60 }, // 61
			{ 290, 21,  60 }, // 62
			{ 295, 19,  55 }, // 63
			{ 295, 19,  55 }, // 64
			{ 300, 17,  50 }, // 65
			{ 300, 17,  50 }, // 66
			{ 310, 17,  50 }, // 67
			{ 310, 17,  50 }, // 68
			{ 310, 17,  50 }, // 69
			{ 320, 17,  50 }, // 70
			{ 320, 17,  50 }, // 71
			{ 325, 15,  45 }, // 72
			{ 325, 15,  45 }, // 73
			{ 325, 15,  45 }, // 74
			{ 330, 15,  45 }, // 75
			{ 330, 15,  45 }, // 76
			{ 335, 15,  40 }, // 77
			{ 335, 15,  40 }, // 78
			{ 335, 15,  40 }, // 79
			{ 345, 15,  40 }, // 80
			{ 345, 15,  40 }, // 81
			{ 355, 15,  40 }, // 82
			{ 355, 15,  40 }, // 83
			{ 355, 15,  40 }, // 84
			{ 365, 15,  40 }, // 85
			{ 365, 15,  40 }, // 86
			{ 375, 15,  40 }, // 87
			{ 375, 15,  40 }, // 88
			{ 375, 15,  40 }, // 89
			{ 385, 15,  40 }, // 90
			{ 385, 15,  40 }, // 91
			{ 390, 15,  40 }, // 92
			{ 390, 15,  40 }, // 93
			{ 390, 15,  40 }, // 94
			{ 395, 15,  40 }, // 95
			{ 395, 15,  40 }, // 96
			{ 400, 15,  40 }, // 97
			{ 400, 15,  40 }, // 98
			{ 400, 15,  40 }, // 99
			{ 405, 15,  40 }, // 100
			{ 405, 15,  40 }, // 101
			{ 410, 15,  40 }, // 102
			{ 410, 15,  40 }, // 103
			{ 410, 15,  40 }, // 104
			{ 415, 15,  40 }, // 105
		};

		boolean monk = false;
		boolean melee = false;

		if (getClassObj() != null)
		{
			monk = getClassObj().getName().equals("MONK");
			melee = getClassObj().isWarriorClass();
		}
		
		// tables caped at 105 for now -- future proofed for a while at least :P
		int level = Math.min(getLevel(), Utils.getMaxLevel());

		if (!melee || (!monk && level < 51))
			return dmg_table[0];

		if (monk && level < 51)
			return mnk_table[0];

		int[][] which = monk ? mnk_table : dmg_table;
		return which[level - 50];
	}

	@Override
	public int getOffense(String skillname)
	{
		int offense = getSkill(skillname);
		int stat_bonus = 0;
		if (skillname.equals("ARCHERY") || skillname.equals("THROWING"))
			stat_bonus = getDexterity();
		else
			stat_bonus = getStrength();
		if (stat_bonus >= 75)
			offense += (2 * stat_bonus - 150) / 3;
		
		// TODO do ATTK
		//offense += getAttk();
		return offense;
	}

	@Override
	public void tryIncreaseSkill(String skillName, int amount) {
		if (!isPlayer())
			return;
		
		try
		{
			ISoliniaPlayer solplayerReward = SoliniaPlayerAdapter.Adapt((Player)this.getBukkitLivingEntity());
			solplayerReward.tryIncreaseSkill(skillName, amount);
		} catch (CoreStateInitException e)
		{
			// dont increase skill
		}
	}
	
	@Override
	public int getWeaponDamageBonus(ItemStack itemStack)
	{
		int level = getLevel();
		if (itemStack == null)
			return 1 + ((level - 28) / 3);

		// TODO include weapon delays
		int delay = 30;
		
		// 1hand weapons
		// we assume sinister strikes is checked before calling here
		if (delay <= 39)
			return 1 + ((level - 28) / 3);
		else if (delay < 43)
			return 2 + ((level - 28) / 3) + ((delay - 40) / 3);
		else if (delay < 45)
			return 3 + ((level - 28) / 3) + ((delay - 40) / 3);
		else if (delay >= 45)
			return 4 + ((level - 28) / 3) + ((delay - 40) / 3);

		// TODO do 2hand items
		
		return 0;
	}

	private boolean isFeigned() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isInulvnerable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LivingEntity getBukkitLivingEntity() {
		// TODO Auto-generated method stub
		return this.livingentity;
	}
	
	@Override
	public int getSkill(String skillname)
	{
		int defaultskill = 0;
		
		try
		{
			if (isPlayer())
			{
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				return player.getSkill(skillname.toUpperCase()).getValue();
			}
			
			if (isNPC())
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				return npc.getSkill(skillname.toUpperCase());
			}
		} catch (CoreStateInitException e)
		{
			return defaultskill;
		}
		
		return defaultskill;		
	}

	@Override
	public int computeToHit(String skillname)
	{
		double tohit = getSkill("OFFENSE") + 7;
		tohit += getSkill(skillname.toUpperCase());
		if (isNPC())
		{
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				if (npc != null)
					tohit += npc.getAccuracyRating();
			} catch (CoreStateInitException e)
			{
				//
			}
		}
		if (isPlayer()) {
			double reduction = getIntoxication() / 2.0;
			if (reduction > 20.0) {
				reduction = Math.min((110 - reduction) / 100.0, 1.0);
				tohit = reduction * (double)(tohit);
			}
			else if (isBerserk()) {
				tohit += (getLevel() * 2) / 5;
			}
		}
		return (int)Math.max(tohit, 1);
	}
	
	private double getIntoxication() {
		// TODO - Drinking increases intoxication
		return 0;
	}

	@Override
	public boolean isBerserk() {
		// if less than 10% health and warrior, is in berserk mode
		if (this.getBukkitLivingEntity().getHealth() < ((this.getBukkitLivingEntity().getMaxHealth() / 100)*10))
		if (this.getClassObj() != null)
		{
			if (this.getClassObj().getName().equals("WARRIOR"))
				return true;
		}
		
		return false;
	}

	@Override
	public int getTotalToHit(String skillname, int hitChanceBonus) {
		if (hitChanceBonus >= 10000) // override for stuff like SE_SkillAttack
			return -1;
		
		skillname = skillname.toUpperCase();

		// calculate attacker's accuracy
		double accuracy = computeToHit(skillname) + 10; 
		if (hitChanceBonus > 0) // multiplier
			accuracy *= hitChanceBonus;

		accuracy = (accuracy * 121) / 100;

		/* TODO
		if (!skillname.equals("ARCHERY") && !skillname.equals("THROWING"))
		{
			accuracy += getItemBonuses("HITCHANCE");
		}
		*/

		// TODO
		double accuracySkill = 0;
		accuracy += accuracySkill;

		// TODO 
		double buffItemAndAABonus = 0;		

		accuracy = (accuracy * (100 + buffItemAndAABonus)) / 100;
		return (int)Math.floor(accuracy);
	}

	@Override
	public int getDefenseByDefenseSkill()
	{
		double defense = getSkill("DEFENSE") * 400 / 225;
		defense += (8000 * (getAgility() - 40)) / 36000;

		// TODO Item bonsues
		//defense += itembonuses.AvoidMeleeChance; // item mod2
		if (isNPC())
		{
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				defense += npc.getAvoidanceRating();
			} catch (CoreStateInitException e)
			{
				// no bonus
			}
		}

		if (isPlayer()) {
			double reduction = getIntoxication() / 2.0;
			if (reduction > 20.0) {
				reduction = Math.min((110 - reduction) / 100.0, 1.0);
				defense = reduction * (double)(defense);
			}
		}

		return (int)Math.max(1, defense);
	}
	
	@Override
	public int getTotalDefense() {
		double avoidance = getDefenseByDefenseSkill() + 10;

		// Todo avoid melee chance spell effects
		/*
		int evasion_bonus = spellbonuses.AvoidMeleeChanceEffect; 
		if (evasion_bonus >= 10000)
			return -1;
		*/	
		
		double aaItemAAAvoidance = 0;
		
		// Todo item bonuses
		//aaItemAAAvoidance += itembonuses.AvoidMeleeChanceEffect + aabonuses.AvoidMeleeChanceEffect; // item bonus here isn't mod2 avoidance
		
		// Evasion is a percentage bonus according to AA descriptions
		if (aaItemAAAvoidance > 0)
			avoidance = (avoidance * (100 + aaItemAAAvoidance)) / 100;

		return (int)Math.floor(avoidance);
	}

	@Override
	public boolean isPet() {
		if (isPlayer())
			return false;

		if (this.getNpcid() < 1)
			return false;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.isPet())
				return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean isUndead() {
		if (isPlayer())
			return false;

		if (this.getNpcid() < 1)
			return false;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.isUndead())
				return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	@Override
	public String getSkillNameFromMaterialInHand(Material materialinhand)
	{
		SkillReward reward = Utils.getSkillForMaterial(materialinhand.toString());
		return reward.getSkillname();
	}

	@Override
	public int getLevel() {
		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solPlayer.getLevel();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}

		return level;
	}

	@Override
	public void setLevel(int level) {
		if (isPlayer())
			return;

		this.level = level;
	}

	@Override
	public void dropLoot() {
		if (isPlayer())
			return;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc.getLoottableid() == 0)
					return;

				ISoliniaLootTable table = StateManager.getInstance().getConfigurationManager()
						.getLootTable(npc.getLoottableid());

				List<ISoliniaLootDropEntry> absoluteitems = new ArrayList<ISoliniaLootDropEntry>();
				List<ISoliniaLootDropEntry> rollitems = new ArrayList<ISoliniaLootDropEntry>();

				for (ISoliniaLootTableEntry entry : StateManager.getInstance().getConfigurationManager()
						.getLootTable(table.getId()).getEntries()) {
					ISoliniaLootDrop droptable = StateManager.getInstance().getConfigurationManager()
							.getLootDrop(entry.getLootdropid());
					for (ISoliniaLootDropEntry dropentry : StateManager.getInstance().getConfigurationManager()
							.getLootDrop(droptable.getId()).getEntries()) {
						if (dropentry.isAlways() == true) {
							absoluteitems.add(dropentry);
							continue;
						}

						rollitems.add(dropentry);
					}
				}

				// Now we have prepared our loot list items let's choose which will
				// drop

				System.out.println(
						"Prepared a Loot List of ABS: " + absoluteitems.size() + " and ROLL: " + rollitems.size());

				if (absoluteitems.size() == 0 && rollitems.size() == 0)
					return;

				int dropcount = StateManager.getInstance().getWorldPerkDropCountModifier();

				Random r = new Random();
				int randomInt = r.nextInt(100) + 1;

				if (rollitems.size() > 0) {
					// Based on the chance attempt to drop this item
					for (int i = 0; i < dropcount; i++) {
						ISoliniaLootDropEntry droptableentry = rollitems.get(new Random().nextInt(rollitems.size()));
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(droptableentry.getItemid());

						randomInt = r.nextInt(100) + 1;
						System.out.println("Rolled a " + randomInt + " against a max of " + droptableentry.getChance()
								+ " for item: " + item.getDisplayname());
						if (randomInt <= droptableentry.getChance()) {
							getBukkitLivingEntity().getLocation().getWorld()
									.dropItem(getBukkitLivingEntity().getLocation(), item.asItemStack());
						}
					}
				}

				// Always drop these items
				if (absoluteitems.size() > 0) {
					for (int i = 0; i < absoluteitems.size(); i++) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(absoluteitems.get(i).getItemid());
						for (int c = 0; c < absoluteitems.get(i).getCount(); c++) {
							getBukkitLivingEntity().getLocation().getWorld()
									.dropItem(getBukkitLivingEntity().getLocation(), item.asItemStack());
						}
					}
				}
			} else {
				/*
				 * This is no longer needed now we have loot drops int itemDropMinimum = 95; if
				 * (Utils.RandomChance(itemDropMinimum)) { if (getBukkitLivingEntity()
				 * instanceof Monster)
				 * getBukkitLivingEntity().getWorld().dropItem(this.getBukkitLivingEntity().
				 * getLocation(),SoliniaItemFactory.GenerateRandomLoot().asItemStack()); }
				 */
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getNpcid() {
		return npcid;
	}

	@Override
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}

	@Override
	public boolean isPlayer() {
		if (this.getBukkitLivingEntity() == null)
			return false;

		if (this.getBukkitLivingEntity() instanceof Player)
			return true;

		return false;
	}

	@Override
	public void emote(String message) {
		StateManager.getInstance().getChannelManager().sendToLocalChannel(this, message);
	}

	@Override
	public void doRandomChat() {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getRandomchatTriggerText() == null || npc.getRandomchatTriggerText().equals(""))
				return;

			// 2% chance of saying something
			int random = Utils.RandomBetween(1, 100);
			if (random < 2) {
				this.emote(ChatColor.AQUA + npc.getName() + " says '" + npc.getRandomchatTriggerText() + "'"
						+ ChatColor.RESET);
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void say(String message) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return;

			this.emote(ChatColor.AQUA + npc.getName() + " says '" + message + "'" + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void say(String message, LivingEntity messageto) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return;

			this.emote(ChatColor.AQUA + npc.getName() + " says to " + messageto.getName() + " '" + message + "'"
					+ ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSlayChat() {
		if (isPlayer())
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getKillTriggerText() == null || npc.getKillTriggerText().equals(""))
				return;

			this.emote(ChatColor.AQUA + npc.getName() + " says '" + npc.getKillTriggerText() + "'" + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSpellCast(Plugin plugin, LivingEntity castingAtEntity) {
		if (isPlayer())
			return;

		this.setMana(this.getMana() + 1);

		if (castingAtEntity == null || this.livingentity == null)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc.getClassid() < 1)
				return;

			List<ISoliniaSpell> spells = StateManager.getInstance().getConfigurationManager()
					.getSpellsByClassIdAndMaxLevel(npc.getClassid(), npc.getLevel());
			if (spells.size() == 0)
				return;

			List<ISoliniaSpell> hostileSpells = new ArrayList<ISoliniaSpell>();
			List<ISoliniaSpell> beneficialSpells = new ArrayList<ISoliniaSpell>();

			for (ISoliniaSpell spell : spells) {
				if (!spell.isBeneficial()) {
					if (!Utils.isInvalidNpcSpell(spell) && (Utils.getSpellTargetType(spell.getTargettype())
							.equals(SpellTargetType.AETarget)
							|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AECaster)
							|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
							|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional)))
						hostileSpells.add(spell);
					continue;
				}

				if (!Utils.isInvalidNpcSpell(spell) && (Utils.getSpellTargetType(spell.getTargettype())
						.equals(SpellTargetType.AECaster)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Group)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.AETarget)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target)
						|| Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional)))
					beneficialSpells.add(spell);
			}

			int chanceToCastBeneficial = Utils.RandomBetween(1, 10);

			boolean success = false;
			if (chanceToCastBeneficial > 7) {
				if (beneficialSpells.size() == 0)
					return;

				// Cast on self
				ISoliniaSpell spellToCast = Utils.getRandomItemFromList(beneficialSpells);

				if (getMana() > spellToCast.getMana()) {
					success = spellToCast.tryApplyOnEntity(plugin, this.livingentity, this.livingentity);
				}

				if (success) {
					this.setMana(this.getMana() - spellToCast.getMana());
				}
			} else {
				if (hostileSpells.size() == 0)
					return;

				ISoliniaSpell spellToCast = Utils.getRandomItemFromList(hostileSpells);

				if (getMana() > spellToCast.getMana()) {
					if (Utils.getSpellTargetType(spellToCast.getTargettype()).equals(SpellTargetType.AETarget)
							|| Utils.getSpellTargetType(spellToCast.getTargettype()).equals(SpellTargetType.AECaster)) {
						if (Utils.getSpellTargetType(spellToCast.getTargettype()).equals(SpellTargetType.AETarget)) {
							for (Entity e : castingAtEntity.getNearbyEntities(10, 10, 10)) {
								if (!(e instanceof Player))
									continue;

								boolean loopSuccess = spellToCast.tryApplyOnEntity(plugin, this.livingentity,
										(Player) e);
								if (loopSuccess == true)
									success = true;
							}
						}

						if (Utils.getSpellTargetType(spellToCast.getTargettype()).equals(SpellTargetType.AECaster)) {
							for (Entity e : getBukkitLivingEntity().getNearbyEntities(10, 10, 10)) {
								if (!(e instanceof Player))
									continue;

								boolean loopSuccess = spellToCast.tryApplyOnEntity(plugin, this.livingentity,
										(Player) e);
								if (loopSuccess == true)
									success = true;
							}
						}

					} else {
						success = spellToCast.tryApplyOnEntity(plugin, this.livingentity, castingAtEntity);
					}
				}

				if (success) {
					this.setMana(this.getMana() - spellToCast.getMana());
				}
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMana(int amount) {
		if (isPlayer())
			return;

		if (this.getNpcid() < 1)
			return;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return;
			StateManager.getInstance().getEntityManager().setNPCMana(this.getBukkitLivingEntity(), npc, amount);
		} catch (CoreStateInitException e) {
			return;
		}

	}

	@Override
	public Integer getMana() {
		if (isPlayer()) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				return solPlayer.getMana();
			} catch (CoreStateInitException e) {
				return 0;
			}
		}

		if (this.getNpcid() < 1)
			return 0;

		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (npc == null)
				return 0;
			return StateManager.getInstance().getEntityManager().getNPCMana(this.getBukkitLivingEntity(), npc);
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	@Override
	public int getResistsFromActiveEffects(SpellResistType type) {
		int total = 0;
		SpellEffectType seekSpellEffectType = Utils.getSpellEffectTypeFromResistType(type);

		if (seekSpellEffectType != null) {
			try {
				SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells(getBukkitLivingEntity());

				for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
					for (ActiveSpellEffect spelleffect : activeSpell.getActiveSpellEffects()) {
						if (spelleffect.getSpellEffectType().equals(SpellEffectType.ResistAll)
								|| spelleffect.getSpellEffectType().equals(seekSpellEffectType)) {
							total += spelleffect.getCalculatedValue();
						}
					}
				}
			} catch (CoreStateInitException e) {
				// skip over
			}
		}

		return total;
	}

	@Override
	public int getResists(SpellResistType type) {
		if (isPlayer()) {
			try {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getResist(type);
			} catch (CoreStateInitException e) {
				return 25;
			}
		} else {
			return 25 + getResistsFromActiveEffects(type);
		}
	}

	@Override
	public void processInteractionEvent(LivingEntity triggerentity, InteractionType type, String data) {
		if (this.getNpcid() > 0) {
			ISoliniaNPC npc;
			try {
				npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				npc.processInteractionEvent(this, triggerentity, type, data);
			} catch (CoreStateInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getProcChancePct() {
		// (Dexterity / 35) / 25
		int dexterity = 75;
		if (this.getBukkitLivingEntity() instanceof Player) {
			try {
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) this.getBukkitLivingEntity());
				if (player != null)
					dexterity = getDexterity();
			} catch (CoreStateInitException e) {

			}
		}

		if (this.getNpcid() > 0)
			dexterity = getDexterity();

		float dexdiv = (dexterity / 35);
		float fina = ((dexdiv / 25) * 100);
		return (int) Math.floor(fina);
	}

	@Override
	public int getMaxDamage() {
		return Utils.getMaxDamage(getLevel(), getStrength());
	}

	@Override
	public double getMaxHP() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		double statHp = Utils.getStatMaxHP(getClassObj(), getLevel(), getStamina());
		double totalHp = statHp;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return totalHp;

				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());

				if (npc.isBoss()) {
					totalHp += (200 * npc.getLevel());
				}

				return totalHp;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return totalHp;

				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());
				// get AA hp bonus
				totalHp += Utils.getTotalAAEffectMaxHp(this.getBukkitLivingEntity());

				return totalHp;
			}
		} catch (CoreStateInitException e) {
			return totalHp;
		}

		return totalHp;
	}

	@Override
	public boolean getDodgeCheck() {

		if (getNpcid() < 1 && !isPlayer())
			return false;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				boolean result = npc.getDodgeCheck();

				return result;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				boolean result = solplayer.getDodgeCheck();

				return solplayer.getDodgeCheck();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public int getStrength() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				
				if (stat > getMaxStat("STRENGTH"))
					stat = getMaxStat("STRENGTH");
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStrength();

				stat += Utils.getTotalItemStat(solplayer, "STRENGTH");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				
				if (stat > getMaxStat("STRENGTH"))
					stat = getMaxStat("STRENGTH");

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}
	
	@Override
	public boolean isNPC()
	{
		if (isPlayer())
			return false;
		
		if (getNpcid() < 1)
		{
			return false;
		}
		
		if (getNpcid() > 0) {
			try
			{
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
			if (npc == null)
				return false;
			
			} catch (CoreStateInitException e)
			{
				return false;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public int getStamina() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");

				if (stat > getMaxStat("STAMINA"))
					stat = getMaxStat("STAMINA");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStamina();

				stat += Utils.getTotalItemStat(solplayer, "STAMINA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				
				if (stat > getMaxStat("STAMINA"))
					stat = getMaxStat("STAMINA");

				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getAgility() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");

				if (stat > getMaxStat("AGILITY"))
					stat = getMaxStat("AGILITY");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getAgility();

				stat += Utils.getTotalItemStat(solplayer, "AGILITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "AGILITY");

				if (stat > getMaxStat("AGILITY"))
					stat = getMaxStat("AGILITY");
				
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getDexterity() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");

				if (stat > getMaxStat("DEXTERITY"))
					stat = getMaxStat("DEXTERITY");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getDexterity();

				stat += Utils.getTotalItemStat(solplayer, "DEXTERITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");

				if (stat > getMaxStat("DEXTERITY"))
					stat = getMaxStat("DEXTERITY");
				
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getIntelligence() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");

				if (stat > getMaxStat("INTELLIGENCE"))
					stat = getMaxStat("INTELLIGENCE");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getIntelligence();

				stat += Utils.getTotalItemStat(solplayer, "INTELLIGENCE");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");

				if (stat > getMaxStat("INTELLIGENCE"))
					stat = getMaxStat("INTELLIGENCE");
				
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getWisdom() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");

				if (stat > getMaxStat("WISDOM"))
					stat = getMaxStat("WISDOM");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getWisdom();

				stat += Utils.getTotalItemStat(solplayer, "WISDOM");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "WISDOM");

				if (stat > getMaxStat("WISDOM"))
					stat = getMaxStat("WISDOM");
				
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getCharisma() {

		if (getNpcid() < 1 && !isPlayer())
			return 1;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;

				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");

				if (stat > getMaxStat("CHARISMA"))
					stat = getMaxStat("CHARISMA");
				
				return stat;
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return 1;

				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getCharisma();

				stat += Utils.getTotalItemStat(solplayer, "CHARISMA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");
				stat += Utils.getTotalAAEffectStat(this.getBukkitLivingEntity(), "CHARISMA");

				if (stat > getMaxStat("CHARISMA"))
					stat = getMaxStat("CHARISMA");
				
				return stat;
			}
		} catch (CoreStateInitException e) {
			return 1;
		}

		return 1;
	}

	@Override
	public int getMaxStat(String skillname)
	{
		int baseMaxStat = 255;
		
		return baseMaxStat;
	}
	
	@Override
	public boolean getRiposteCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.getRiposteCheck();
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				return solplayer.getRiposteCheck();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public boolean getDoubleAttackCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;

		try {
			if (getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.getDoubleAttackCheck();
			}

			if (isPlayer()) {
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity());
				if (solplayer == null)
					return false;
				return solplayer.getDoubleAttackCheck();
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	@Override
	public int getMaxMP() {
		if (getClassObj() == null)
			return 1;

		String profession = getClassObj().getName().toUpperCase();

		int wisintagi = 0;
		if (Utils.getCasterClass(profession).equals("W"))
			wisintagi = getWisdom();
		if (Utils.getCasterClass(profession).equals("I"))
			wisintagi = getIntelligence();
		if (Utils.getCasterClass(profession).equals("N"))
			wisintagi = getAgility();

		double maxmana = ((850 * getLevel()) + (85 * wisintagi * getLevel())) / 425;
		if (this.getNpcid() > 0) {
			maxmana = maxmana + (50 * getLevel());

			try {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				if (npc != null) {
					if (npc.isBoss()) {
						maxmana += (200 * npc.getLevel());
					}
				}
			} catch (CoreStateInitException e) {

			}

		}
		return (int) Math.floor(maxmana);
	}

	@Override
	public ISoliniaClass getClassObj() {
		try {
			if (isPlayer()) {
				return SoliniaPlayerAdapter.Adapt((Player) getBukkitLivingEntity()).getClassObj();
			}

			if (this.getNpcid() > 0) {
				return StateManager.getInstance().getConfigurationManager().getNPC(getNpcid()).getClassObj();
			}
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}

	public void targetSelector()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final net.minecraft.server.v1_12_R1.EntityInsentient e = (net.minecraft.server.v1_12_R1.EntityInsentient) ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) getBukkitLivingEntity())
				.getHandle();
		if (!(e instanceof net.minecraft.server.v1_12_R1.EntityCreature)
				&& !(e instanceof net.minecraft.server.v1_12_R1.EntityTameableAnimal))
			return;

		final Field goalsField = net.minecraft.server.v1_12_R1.EntityInsentient.class
				.getDeclaredField("targetSelector");
		goalsField.setAccessible(true);

		final net.minecraft.server.v1_12_R1.PathfinderGoalSelector goals = (net.minecraft.server.v1_12_R1.PathfinderGoalSelector) goalsField
				.get(e);
		Field listField = net.minecraft.server.v1_12_R1.PathfinderGoalSelector.class.getDeclaredField("b");
		listField.setAccessible(true);
		Set list = (Set) listField.get(goals);
		list.clear();
		listField = net.minecraft.server.v1_12_R1.PathfinderGoalSelector.class.getDeclaredField("c");
		listField.setAccessible(true);
		list = (Set) listField.get(goals);
		list.clear();
		goals.a(1,
				(net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer) new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(
						e, (Class) net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));
		goals.a(2,
				(net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer) new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(
						e, (Class) net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));
		goals.a(10,
				(net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer) new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(
						e, (Class) net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));

		goals.a(1, new PathfinderGoalOwnerHurtByTarget((EntityTameableAnimal) e));
		goals.a(2, new PathfinderGoalOwnerHurtTarget((EntityTameableAnimal) e));
		goals.a(3, new PathfinderGoalHurtByTarget((EntityCreature) e, true, new Class[0]));
	}

	@Override
	public void configurePetGoals() {
		if (!isPet())
			return;

		System.out.println("Reconfiguring Pet Goals");

		try {
			targetSelector();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void doSummon(Plugin plugin, LivingEntity summoningEntity) {
		if (isPlayer())
			return;

		if (summoningEntity == null || this.livingentity == null)
			return;

		ISoliniaNPC npc;
		try {
			npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
			if (!npc.isSummoner())
				return;

			int chanceToSummon = Utils.RandomBetween(1, 10);

			if (chanceToSummon > 8) {
				if (summoningEntity instanceof Player) {
					this.say("You will not evade me " + ((Player) summoningEntity).getDisplayName() + "!");
				} else {
					this.say("You will not evade me " + summoningEntity.getName() + "!");

				}
				summoningEntity.teleport(getBukkitLivingEntity().getLocation());
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addToHateList(UUID uniqueId, int hate) {
		// TODO
	}

	@Override
	public DamageHitInfo avoidDamage(SoliniaLivingEntity attacker, DamageHitInfo hit) {
		ISoliniaLivingEntity defender = this;
		
		// TODO Block from rear check
		
		// TODO Parry Check

		if (getDodgeCheck()) 
		{
			hit.damage_done = 0;
			hit.avoided = true;
			hit.dodged = true;
			return hit;
		}
		
		if (getRiposteCheck())
		{
			hit.damage_done = 0;
			hit.riposted = true;
			hit.avoided = true;
			return hit;
		}
		
		// TODO Shield Block
		
		// TODO Two Hand Block

		hit.avoided = false;
		return hit;
	}

	@Override
	public boolean checkHitChance(SoliniaLivingEntity attacker, DamageHitInfo hit) {
		ISoliniaLivingEntity defender = this;

		
		if (defender.isPlayer())
		{
			try
			{
				ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)defender.getBukkitLivingEntity());
				if (player.isMeditating())
				{
					return true;
				}
			} catch (CoreStateInitException e)
			{
				// ignore it
			}
		}

		int avoidance = defender.getTotalDefense();

		int accuracy = hit.tohit;
		//if (accuracy == -1)
		//	return true;

		double hitRoll = Utils.RandomBetween(0,(int)Math.floor(accuracy));
		double avoidRoll = Utils.RandomBetween(0,(int)Math.floor(avoidance));

		// tie breaker? Don't want to be biased any one way
		return hitRoll > avoidRoll;
	}

	@Override
	public DamageHitInfo meleeMitigation(SoliniaLivingEntity attacker, DamageHitInfo hit) {
		if (hit.damage_done < 0 || hit.base_damage == 0)
			return hit;

		ISoliniaLivingEntity defender = this;
		int mitigation = defender.getMitigationAC();
		
		if (isPlayer() && attacker.isPlayer())
			mitigation = mitigation * 80 / 100; // PvP 

		int roll = (int) rollD20(hit.offense, mitigation);

		// +0.5 for rounding, min to 1 dmg
		hit.damage_done = Math.max((int)(roll * (double)(hit.base_damage) + 0.5), 1);

		return hit;
	}

	double rollD20(int offense, int mitigation)
	{
		double mods[] = {
			0.1, 0.2, 0.3, 0.4, 0.5,
			0.6, 0.7, 0.8, 0.9, 1.0,
			1.1, 1.2, 1.3, 1.4, 1.5,
			1.6, 1.7, 1.8, 1.9, 2.0
		};

		if (isPlayer() && isMeditating())
			return mods[19];

		int atk_roll = Utils.RandomBetween(0,offense + 5);
		int def_roll = Utils.RandomBetween(0,mitigation + 5);

		int avg = (offense + mitigation + 10) / 2;
		int index = Math.max(0, (atk_roll - def_roll) + (avg / 2));

		index = (int) Utils.clamp((index * 20) / avg, 0, 19);

		return mods[index];
	}
	
	private boolean isMeditating() {
		if (isPlayer())
		{
			ISoliniaPlayer player;
			try {
				player = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				return player.isMeditating();
			} catch (CoreStateInitException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public int getMitigationAC() {
		return 15;
	}

	@Override
	public int getSkillDmgTaken(String skill) {
		int skilldmg_mod = 0;

		if(skilldmg_mod < -100)
			skilldmg_mod = -100;

		return skilldmg_mod;
	}

	@Override
	public int getFcDamageAmtIncoming(SoliniaLivingEntity caster, int spell_id, boolean use_skill, String skill) {
		//Used to check focus derived from SE_FcDamageAmtIncoming which adds direct damage to Spells or Skill based attacks.
		int dmg = 0;
		return dmg;
	}

	@Override
	public int getActSpellDamage(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect,ISoliniaLivingEntity target) {
		if (Utils.getSpellTargetType(soliniaSpell.getTargettype()).equals(SpellTargetType.Self))
			return value;
		
		if (getClassObj() == null)
			return value;

		boolean critical = false;
		int value_BaseEffect = 0;
		value_BaseEffect = value;
		int chance = 0;

		// TODO Focus effects
		
		// TODO Harm Touch Scaling

		chance = 0;
		
		// TODO take into account item,spell,aa bonuses
		if (isPlayer())
		{
			chance += Utils.getTotalAAEffectEffectType(getBukkitLivingEntity(),SpellEffectType.CriticalSpellChance);
		}
		
		// TODO Items/aabonuses
		if (chance > 0 || (getClassObj().getName().equals("WIZARD") && getLevel() >= 12)) {

			 int ratio = 100;

			// TODO Harm Touch
			 
			// TODO Crit Chance Overrides

			if (Utils.RandomBetween(0,100) < ratio) {
				critical = true;
				if (isPlayer())
				{
					ratio += Utils.getTotalAAEffectEffectType(getBukkitLivingEntity(),SpellEffectType.SpellCritDmgIncrease);
				}
				// TODO add ratio bonuses from spells, aas
			}
			else if (getClassObj().getName().equals("WIZARD")) {
				if ((getLevel() >= 12) && Utils.RandomBetween(0,100) < 7)
				{
					ratio += Utils.RandomBetween(20,70);
					critical = true;
				}
			}

			if (critical){

				value = value_BaseEffect*ratio/100;

				// TODO Vulnerabilities

				// TODO spell dmg level restriction
				// TODO NPC Spell Scale

				if (isPlayer())
				{
					getBukkitLivingEntity().sendMessage("* You critical blast for " + value);
				}

				return value;
			}
		}
		
		//Non Crtical Hit Calculation pathway
		value = value_BaseEffect;

		// TODO Vulnerabilities

		// TODO SPell damage lvl restriction
		// TODO NPC Spell Scale

		return value;
	}

	@Override
	public int getActSpellHealing(ISoliniaSpell soliniaSpell, int value, SpellEffect spellEffect, ISoliniaLivingEntity target) {
		if (getClassObj() == null)
			return value;
		
		//int value_BaseEffect = value;
		int chance = 0;
		int modifier = 1;
		boolean critical = false;

		// TODO FOcus effects
		//value_BaseEffect = value;

		// TODO FOcus effects

		// Instant Heals
		if(soliniaSpell.getBuffduration() < 1) {
			
			// TODO Items/aabonuses
			if (isPlayer())
			{
				chance += Utils.getTotalAAEffectEffectType(getBukkitLivingEntity(),SpellEffectType.CriticalHealChance);
			}
			
			// TODO FOcuses

			if(chance > -1 && (Utils.RandomBetween(0,100) < chance)) {
				critical = true;
				modifier = 2;
			}
			
			value *= modifier;

			// TODO No heal items

			// TODO NPC Heal Scale

			if (critical) {
				if (isPlayer())
					getBukkitLivingEntity().sendMessage("* You perform an exception heal for " + value);
			}

			return value;
		} else {
			//Heal over time spells. [Heal Rate and Additional Healing effects do not increase this value]
			// TODO Item bonuses
			
			// TODO FOcuses

			// TOOD Spell Bonuses

			if(chance > -1 && Utils.RandomBetween(0,100) < chance)
				value *= 2;
		}

		// TODO Npc Heal Scale

		return value;
	}

	@Override
	public int getRune()
	{
		return Utils.getActiveSpellEffectsRemainingValue(this.getBukkitLivingEntity(), SpellEffectType.Rune);
	}
	
	@Override
	public int reduceAndRemoveRunesAndReturnLeftover(int damage)
	{
		int dmgLeft = damage;
		List<Integer> removeSpells = new ArrayList<Integer>();
		try
		{
			for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager().getActiveEntitySpells(getBukkitLivingEntity()).getActiveSpells()) {
				if (!spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Rune))
						continue;
				
				for (ActiveSpellEffect effect : spell.getActiveSpellEffects())
				{
					if (!(effect.getSpellEffectType().equals(SpellEffectType.Rune)))
						continue;
					
					if (dmgLeft <= 0)
						break;
					
					if (effect.getRemainingValue() <= dmgLeft)
					{
						dmgLeft -= effect.getRemainingValue();
						effect.setRemainingValue(0);
						removeSpells.add(spell.getSpellId());
					} else {
						effect.setRemainingValue(effect.getRemainingValue() - dmgLeft);
						dmgLeft = 0;
						break;
					}
				}
			}
			
			for (Integer spellId : removeSpells) {
				StateManager.getInstance().getEntityManager().removeSpellEffectsOfSpellId(getBukkitLivingEntity().getUniqueId(), spellId);
				
			}
		} catch (CoreStateInitException e) {
			// ignore and return full amount
		}
			
		return dmgLeft;
	}
}
