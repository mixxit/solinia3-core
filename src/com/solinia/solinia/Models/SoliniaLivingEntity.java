package com.solinia.solinia.Models;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
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
	public LivingEntity getBukkitLivingEntity() {
		// TODO Auto-generated method stub
		return this.livingentity;
	}

	@Override
	public void modifyDamageEvent(Plugin plugin, LivingEntity damager, EntityDamageByEntityEvent event) {
		LivingEntity attacker = damager;

		// Change attacker to archer
		if (event.getDamager() instanceof Arrow) {
			Arrow arr = (Arrow) event.getDamager();
			if (arr.getShooter() instanceof LivingEntity) {
				attacker = (LivingEntity) arr.getShooter();
			} else {
			}
		}

		if (attacker == null)
			return;

		if (attacker instanceof Player && getBukkitLivingEntity() instanceof Wolf) {
			if (isPet()) {
				Wolf wolf = (Wolf)getBukkitLivingEntity();
				if (wolf != null)
				{
					if (!wolf.getTarget().equals(attacker))
					{
						event.setCancelled(true);
						return;
					}
				} else {
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.getEntity() instanceof LivingEntity && event instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;

			if (getDodgeCheck()) {
				if (isPlayer()) {
					((Player)getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You dodge the attack!");
					try {
						SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity()).tryIncreaseSkill("DODGE", 1);
					} catch (CoreStateInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (damagecause.getDamager() instanceof Player) {
					((Player) damagecause.getDamager()).sendMessage(ChatColor.GRAY + "* " + getBukkitLivingEntity().getCustomName() + " dodges your attack!");
				}

				event.setCancelled(true);
				
				if (getRiposteCheck()) {
					ItemStack mainitem = getBukkitLivingEntity().getEquipment().getItemInMainHand();
					if (mainitem != null) {
						// returns the damage back to the player
						if(isPlayer())
						{
							((Player)getBukkitLivingEntity()).sendMessage(ChatColor.GRAY + "* You riposte the attack!");
							try {
								SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity()).tryIncreaseSkill("RIPOSTE", 1);
							} catch (CoreStateInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (damagecause.getDamager() instanceof Player) {
							((Player) damagecause.getDamager()).sendMessage(ChatColor.GRAY + "* " + getBukkitLivingEntity().getCustomName() + " ripostes your attack!");
							((Player) damagecause.getDamager()).damage(event.getDamage(), getBukkitLivingEntity());
						} else {
							attacker.damage(event.getDamage(), getBukkitLivingEntity());
						}
					}
				}
				
				return;
			}
		}

		// for action bar
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		
		// damage shield response
		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(getBukkitLivingEntity());

			if (effects != null && (!(event.getDamager() instanceof Arrow))) {
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
										((CraftEntity) getBukkitLivingEntity()).getHandle());
								source.setMagic();
								source.ignoresArmor();

								((CraftEntity) attacker).getHandle().damageEntity(source, spelleffect.getCalculatedValue() * -1);
								// attacker.damage(spelleffect.getBase() * -1);
								
								if (getBukkitLivingEntity() instanceof Player)
								{
									((Player)getBukkitLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Your damage shield hit " + attacker.getName() + " for " + df.format(spelleffect.getCalculatedValue() * -1) + "["+df.format(attacker.getHealth()-(spelleffect.getCalculatedValue() * -1))+"/"+df.format(attacker.getMaxHealth())+"]"));
								}
							}
						}
					}
				}
			}
		} catch (CoreStateInitException e) {
			return;
		}

		// WeaponProc Spell Effects
		try {
			// Check if attacker has any WeaponProc effects
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(attacker);

			if (effects != null && (!(event.getDamager() instanceof Arrow))) {
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
							int procChance = SoliniaLivingEntityAdapter.Adapt(attacker).getProcChancePct();
							int roll = Utils.RandomBetween(0, 100);

							if (roll < procChance) {
								boolean itemUseSuccess = procSpell.tryApplyOnEntity(plugin, attacker,
										this.getBukkitLivingEntity());

								if (procSpell.getMana() > 0)
									if (itemUseSuccess && (attacker instanceof LivingEntity)) {
										if (attacker instanceof Player)
										{
											SoliniaPlayerAdapter.Adapt((Player) attacker).reducePlayerMana(procSpell.getMana());
										}
									}
							}
						}
					}
				}
			}
		} catch (CoreStateInitException e) {

		}

		// Validate attackers weapon (player only)
		// Apply player skill damages
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			ISoliniaPlayer solplayer;
			try {
				solplayer = SoliniaPlayerAdapter.Adapt(player);
				
				// Apply Bane Damage
				if (isUndead())
				{
					ISoliniaItem attackerItem;
					try {
						attackerItem = SoliniaItemAdapter.Adapt(player.getInventory().getItemInMainHand());
						
						if (attackerItem != null)
						{
							if (attackerItem.getBaneUndead() > 0)
							{
								double newdmg = event.getDamage() + attackerItem.getBaneUndead();
								event.setDamage(DamageModifier.BASE, newdmg);
							}
						}
					} catch (SoliniaItemException e) {
						// do nothing
					}
				}
			} catch (CoreStateInitException e) {
				return;
			}

			if (player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.OXYGEN) > 999) {
				try {
					ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager()
							.getItem(player.getInventory().getItemInMainHand());
					if (soliniaitem != null) {
						if (soliniaitem.getAllowedClassNames().size() > 0) {
							if (solplayer.getClassObj() == null) {
								System.out.print("Player class was null");
								event.setCancelled(true);
								player.updateInventory();
								player.sendMessage(ChatColor.GRAY + "Your class cannot use this item");
								return;
							}

							if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName())) {
								event.setCancelled(true);
								player.updateInventory();
								player.sendMessage(ChatColor.GRAY + "Your class cannot use this item");
								return;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			double currentdamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE);
			
			if (currentdamage < 1) {
				currentdamage++;
			}

			if (event.getDamager() instanceof Arrow) {
				Arrow arr = (Arrow) event.getDamager();
				if (arr.getShooter() instanceof Player) 
				{
					try
					{
						// Apply archery modifier
						SoliniaPlayerSkill skill = solplayer.getSkill("ARCHERY");
						ISoliniaLivingEntity solplayerentity = SoliniaLivingEntityAdapter.Adapt(solplayer.getBukkitPlayer());
						double racestatbonus = solplayerentity.getDexterity() + skill.getValue();
						double bonus = racestatbonus / 100;
						double damagemlt = currentdamage * bonus;
						double newdmg = damagemlt;
						double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE);
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.ARMOR,
									event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.MAGIC,
									event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE,
									event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING,
									event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct);
						} catch (Exception e1) {
						}
	
						event.setDamage(DamageModifier.BASE, newdmg);
					}
					catch (CoreStateInitException e)
					{
						
					}
					
					if (attacker instanceof Player)
					{
						((Player)attacker).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You SHOT " + getBukkitLivingEntity().getName() + " for " + df.format(event.getDamage()) + " [" + df.format(getBukkitLivingEntity().getHealth()-event.getDamage()) + "/" + df.format(getBukkitLivingEntity().getMaxHealth()) + "]"));
					}
				}
			}

			// SLASHING
			if (event.getCause() == DamageCause.ENTITY_ATTACK) {
				Material materialinhand = player.getInventory().getItemInMainHand().getType();
				
				if (materialinhand.equals(Material.WOOD_SWORD) || materialinhand.equals(Material.WOOD_AXE)
						|| materialinhand.equals(Material.STONE_SWORD) || materialinhand.equals(Material.STONE_AXE)
						|| materialinhand.equals(Material.IRON_SWORD) || materialinhand.equals(Material.IRON_AXE)
						|| materialinhand.equals(Material.GOLD_SWORD) || materialinhand.equals(Material.GOLD_AXE)
						|| materialinhand.equals(Material.DIAMOND_SWORD)
						|| materialinhand.equals(Material.DIAMOND_AXE)) 
				{
					// Apply slashing modifier
					try
					{
						SoliniaPlayerSkill skill = solplayer.getSkill("SLASHING");
						ISoliniaLivingEntity solplayerentity = SoliniaLivingEntityAdapter.Adapt(solplayer.getBukkitPlayer());
						double racestatbonus = solplayerentity.getStrength() + skill.getValue();
						double bonus = racestatbonus / 100;
						double damagemlt = currentdamage * bonus;
						double newdmg = damagemlt;
						double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE);
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.ARMOR,
									event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.MAGIC,
									event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE,
									event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING,
									event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct);
						} catch (Exception e1) {
						}

						event.setDamage(DamageModifier.BASE, newdmg);
					
						ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(attacker);
						if (solle != null)
						if (solle.getDoubleAttackCheck()) {
							if(attacker instanceof Player)
							{
								((Player)attacker).sendMessage(ChatColor.GRAY + "* You double attack!");
								try {
									SoliniaPlayerAdapter.Adapt((Player)attacker).tryIncreaseSkill("DOUBLEATTACK", 1);
								} catch (CoreStateInitException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							getBukkitLivingEntity().damage(newdmg, attacker);
						}
						
						event.setDamage(DamageModifier.BASE, newdmg);

					} catch (CoreStateInitException e)
					{
						//
					}
					
					if (attacker instanceof Player)
					{
						((Player)attacker).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You SLASHED " + getBukkitLivingEntity().getName() + " for " + df.format(event.getDamage()) + " [" + df.format(getBukkitLivingEntity().getHealth()-event.getDamage()) + "/" + df.format(getBukkitLivingEntity().getMaxHealth()) + "]"));
					}

				}
			}
			
			// Everything else
			if (event.getCause() == DamageCause.ENTITY_ATTACK) {
				Material materialinhand = player.getInventory().getItemInMainHand().getType();
				if (materialinhand == null || materialinhand.equals(Material.STICK) || materialinhand.equals(Material.WOOD_SPADE)
						|| materialinhand.equals(Material.STONE_SPADE) || materialinhand.equals(Material.IRON_SPADE)
						|| materialinhand.equals(Material.GOLD_SPADE) || materialinhand.equals(Material.DIAMOND_SPADE)
						|| materialinhand.equals(Material.AIR) || materialinhand.equals(Material.WOOD_SWORD) || 
						materialinhand.equals(Material.WOOD_AXE)
						|| materialinhand.equals(Material.STONE_SWORD) || materialinhand.equals(Material.STONE_AXE)
						|| materialinhand.equals(Material.IRON_SWORD) || materialinhand.equals(Material.IRON_AXE)
						|| materialinhand.equals(Material.GOLD_SWORD) || materialinhand.equals(Material.GOLD_AXE)
						|| materialinhand.equals(Material.DIAMOND_SWORD)
						|| materialinhand.equals(Material.DIAMOND_AXE)
						|| event.getDamager() instanceof Arrow
						) 
				{
					// already handled in other areas
				} else {
					if (attacker instanceof Player)
					{
						((Player)attacker).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You hit " + getBukkitLivingEntity().getName() + " for " + df.format(event.getDamage()) + " [" + df.format(getBukkitLivingEntity().getHealth()-event.getDamage()) + "/" + df.format(getBukkitLivingEntity().getMaxHealth()) + "]"));
					}
				}
			}

			// CRUSHING
			if (event.getCause() == DamageCause.ENTITY_ATTACK) {
				Material materialinhand = player.getInventory().getItemInMainHand().getType();

				if (materialinhand == null || materialinhand.equals(Material.STICK) || materialinhand.equals(Material.WOOD_SPADE)
						|| materialinhand.equals(Material.STONE_SPADE) || materialinhand.equals(Material.IRON_SPADE)
						|| materialinhand.equals(Material.GOLD_SPADE) || materialinhand.equals(Material.DIAMOND_SPADE)
						|| materialinhand.equals(Material.AIR)) 
				{
					try
					{
						// Apply crushing modifier
						SoliniaPlayerSkill skill = solplayer.getSkill("CRUSHING");
						ISoliniaLivingEntity solplayerentity = SoliniaLivingEntityAdapter.Adapt(solplayer.getBukkitPlayer());
	
						double racestatbonus = solplayerentity.getStrength() + skill.getValue();
						double bonus = racestatbonus / 100;
						double damagemlt = currentdamage * bonus;
						double newdmg = damagemlt;
						double damagepct = newdmg / event.getDamage(EntityDamageEvent.DamageModifier.BASE);
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.ARMOR,
									event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.MAGIC,
									event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE,
									event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagepct);
						} catch (Exception e1) {
						}
						try {
							event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING,
									event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagepct);
						} catch (Exception e1) {
						}
	
						event.setDamage(DamageModifier.BASE, newdmg);
	
						ISoliniaLivingEntity solle = SoliniaLivingEntityAdapter.Adapt(attacker);
						if (solle != null)
						if (solle.getDoubleAttackCheck()) {
							if(attacker instanceof Player)
							{
								((Player)attacker).sendMessage(ChatColor.GRAY + "* You double attack!");
								try {
									SoliniaPlayerAdapter.Adapt((Player)attacker).tryIncreaseSkill("DOUBLEATTACK", 1);
								} catch (CoreStateInitException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							getBukkitLivingEntity().damage(newdmg, attacker);
						}

					event.setDamage(DamageModifier.BASE, newdmg);
					} catch (CoreStateInitException e)
					{
						//
					}
					
					if (attacker instanceof Player)
					{
						((Player)attacker).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You CRUSHED " + getBukkitLivingEntity().getName() + " for " + df.format(event.getDamage()) + " [" + df.format(getBukkitLivingEntity().getHealth()-event.getDamage()) + "/" + df.format(getBukkitLivingEntity().getMaxHealth()) + "]"));
					}
				}
			}
			
			SkillReward reward = Utils
					.getSkillForMaterial(player.getInventory().getItemInMainHand().getType().toString());
			if (reward != null) {
				solplayer.tryIncreaseSkill(reward.getSkillname(), reward.getXp());
			}
		}
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
					if (!Utils.isInvalidNpcSpell(spell) && (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Target) || Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.TargetOptional)))
						hostileSpells.add(spell);
					continue;
				}

				if (!Utils.isInvalidNpcSpell(spell) && (Utils.getSpellTargetType(spell.getTargettype()).equals(SpellTargetType.Self)
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
					success = spellToCast.tryApplyOnEntity(plugin, this.livingentity, castingAtEntity);
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
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return totalHp;
				
				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());
				
				if (npc.isBoss())
				{
					totalHp += (200 * npc.getLevel());
				}
				
				return totalHp;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return totalHp;

				totalHp += Utils.getTotalEffectTotalHP(this.getBukkitLivingEntity());
				
				return totalHp;
			}
		} catch (CoreStateInitException e)
		{
			return totalHp;
		}
		
		return totalHp;
	}


	@Override
	public boolean getDodgeCheck() {
		
		if (getNpcid() < 1 && !isPlayer())
			return false;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;
				
				boolean result = npc.getDodgeCheck();
				
				return result;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				boolean result = solplayer.getDodgeCheck();

				return solplayer.getDodgeCheck();
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
		
		return false;
	}
	
	@Override
	public int getStrength() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStrength();

				stat += Utils.getTotalItemStat(solplayer,"STRENGTH");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STRENGTH");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getStamina() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getStamina();

				stat += Utils.getTotalItemStat(solplayer,"STAMINA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "STAMINA");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getAgility() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getAgility();

				stat += Utils.getTotalItemStat(solplayer,"AGILITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "AGILITY");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getDexterity() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getDexterity();

				stat += Utils.getTotalItemStat(solplayer,"DEXTERITY");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "DEXTERITY");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getIntelligence() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getIntelligence();

				stat += Utils.getTotalItemStat(solplayer,"INTELLIGENCE");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "INTELLIGENCE");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getWisdom() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getWisdom();

				stat += Utils.getTotalItemStat(solplayer,"WISDOM");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "WISDOM");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public int getCharisma() {
		
		if (getNpcid() < 1 && !isPlayer())
			return 1;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return 1;
				
				int stat = npc.getLevel() * 5;
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");
				
				return stat;
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return 1;
				
				int stat = 1;

				if (solplayer.getRace() != null)
					stat += solplayer.getRace().getCharisma();

				stat += Utils.getTotalItemStat(solplayer,"CHARISMA");
				stat += Utils.getTotalEffectStat(this.getBukkitLivingEntity(), "CHARISMA");
				
				return stat;
			}
		} catch (CoreStateInitException e)
		{
			return 1;
		}
		
		return 1;
	}
	
	@Override
	public boolean getRiposteCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.getRiposteCheck();
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return false;

				return solplayer.getRiposteCheck();
			}
		} catch (CoreStateInitException e)
		{
			return false;
		}
		
		return false;
	}

	@Override
	public boolean getDoubleAttackCheck() {
		if (getNpcid() < 1 && !isPlayer())
			return false;
		
		try
		{
			if (getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(getNpcid());
				if (npc == null)
					return false;

				return npc.getDoubleAttackCheck();
			}
			
			if (isPlayer())
			{
				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity());
				if (solplayer == null)
					return false;
				return solplayer.getDoubleAttackCheck();
			}
		} catch (CoreStateInitException e)
		{
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
		if (this.getNpcid() > 0)
		{
			maxmana = maxmana + (50 * getLevel());
			
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(this.getNpcid());
				if (npc != null)
				{
					if (npc.isBoss())
					{
						maxmana += (200 * npc.getLevel());
					}
				}
			} catch (CoreStateInitException e)
			{
				
			}
			
		}
		return (int) Math.floor(maxmana);
	}

	@Override
	public ISoliniaClass getClassObj() 
	{
		try
		{
			if (isPlayer())
			{
				return SoliniaPlayerAdapter.Adapt((Player)getBukkitLivingEntity()).getClassObj();
			}
			
			if (this.getNpcid() > 0)
			{
				return StateManager.getInstance().getConfigurationManager().getNPC(getNpcid()).getClassObj();
			}
		} catch (CoreStateInitException e)
		{
			return null;
		}
		return null;
	}
	
	public void targetSelector() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		final net.minecraft.server.v1_12_R1.EntityInsentient e = (net.minecraft.server.v1_12_R1.EntityInsentient)((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity)getBukkitLivingEntity()).getHandle();
		if (!(e instanceof net.minecraft.server.v1_12_R1.EntityCreature) && !(e instanceof net.minecraft.server.v1_12_R1.EntityTameableAnimal))
			return;
		
        final Field goalsField = net.minecraft.server.v1_12_R1.EntityInsentient.class.getDeclaredField("targetSelector");
        goalsField.setAccessible(true);
        
        final net.minecraft.server.v1_12_R1.PathfinderGoalSelector goals = (net.minecraft.server.v1_12_R1.PathfinderGoalSelector)goalsField.get(e);
        Field listField = net.minecraft.server.v1_12_R1.PathfinderGoalSelector.class.getDeclaredField("b");
        listField.setAccessible(true);
        Set list = (Set)listField.get(goals);
        list.clear();
        listField = net.minecraft.server.v1_12_R1.PathfinderGoalSelector.class.getDeclaredField("c");
        listField.setAccessible(true);
        list = (Set)listField.get(goals);
        list.clear();
        goals.a(1, (net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer)new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(e, (Class)net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));
        goals.a(2, (net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer)new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(e, (Class)net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));
        goals.a(10, (net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer)new net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer(e, (Class)net.minecraft.server.v1_12_R1.EntityHuman.class, 5.0f, 1.0f));
        
        		
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
				if (summoningEntity instanceof Player)
				{
					this.say("You will not evade me " + ((Player)summoningEntity).getDisplayName() + "!");
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
}
