package com.solinia.solinia.Listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.FactionStandingEntry;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Solinia3CoreEntityListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreEntityListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}

	// Needs to occur before anything else
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTargetEvent(EntityTargetEvent event) {
		if (event.isCancelled())
			return;

		if (event.getEntity() == null || event.getTarget() == null)
			return;

		if (event.getEntity().isDead() || event.getTarget().isDead())
			return;

		if (!(event.getEntity() instanceof Creature))
			return;

		try {
			if (event.getEntity() instanceof LivingEntity)
				if (StateManager.getInstance().getEntityManager().getHateListEntry(event.getEntity().getUniqueId(),
						event.getTarget().getUniqueId()) < 1)
					if (!Utils.isEntityInLineOfSightCone((LivingEntity) event.getEntity(), event.getTarget(), 90,
							Utils.MAX_ENTITY_AGGRORANGE)) {
						Utils.CancelEvent(event);
						return;
					}
		} catch (CoreStateInitException e) {

		}

		// cancel feigened if targetting
		try {
			boolean feigned = StateManager.getInstance().getEntityManager()
					.isFeignedDeath(event.getEntity().getUniqueId());
			if (feigned == true) {
				StateManager.getInstance().getEntityManager().setFeignedDeath(event.getEntity().getUniqueId(), false);
			}
		} catch (CoreStateInitException e) {

		}

		try {
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null) {
				if (event.getEntity() instanceof Player) {
					((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				}
				Utils.CancelEvent(event);
				return;
			}
		} catch (CoreStateInitException e) {

		}

		try {
			Timestamp stExpiry = StateManager.getInstance().getEntityManager()
					.getStunned((LivingEntity) event.getEntity());
			if (stExpiry != null) {
				if (event.getEntity() instanceof Player) {
					((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are stunned!"));
				}
				Utils.CancelEvent(event);
				return;
			}
		} catch (CoreStateInitException e) {

		}

		try {
			// Me
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
			if (solEntity.isUndead() && !(event.getEntity() instanceof Player)
					&& event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),
						SpellEffectType.InvisVsUndead)
						|| StateManager.getInstance().getEntityManager().hasEntityEffectType(
								(LivingEntity) event.getTarget(), SpellEffectType.InvisVsUndead2)) {
					solEntity.setAttackTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}

			if (!solEntity.isUndead() && !(event.getEntity() instanceof Player) && !solEntity.isAnimal()
					&& event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),
						SpellEffectType.Invisibility)
						|| StateManager.getInstance().getEntityManager()
								.hasEntityEffectType((LivingEntity) event.getTarget(), SpellEffectType.Invisibility2)) {
					solEntity.setAttackTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}

			if (solEntity.isAnimal() && !(event.getEntity() instanceof Player)
					&& event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),
						SpellEffectType.InvisVsAnimals)
						|| StateManager.getInstance().getEntityManager().hasEntityEffectType(
								(LivingEntity) event.getTarget(), SpellEffectType.ImprovedInvisAnimals)) {
					solEntity.setAttackTarget(null);
					Utils.CancelEvent(event);
					return;
				}
			}

			// rogue sneak
			if (event.getTarget() instanceof Player && !(event.getEntity() instanceof Player)) {
				Player targetPlayer = (Player) event.getTarget();
				if (targetPlayer.isSneaking()) {
					ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) event.getTarget());
					if (player.getClassObj() != null) {
						if (player.getClassObj().isSneakFromCrouch()) {
							Utils.CancelEvent(event);
							return;
						}
					}
				}
			}

			if (event.getEntity() != null && event.getTarget() != null) {
				if (!(event.getEntity() instanceof Player)) {
					// Mez cancel target
					Timestamp mezExpiry = StateManager.getInstance().getEntityManager()
							.getMezzed((LivingEntity) event.getTarget());

					if (mezExpiry != null) {
						solEntity.setAttackTarget(null);
						event.getEntity().sendMessage("The target is mezzed, you cannot hit it");
						Utils.CancelEvent(event);
						return;
					}
				}
			}

			if (event.getEntity() != null && event.getTarget() != null) {
				if (!(event.getEntity() instanceof Player)) {
					// Feigned death cancel target
					boolean feigned = StateManager.getInstance().getEntityManager()
							.isFeignedDeath(event.getTarget().getUniqueId());

					if (feigned == true) {
						solEntity.setAttackTarget(null);
						event.getEntity().sendMessage("The target is feigned, you cannot hit it");
						Utils.CancelEvent(event);
						return;
					}
				}
			}

		} catch (CoreStateInitException e) {
			return;
		}

	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof LivingEntity))
			return;

		if (event.isCancelled())
			return;

		final UUID entityUUID = event.getEntity().getUniqueId();

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Solinia3Core"),
				new Runnable() {
					public void run() {
						Entity entity = Bukkit.getEntity(entityUUID);
						if (entity == null)
							return;

						if (!(entity instanceof LivingEntity))
							return;

						if (entity.isDead())
							return;

						if (Utils.isLivingEntityNPC((LivingEntity) entity)) {
							try {
								ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter
										.Adapt((LivingEntity) entity);

								if (solEntity.doCheckForDespawn()) {
									entity.remove();
									return;
								}

								if (!solEntity.isPet()) {
									LivingEntity le = (LivingEntity) entity;
									String location = le.getLocation().getWorld().getName() + ","
											+ le.getLocation().getX() + "," + le.getLocation().getY() + ","
											+ le.getLocation().getZ();
									le.setMetadata("spawnpoint", new FixedMetadataValue(
											Bukkit.getPluginManager().getPlugin("Solinia3Core"), location));

								}

							} catch (CoreStateInitException e) {

							}
						}

						// if this is a skeleton entity, remove the chase task frmo the mobs AI
						org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity cle = ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity) entity);
						if (cle == null)
							return;

						if (cle.getHandle() == null)
							return;

						if (cle.getHandle().getAttributeInstance(
								net.minecraft.server.v1_13_R2.GenericAttributes.FOLLOW_RANGE) == null)
							return;

						cle.getHandle()
								.getAttributeInstance(net.minecraft.server.v1_13_R2.GenericAttributes.FOLLOW_RANGE)
								.setValue(Utils.MAX_ENTITY_AGGRORANGE);
					}
				});
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof LivingEntity))
			return;

		// Fall damage

		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
			onEntityFallDamageEvent(event);
		}
		
		if (event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
			onEntityDrowningDamageEvent(event);
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
			onEntityLavaDamageEvent(event);
		}
		
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}

		// Close range weapon
		EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;
		if (damagecause.getDamager() instanceof Player && (event.getEntity() instanceof LivingEntity)) {
			try {
				Player damager = (Player) damagecause.getDamager();
				ItemStack itemstack = damager.getEquipment().getItemInMainHand();
				if (itemstack != null && damager.isSneaking()
						&& ConfigurationManager.WeaponMaterials.contains(itemstack.getType().name())) {
					StateManager.getInstance().getEntityManager().setEntityTarget((LivingEntity) damager,
							(LivingEntity) event.getEntity());
					Utils.CancelEvent(event);
					return;
				}
			} catch (CoreStateInitException e) {

			}
		}

		// Close range spell/petcontrol rod
		if (damagecause.getDamager() instanceof Player && (event.getEntity() instanceof LivingEntity)) {
			try {
				Player damager = (Player) damagecause.getDamager();
				ItemStack itemstack = damager.getEquipment().getItemInMainHand();
				if (itemstack != null) {
					ISoliniaItem solItem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					if (damager.isSneaking() && solItem != null
							&& (solItem.isSpellscroll() || solItem.isPetControlRod())) {
						StateManager.getInstance().getEntityManager().setEntityTarget((LivingEntity) damager,
								(LivingEntity) event.getEntity());
						Utils.CancelEvent(event);
						return;
					}
				}
			} catch (CoreStateInitException e) {

			}
		}

		if (damagecause.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
			double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) damagecause.getDamager(),
					(LivingEntity) event.getEntity());
			if (distanceOverLimit > 0) {
				if (damagecause.getDamager() instanceof Player)
					damagecause.getDamager()
							.sendMessage("You were too far to cause damage [" + distanceOverLimit + " blocks too far]");

				Utils.CancelEvent(event);
				return;
			}
		}

		// Disable jumping crits for melee
		if (!event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)
				&& damagecause.getDamager() instanceof LivingEntity) {
			LivingEntity damager = (LivingEntity) damagecause.getDamager();
			boolean flag = damager.getFallDistance() > 0.0F && !damager.isOnGround();

			double f = damagecause.getDamage(DamageModifier.BASE);
			if (flag && f > 0.0D) {
				damagecause.setDamage(DamageModifier.BASE, f / 1.5D);
			}
		}

		if (damagecause.getDamager() instanceof Arrow) {
			ProjectileSource source = ((Arrow) damagecause.getDamager()).getShooter();
			if (source instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
				double distanceOverLimit = Utils.DistanceOverAggroLimit((LivingEntity) source,
						(LivingEntity) event.getEntity());

				if (distanceOverLimit > 0D) {
					if (source instanceof Player)
						((Player) source).sendMessage(
								"You were too far to cause damage [" + distanceOverLimit + " blocks too far]");

					Utils.CancelEvent(event);
					return;
				}
			}

			// cancel crit jump damage for bows
			if (!event.getCause().equals(EntityDamageEvent.DamageCause.THORNS) && source instanceof LivingEntity) {
				LivingEntity damager = (LivingEntity) source;
				boolean flag = damager.getFallDistance() > 0.0F && !damager.isOnGround();

				double f = damagecause.getDamage(DamageModifier.BASE);
				if (flag && f > 0.0D) {
					damagecause.setDamage(DamageModifier.BASE, f / 1.5D);
				}
			}
		}

		// Detect damage caused by entity collision response and cancel it
		// We will move all damage from NPCs to the NPC combat loop
		// This allows implementation of NPC Slow and Haste
		if (!(damagecause.getDamager() instanceof Player) && !(damagecause.getDamager() instanceof Arrow)
				&& event.getEntity() instanceof LivingEntity) {
			// TODO
			// ALWAYS CANCEL DAMAGE EVENTS THAT ARE NOT ENTITY SWEEP CAUSE

			if (!damagecause.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
				Utils.CancelEvent(event);
				return;
			}

			// event.setCancelled(true);
		}

		// Negate normal modifiers
		try {
			damagecause.setDamage(DamageModifier.ABSORPTION, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.ARMOR, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.BLOCKING, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.RESISTANCE, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.MAGIC, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.HARD_HAT, 0);
		} catch (UnsupportedOperationException e) {

		}
		try {
			damagecause.setDamage(DamageModifier.BLOCKING, 0);
		} catch (UnsupportedOperationException e) {

		}

		ISoliniaLivingEntity solLivingEntity;
		try {
			solLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
			int damage = solLivingEntity.calculateDamageFromDamageEvent(damagecause.getDamager(),
					event.getCause().equals(EntityDamageEvent.DamageCause.THORNS), (int) Math.floor(event.getDamage()));
			if (damage < 1) {
				Utils.CancelEvent(event);
				return;
			}
			event.setDamage(DamageModifier.BASE, damage);

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	private void onEntityLavaDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
			return;

		// 20% damage per hit
		event.setDamage((le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/100)*20);
	}

	private void onEntityDrowningDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
			return;

		// 10% damage per hit
		event.setDamage((le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/100)*10);
	}

	private void onEntityFallDamageEvent(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player)) {
			LivingEntity le = (LivingEntity)event.getEntity();
			if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
				event.setDamage((le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/100)*10);

			Player player = (Player) event.getEntity();
			ISoliniaPlayer solplayer;
			try {
				solplayer = SoliniaPlayerAdapter.Adapt(player);
				if (solplayer == null)
					return;

				boolean cancelFall = solplayer.getSafefallCheck();
				if (cancelFall == true) {
					Utils.CancelEvent(event);
					;
					solplayer.emote(
							ChatColor.AQUA + "* " + solplayer.getFullName() + " lands softly, breaking their fall",
							false);
					solplayer.tryIncreaseSkill("SAFEFALL", 1);
					return;
				}
			} catch (CoreStateInitException e) {
				return;
			}
		}
	}

	@EventHandler
	public void onShootBow(EntityShootBowEvent event) {
		if (event.isCancelled())
			return;

		try {
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null) {
				if (event.getEntity() instanceof Player) {
					((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				}
				Utils.CancelEvent(event);
				;
				return;
			}
		} catch (CoreStateInitException e) {

		}

		try {
			Timestamp stExpiry = StateManager.getInstance().getEntityManager()
					.getStunned((LivingEntity) event.getEntity());
			if (stExpiry != null) {
				if (event.getEntity() instanceof Player) {
					((Player) event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are stunned!"));
				}
				Utils.CancelEvent(event);
				;
				return;
			}
		} catch (CoreStateInitException e) {

		}

		if (event.getEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity();
			{
				ItemStack seconditem = shooter.getInventory().getItemInOffHand();

				if (seconditem != null) {
					if (seconditem.getType() == Material.BOW) {
						shooter.sendMessage("You cannot shoot while you have a bow in your offhand");
						Utils.CancelEvent(event);
						;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		try {
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) event.getPlayer());
			if (mzExpiry != null) {
				if (event.getPlayer() instanceof Player) {
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are mezzed!"));
				}
				Utils.CancelEvent(event);
				;
				return;
			}
		} catch (CoreStateInitException e) {

		}

		try {
			Timestamp stExpiry = StateManager.getInstance().getEntityManager()
					.getStunned((LivingEntity) event.getPlayer());
			if (stExpiry != null) {
				if (event.getPlayer() instanceof Player) {
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.GRAY + "* You are stunned!"));
				}
				Utils.CancelEvent(event);
				;
				return;
			}
		} catch (CoreStateInitException e) {

		}

		if (!(event.getRightClicked() instanceof LivingEntity)) {
			return;
		}

		if (!(event.getRightClicked() instanceof LivingEntity)) {
			return;
		}

		if (event.getRightClicked() instanceof Player) {
			return;
		}

		if (event.getHand() != EquipmentSlot.HAND || event.getRightClicked() == null) {
			return;
		}

		try {
			// if its in combat dont respond
			if (event.getRightClicked() instanceof Creature) {
				Creature le = (Creature) event.getRightClicked();
				if (le.getTarget() == null) {
					ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter
							.Adapt((LivingEntity) event.getRightClicked());

					if (solentity.getNpcid() > 0) {
						SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(
								solentity.getBukkitLivingEntity().getUniqueId(),
								StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()));
						solentity.processInteractionEvent(event.getPlayer(), InteractionType.CHAT, "hail");
					}
				} else {
					// too spammy, move ot action bar
					event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY
							+ "* This npc will not respond to interactions while it is in combat (has a target)"));
				}
			} else {
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter
						.Adapt((LivingEntity) event.getRightClicked());
				if (solentity.getNpcid() > 0) {
					SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(
							solentity.getBukkitLivingEntity().getUniqueId(),
							StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()));
					solentity.processInteractionEvent(event.getPlayer(), InteractionType.CHAT, "hail");
				}
			}
		} catch (CoreStateInitException e) {
			e.printStackTrace();
			return;
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if ((event.getEntity() instanceof ArmorStand)) {
			return;
		}

		if (!(event.getEntity() instanceof LivingEntity))
			return;

		try {
			StateManager.getInstance().getEntityManager().clearTargetsAgainstMe((LivingEntity) event.getEntity());
		} catch (CoreStateInitException e) {

		}

		if (event.getEntity() instanceof Player)
			return;

		if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		if (event.getEntity() instanceof Animals && !Utils.isLivingEntityNPC((LivingEntity) event.getEntity()))
			return;

		EntityDamageByEntityEvent entitykiller = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
		Entity damager = entitykiller.getDamager();
		if (damager instanceof Projectile) {

			Projectile projectile = (Projectile) damager;
			damager = (Entity) projectile.getShooter();
		}

		if (!(damager instanceof LivingEntity))
			return;

		ISoliniaLivingEntity soldamagerentity = null;
		try {
			soldamagerentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) damager);
		} catch (CoreStateInitException e) {

		}

		// If damager is npc, have a chance to trigger its chat text for slaying
		// something
		if ((!(damager instanceof Player)) && Utils.isLivingEntityNPC((LivingEntity) damager)) {
			soldamagerentity.doSlayChat();
		}

		if (!(damager instanceof Player) && !soldamagerentity.isPet())
			return;

		try {
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(event.getEntity());
			ISoliniaPlayer player = null;
			if (damager instanceof Player) {
				player = SoliniaPlayerAdapter.Adapt((Player) damager);
			}
			if (soldamagerentity.isPet()) {
				if (damager instanceof Tameable) {
					Tameable w = (Tameable) damager;
					player = SoliniaPlayerAdapter.Adapt((Player) w.getOwner());
				}
			}
			if (player == null) {
				return;
			}

			Double experience = Utils.getExperienceRewardAverageForLevel(livingEntity.getLevel());

			// try to share with group
			ISoliniaGroup group = StateManager.getInstance().getGroupByMember(player.getUUID());
			if (group != null) {
				Integer dhighestlevel = 0;

				List<Integer> levelranges = new ArrayList<Integer>();

				for (UUID member : group.getMembers()) {
					ISoliniaPlayer playerchecked = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(member));
					int checkedlevel = playerchecked.getLevel();
					levelranges.add(checkedlevel);
				}

				Collections.sort(levelranges);

				// get the highest person in the group
				dhighestlevel = levelranges.get(levelranges.size() - 1);

				int ihighlvl = (int) Math.floor(dhighestlevel);
				int ilowlvl = Utils.getMinLevelFromLevel(ihighlvl);

				if (ilowlvl < 1) {
					ilowlvl = 1;
				}

				if (player.getLevel() < ilowlvl) {
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getLevel() >= Utils.getMinLevelFromLevel(player.getLevel())) {
						player.increasePlayerExperience(experience, true);

						// Grant title for killing mob
						if (livingEntity.getNpcid() > 0) {
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
									.getNPC(livingEntity.getNpcid());
							if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
								player.grantTitle(npc.getDeathGrantsTitle());
							}

							if (npc.isBoss() || npc.isRaidboss()) {
								player.grantTitle("the Vanquisher");
							}
						}

					} else {
						// player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low
						// level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " +
						// Utils.getMinLevelFromLevel(player.getLevel()));
					}

				} else {
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl) {
								tgtplayer.sendMessage(ChatColor.GRAY
										+ "You were out of level range to gain experience in this group (Min: "
										+ ilowlvl + " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(player.getBukkitPlayer().getWorld())) {
								// tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(player.getBukkitPlayer().getLocation()) <= 100) {
								if (livingEntity.getLevel() >= (Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()))) {
									tgtsolplayer.increasePlayerExperience(experience, true);

									// Grant title for killing mob
									if (livingEntity.getNpcid() > 0) {
										ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
												.getNPC(livingEntity.getNpcid());
										if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
											tgtsolplayer.grantTitle(npc.getDeathGrantsTitle());
										}

										if (npc.isBoss() || npc.isRaidboss()) {
											tgtsolplayer.grantTitle("the Vanquisher");
										}
									}

								} else {
									// tgtplayer.sendMessage(ChatColor.GRAY + "* The npc was too low level to gain
									// experience from - Was: " + livingEntity.getLevel() + " Min: " +
									// Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()));
								}

							} else {
								// tgtplayer.sendMessage(ChatColor.GRAY + "You were out of range for shared
								// group xp (distance)");
								continue;
							}
						}
					}
				}
			} else {
				if (livingEntity.getLevel() >= (Utils.getMinLevelFromLevel(player.getLevel()))) {
					player.increasePlayerExperience(experience, true);

					// Grant title for killing mob
					if (livingEntity.getNpcid() > 0) {
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager()
								.getNPC(livingEntity.getNpcid());
						if (npc != null && !npc.getDeathGrantsTitle().equals("")) {
							player.grantTitle(npc.getDeathGrantsTitle());
						}

						if (npc.isBoss() || npc.isRaidboss()) {
							player.grantTitle("the Vanquisher");
						}
					}

				} else {
					// player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low
					// level to gain experience from - Was: " + livingEntity.getLevel() + " Min: " +
					// Utils.getMinLevelFromLevel(player.getLevel()));
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc.getFactionid() > 0) {
					ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
							.getFaction(npc.getFactionid());
					player.decreaseFactionStanding(npc.getFactionid(), 50);
					for (FactionStandingEntry factionEntry : faction.getFactionEntries()) {
						if (factionEntry.getValue() >= 1500) {
							// If this is an ally of the faction, grant negative faction
							player.decreaseFactionStanding(factionEntry.getFactionId(), 10);
						}

						if (factionEntry.getValue() <= -1500) {
							// If this is an enemy of the faction, grant positive faction
							player.increaseFactionStanding(factionEntry.getFactionId(), 1);
						}
					}
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());

				if (npc != null) {
					if (npc.getChanceToRespawnOnDeath() > 0)
						if (Utils.RandomBetween(1, 100) <= npc.getChanceToRespawnOnDeath()) {
							npc.Spawn(player.getBukkitPlayer().getLocation(), 1);
						}

					if (!npc.getDeathGrantsTitle().equals("")) {
						player.grantTitle(npc.getDeathGrantsTitle());
					}

					if (npc.isBoss() || npc.isRaidboss()) {
						player.grantTitle("the Vanquisher");
					}

					/*
					 * if (npc.isBoss() || npc.isRaidboss()) { Utils.
					 * BroadcastPlayers("[VICTORY] The foundations of the earth shake following the destruction of "
					 * + npc.getName() + " at the hands of " + player.getFullNameWithTitle() + "!");
					 * }
					 */
				}
			}

			player.giveMoney(1);
			livingEntity.dropLoot();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
