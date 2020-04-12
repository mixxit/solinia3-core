package com.solinia.solinia.Listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaLivingEntityPassiveEffectTickEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.FactionStandingEntry;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.RaycastUtils;
import com.solinia.solinia.Utils.Utils;

import net.minecraft.server.v1_14_R1.Tuple;

public class Solinia3CoreEntityListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreEntityListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler()
	public void onBlockDispenseArmorEvent(BlockDispenseArmorEvent  event)
	{
		if (event.isCancelled())
			return;
		
		// No
		Utils.CancelEvent(event);
	}
	
	// Needs to occur before anything else
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTargetEvent(EntityTargetEvent event) {
		if (event.isCancelled())
			return;
		
		if (event.getEntity() instanceof ArmorStand || event.getTarget() instanceof ArmorStand)
		{
			Utils.CancelEvent(event);
			return;
		}

		
		if (event.getTarget() != null && event.getEntity() != null && event.getEntity() instanceof Creature && event.getEntity() instanceof LivingEntity)
		{
			try
			{
			if (!StateManager.getInstance().getEntityManager().hasHate(event.getEntity().getUniqueId())) {
				//PathfinderGoalTarget
				// TODO why is this happening? We already cleared their target and last target - why is it setting it back?
				Utils.CancelEvent(event);
				return;
			}	
			} catch (CoreStateInitException e)
			{
				
			}
		}
		
		if (event.getTarget() != null && event.getTarget().isInvulnerable() || event.getTarget().isDead())
		{
			Utils.CancelEvent(event);
			return;
		}

		if (event.getEntity() == null || event.getTarget() == null)
			return;

		if (event.getEntity().isDead() || event.getTarget().isDead())
			return;

		if (!(event.getEntity() instanceof Creature))
			return;
		
		if (event.getTarget() instanceof Player)
			if (((Player)event.getTarget()).getGameMode() != GameMode.SURVIVAL)
			{
				Utils.CancelEvent(event);
				return;
			}

		try {
			// Pets dont need line of sight to set their target
			
			if (event.getEntity() instanceof LivingEntity)
			{
				ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntity());
				if (!solentity.isCurrentlyNPCPet())
				if (StateManager.getInstance().getEntityManager().getHateListEntry(event.getEntity().getUniqueId(),event.getTarget().getUniqueId()).a() < 1)
					if (!RaycastUtils.isEntityInLineOfSightCone((LivingEntity) event.getEntity(), event.getTarget(), 90,
							Utils.MAX_ENTITY_AGGRORANGE)) {
						Utils.CancelEvent(event);
						return;
					}
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

		if (EntityUtils.isMezzed((LivingEntity)event.getEntity()))
		{
			Utils.CancelEvent(event);
			return;
		}

		if (EntityUtils.isStunned((LivingEntity)event.getEntity()))
		{
			Utils.CancelEvent(event);
			return;
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

			if (!solEntity.isUndead() && !solEntity.hasSpellEffectType(SpellEffectType.SeeInvis) && !(event.getEntity() instanceof Player) && !solEntity.isAnimal()
					&& event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),
						SpellEffectType.Invisibility)
						|| StateManager.getInstance().getEntityManager()
								.hasEntityEffectType((LivingEntity) event.getTarget(), SpellEffectType.Invisibility2)
						|| ((LivingEntity) event.getTarget()).hasPotionEffect(PotionEffectType.INVISIBILITY)
						) {
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
				ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getTarget());
				if(soliniaLivingEntity != null && soliniaLivingEntity.isSneaking())
				{
					Utils.CancelEvent(event);
					return;
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
			
			if (event.getEntity() instanceof LivingEntity && event.getEntity() != null && event.getTarget() != null) {
				ISoliniaLivingEntity soliniaLivingEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntity());
				if (soliniaLivingEntity.isCurrentlyNPCPet())
				{
					if (soliniaLivingEntity.getOwnerEntity().getUniqueId().equals(event.getTarget().getUniqueId()))
					{
						// Cancel owner attack
						solEntity.setAttackTarget(null);
						Utils.CancelEvent(event);
					}
				}
			}

		} catch (CoreStateInitException e) {
			return;
		}
		
	}
	
	@EventHandler
	public void onPassiveEffectTickEvent(SoliniaLivingEntityPassiveEffectTickEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getSoliniaLivingEntity() == null)
			return;
		
		if (event.getSoliniaLivingEntity().getBukkitLivingEntity().isDead())
			return;
		
		if (event.getSoliniaLivingEntity().getRace() != null)
			if (event.getSoliniaLivingEntity().getRace().getPassiveAbilityId() > 0)
				event.getSoliniaLivingEntity().tryApplySpellOnSelf(event.getSoliniaLivingEntity().getRace().getPassiveAbilityId(), "");

		if (event.getSoliniaLivingEntity().getGod() != null)
			if (event.getSoliniaLivingEntity().getGod().getPassiveAbilityId() > 0)
				event.getSoliniaLivingEntity().tryApplySpellOnSelf(event.getSoliniaLivingEntity().getGod().getPassiveAbilityId(), "");
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
								
								// Set max HP
								solEntity.updateMaxHp();

								if (solEntity.doCheckForDespawn()) {
									Utils.RemoveEntity(entity,"ONCREATURESPAWN");
									return;
								}
							} catch (CoreStateInitException e) {

							}
						}

						// if this is a skeleton entity, remove the chase task frmo the mobs AI
						org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity cle = ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity) entity);

						if (cle.getHandle() == null)
							return;

						if (cle.getHandle().getAttributeInstance(
								net.minecraft.server.v1_14_R1.GenericAttributes.FOLLOW_RANGE) == null)
							return;

						cle.getHandle()
								.getAttributeInstance(net.minecraft.server.v1_14_R1.GenericAttributes.FOLLOW_RANGE)
								.setValue(Utils.MAX_ENTITY_AGGRORANGE);
					}
				}, 100L);
	}
	@EventHandler
    public void ArmAnimationEvent(PlayerAnimationEvent anim) {
        
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		
		if (event.isCancelled())
			return;

		if (event.getEntity() instanceof ArmorStand)
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
		
		// This is a hack we use to hide arrows bouncing off an enemy when the event is cancelled below
		EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)event;
		// INFO: Just so you know, we are not guaranteed to have a living entity for this event

		if (entityDamageByEntityEvent.getDamager() instanceof Projectile)
		{
			Projectile entity = (Projectile)entityDamageByEntityEvent.getDamager();
			entity.remove();
		}
		
		
		// WE ARE NO LONGER USING THE CLASSIC ENTITY DAMAGE BY ENTITY SYSTEM
		
		if (event instanceof EntityDamageByEntityEvent) {
			Utils.CancelEvent(event);
		}
		
		if (!(entityDamageByEntityEvent.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof LivingEntity))
			return;

		// HOWEVER, PLAYER STILL LIKE MANUALLY ATTACKING SO WE WILL TRIGGER
		// AUTO ATTACK

		// If they have no target, target the entity
		try {
			ISoliniaLivingEntity defender = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntity());
			if (defender.getBukkitLivingEntity() == null || defender.getBukkitLivingEntity().isDead())
				return;
			
			ISoliniaLivingEntity damager = SoliniaLivingEntityAdapter.Adapt((Player)(entityDamageByEntityEvent.getDamager()));
			if (damager == null || damager.getBukkitLivingEntity().isDead())
				return;
			
			damager.setEntityTarget(defender.getBukkitLivingEntity());
			damager.processAutoAttack(true);
			
		} catch (CoreStateInitException e) {
		}
		
	}

	private float getLavaDamageEffectiveness(Player victim) {
		try
		{
			// TODO Auto-generated method stub
			int victimlevel = SoliniaPlayerAdapter.Adapt((Player) victim).getLevel();
			int targetresist = SoliniaPlayerAdapter.Adapt((Player) victim).getResist(SpellResistType.RESIST_FIRE);
	
			int resist_chance = 0;
	
			resist_chance += targetresist;
	
			if (resist_chance > 255) {
				resist_chance = 255;
			}
	
			if (resist_chance < 0) {
				resist_chance = 0;
			}
	
			int roll = Utils.RandomBetween(0, 200);
	
			if (roll > resist_chance) {
				return 100;
			} else {
				if (resist_chance < 1) {
					resist_chance = 1;
				}
	
				int partial_modifier = ((150 * (resist_chance - roll)) / resist_chance);
	
				if (partial_modifier <= 0) {
					return 100F;
				} else if (partial_modifier >= 100) {
					return 0;
				}
	
				return (100.0f - partial_modifier);
			}
		} catch (CoreStateInitException e)
		{
			return 100f;
		}
	}

	private void onEntityLavaDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		LivingEntity le = (LivingEntity) event.getEntity();
		if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
			return;

		// 40 points of damage
		double damage = 40;
		damage = damage * (getLavaDamageEffectiveness((Player)le) / 100);
		if (damage > 0)
		{
			if (damage > Integer.MAX_VALUE)
				damage = Integer.MAX_VALUE;
			
			int finalDamage = (int)Math.round(damage);
			event.setDamage(finalDamage);
			le.sendMessage(ChatColor.GRAY + "* You have been hit for " + finalDamage + " points of LAVA damage!");
			
			if (le instanceof Player) {
				try {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) le);
					PartyWindowUtils.UpdateGroupWindowForEveryone(le.getUniqueId(),
							solPlayer.getGroup(), false);
				} catch (CoreStateInitException e) {

				}
			}
		}
		
	}

	private void onEntityDrowningDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		LivingEntity le = (LivingEntity) event.getEntity();
		if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
			return;

		// 128 dmg per hit
		double drowningdamage = Math.round(le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/8D);
		event.setDamage(drowningdamage);
		le.sendMessage(ChatColor.GRAY + "* You have been hit for " + drowningdamage + " points of DROWNING damage!");
		
		if (le instanceof Player) {
			try {
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) le);
				PartyWindowUtils.UpdateGroupWindowForEveryone(le.getUniqueId(),
						solPlayer.getGroup(), false);
			} catch (CoreStateInitException e) {

			}
		}
	}

	private void onEntityFallDamageEvent(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player)) {
			LivingEntity le = (LivingEntity) event.getEntity();
			int finalDamage = 0;
			if (le.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
			{
				double damage = (le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 100) * (event.getDamage() * 3);
				if (damage > Integer.MAX_VALUE)
					damage = Integer.MAX_VALUE;
				
				finalDamage = (int)Math.round(damage);
				event.setDamage(finalDamage);
			}

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
							false, false);
					solplayer.tryIncreaseSkill(SkillType.SafeFall, 1);
					return;
				}
			} catch (CoreStateInitException e) {
				return;
			}
			
			le.sendMessage(ChatColor.GRAY + "* You have been hit for " + finalDamage + " points of FALL damage!");
			if (le instanceof Player) {
				try {
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player) le);
					PartyWindowUtils.UpdateGroupWindowForEveryone(le.getUniqueId(),
							solPlayer.getGroup(), false);
				} catch (CoreStateInitException e) {

				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(EntityInteractEvent event) {
		if (event.isCancelled())
			return;
		
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (EntityUtils.isMezzed(event.getPlayer()))
		{
			Utils.CancelEvent(event);
			return;
		}

		if (EntityUtils.isStunned(event.getPlayer()))
		{
			Utils.CancelEvent(event);
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
		
		if (!(event.getRightClicked() instanceof Creature))
			return;
	}
	
	@EventHandler
	public void onShootBow(EntityShootBowEvent event) {
		if (event.isCancelled())
			return;
		
		if (event.getEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity();
			{
				ItemStack seconditem = shooter.getInventory().getItemInOffHand();

				if (seconditem != null) {
					if (seconditem.getType() == Material.BOW || seconditem.getType() == Material.CROSSBOW
							|| seconditem.getType() == Material.LEGACY_BOW) {
						shooter.sendMessage("You cannot shoot while you have a bow in your offhand");
						Utils.CancelEvent(event);
						return;
					}
				}
			}
		}
		
		// This is done from auto attack now
		Utils.CancelEvent(event);
		
		// TODO Try Auto Attack here if timer is ok
		return;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (event.getEntity() instanceof ArmorStand) {
			return;
		}

		if (!(event.getEntity() instanceof LivingEntity))
			return;

		try {
			StateManager.getInstance().getEntityManager().forceClearTargetsAgainstMe((LivingEntity) event.getEntity());
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

		if (!(damager instanceof Player) && !soldamagerentity.isCurrentlyNPCPet())
			return;

		try {
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(event.getEntity());
			
			if ((livingEntity instanceof Player) || livingEntity.isCurrentlyNPCPet() || !livingEntity.isNPC())
				return;
			
			ISoliniaPlayer player = null;
			if (damager instanceof Player) {
				player = SoliniaPlayerAdapter.Adapt((Player) damager);
			}
			if (soldamagerentity.isCurrentlyNPCPet()) {
				if (soldamagerentity.getActiveMob() != null)
					player = SoliniaPlayerAdapter.Adapt((Player)soldamagerentity.getOwnerEntity());
			}
			if (player == null) {
				return;
			}

			Double experience = PlayerUtils.getExperienceRewardAverageForLevel(livingEntity.getLevel());

			// try to share with group
			ISoliniaGroup group = StateManager.getInstance().getGroupByMember(player.getOwnerUUID());
			if (group != null) {

				List<Integer> levelRanges = new ArrayList<Integer>();
				for (UUID member : group.getMembers()) {
					ISoliniaPlayer playerchecked = SoliniaPlayerAdapter.Adapt(Bukkit.getPlayer(member));
					int checkedlevel = playerchecked.getLevel();
					levelRanges.add(checkedlevel);
				}
				
				Tuple<Integer,Integer> lowhighlvl = Utils.GetGroupExpMinAndMaxLevel(levelRanges);
				int ilowlvl = lowhighlvl.a();
				int ihighlvl = lowhighlvl.b();

				if (player.getLevel() < ilowlvl || player.getLevel() > ihighlvl) {
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getLevel() >= Utils.getMinLevelFromLevel(player.getLevel())) {
						// Due to being out of range they get the full xp
						player.increasePlayerExperience(experience, true, true);
						if (player.getFellowship() != null)
							player.grantFellowshipXPBonusToFellowship(experience);

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

					} 

				} else {
					double experienceReward = experience / group.getMembers().size();
					double groupBonus = (experienceReward/100)*(group.getMembers().size()*10);
					
					List<Integer> awardsFellowshipIds = new ArrayList<Integer>();
					
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl || tgtlevel > ihighlvl) {
								tgtplayer.sendMessage(ChatColor.GRAY
										+ "You were out of level range to gain experience in this group (Min: "
										+ ilowlvl + " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(player.getBukkitPlayer().getWorld())) {
								// tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(player.getBukkitPlayer().getLocation()) <= Utils.MaxRangeForExperience) {
								if (livingEntity.getLevel() >= (Utils.getMinLevelFromLevel(tgtsolplayer.getLevel()))) {
									// they split the overall experience across the group size
									
									// add 10% bonus per player
									
									tgtsolplayer.increasePlayerExperience(experienceReward+groupBonus, true, true);
									
									if (player.getFellowship() != null)
									if (!awardsFellowshipIds.contains(player.getFellowship().getId()))
									{
										awardsFellowshipIds.add(player.getFellowship().getId());
										player.grantFellowshipXPBonusToFellowship(experience);
									}

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
									// The npc was too low level to gain
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
					// they are on their own so get the full amount of xp
					player.increasePlayerExperience(experience, true, true);
					
					if (player.getFellowship() != null)
					player.grantFellowshipXPBonusToFellowship(experience);

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

					// Dern wants this back
					 if (
							 //npc.isBoss() || 
							 npc.isRaidboss()) { PlayerUtils.BroadcastPlayers("[VICTORY] The foundations of the earth shake following the destruction of " + npc.getName() + " at the hands of " + player.getFullNameWithTitle() + "!");
					 }
					 
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
