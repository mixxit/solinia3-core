package com.solinia.solinia.Listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.FactionStandingEntry;
import com.solinia.solinia.Models.FactionStandingType;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.Utils;

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

		if (!(event.getEntity() instanceof Creature))
			return;
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are mezzed!");
				}
				event.setCancelled(true);
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}

		if (event.getEntity() instanceof Wolf && event.getTarget() instanceof Skeleton) {
			Wolf w = (Wolf) event.getEntity();
			if (w.getOwner() != null) {
				if (event.getTarget().getLastDamageCause() == null) {
					((Creature) event.getEntity()).setTarget(null);
					event.setCancelled(true);
					return;
				}

				// This is where wolves cancel their attacks against all skeletons
				// If the skeleton in question has hurt their master then it will set its target
				// as normal
				if (event.getTarget().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent dmgByEntity = (EntityDamageByEntityEvent) event.getTarget()
							.getLastDamageCause();
					if (dmgByEntity.getDamager() == null) {
						((Creature) event.getEntity()).setTarget(null);
						event.setCancelled(true);
						return;
					}

					Entity attacker = dmgByEntity.getDamager();

					if (dmgByEntity.getDamager() instanceof Arrow) {
						Arrow arr = (Arrow) dmgByEntity.getDamager();
						if (arr.getShooter() instanceof LivingEntity) {
							attacker = (LivingEntity) arr.getShooter();
						} else {
						}
					}

					if (!(attacker instanceof Player)) {
						((Creature) event.getEntity()).setTarget(null);
						event.setCancelled(true);
						return;
					}

					if ((event.getTarget().getLastDamageCause().getEntity() instanceof Player)
							&& !(w.getOwner().getUniqueId().equals(attacker.getUniqueId()))) {
						((Creature) event.getEntity()).setTarget(null);
						event.setCancelled(true);
						return;
					}
				}

			}
		}

		try {
			// Me
			ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
			if (solEntity.isUndead() && event.getTarget() instanceof LivingEntity) {
				if (StateManager.getInstance().getEntityManager().hasEntityEffectType((LivingEntity) event.getTarget(),
						SpellEffectType.InvisVsUndead)
						|| StateManager.getInstance().getEntityManager().hasEntityEffectType(
								(LivingEntity) event.getTarget(), SpellEffectType.InvisVsUndead2)) {
					((Creature) event.getEntity()).setTarget(null);
					event.setCancelled(true);
					return;
				}
			}

			// Mez cancel target
			Timestamp mezExpiry = StateManager.getInstance().getEntityManager()
					.getMezzed((LivingEntity) event.getEntity());

			if (mezExpiry != null) {
				((Creature) event.getEntity()).setTarget(null);
				event.setCancelled(true);
				return;
			}
		} catch (CoreStateInitException e) {
			return;
		}

	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof LivingEntity)
		{
			// if this is a skeleton entity, remove the chase task frmo the mobs AI
			event.getEntity().spigot();
		}
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if ((event.getEntity() instanceof Player)) {
			if (!(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)))
				return;

			Player player = (Player) event.getEntity();
			ISoliniaPlayer solplayer;
			try {
				solplayer = SoliniaPlayerAdapter.Adapt(player);
				if (solplayer == null)
					return;

				boolean cancelFall = solplayer.getSafefallCheck();
				if (cancelFall == true) {
					event.setCancelled(true);
					solplayer.emote(
							ChatColor.GRAY + "* " + solplayer.getFullName() + " lands softly, breaking their fall");
					solplayer.tryIncreaseSkill("SAFEFALL", 1);
					return;
				}
			} catch (CoreStateInitException e) {
				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}

		EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;

		
		// Remove buffs on attacker (invis should drop)
		// and check they are not mezzed
		try {
			if (damagecause.getDamager() instanceof LivingEntity) {
				
				LivingEntity attacker = (LivingEntity) damagecause.getDamager();
				// Change attacker to archer
				if (damagecause.getDamager() instanceof Arrow) {
					Arrow arr = (Arrow)attacker;
					if (arr.getShooter() instanceof LivingEntity) {
						attacker = (LivingEntity) arr.getShooter();
					} else {
					}
				}
				
				try
				{
					Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) attacker);
					if (mzExpiry != null)
					{
						if (attacker instanceof Player)
						{
							attacker.sendMessage("* You are mezzed!");
						}
						event.setCancelled(true);
						return;
					}
				} catch (CoreStateInitException e)
				{
					
				}
				
				List<Integer> removeSpells = new ArrayList<Integer>();
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells((LivingEntity) attacker).getActiveSpells()) {
					if (spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead) ||

							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsAnimals)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.ImprovedInvisAnimals)) {
						if (!removeSpells.contains(spell.getSpell().getId()))
							removeSpells.add(spell.getSpell().getId());

					}
				}

				for (Integer spellId : removeSpells) {
					StateManager.getInstance().getEntityManager()
							.removeSpellEffectsOfSpellId(((LivingEntity) attacker).getUniqueId(), spellId);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}
		
		

		// Remove buffs on recipient (invis should drop)
		try {
			if (event.getEntity() instanceof LivingEntity) {
				List<Integer> removeSpells = new ArrayList<Integer>();
				for (SoliniaActiveSpell spell : StateManager.getInstance().getEntityManager()
						.getActiveEntitySpells((LivingEntity) event.getEntity()).getActiveSpells()) {
					if (
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Mez) ||
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead) ||
							spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsUndead2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.Invisibility2)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.InvisVsAnimals)
							|| spell.getSpell().getSpellEffectTypes().contains(SpellEffectType.ImprovedInvisAnimals)) {
						if (!removeSpells.contains(spell.getSpell().getId()))
							removeSpells.add(spell.getSpell().getId());

					}
				}

				for (Integer spellId : removeSpells) {
					StateManager.getInstance().getEntityManager()
							.removeSpellEffectsOfSpellId(((LivingEntity) event.getEntity()).getUniqueId(), spellId);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		if (damagecause.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
			// Never forward magic spell damage (cause thorns) to melee damage calculation
			// code
			if (event.getCause().equals(DamageCause.THORNS))
				return;
			try {
				ISoliniaLivingEntity soliniaEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getEntity());
				soliniaEntity.modifyDamageEvent(this.plugin, (LivingEntity) damagecause.getDamager(), damagecause);
			} catch (CoreStateInitException e) {

			}
		}

	}

	@EventHandler
	public void onShootBow(EntityShootBowEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getEntity());
			if (mzExpiry != null)
			{
				if (event.getEntity() instanceof Player)
				{
					event.getEntity().sendMessage("* You are mezzed!");
				}
				event.setCancelled(true);
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}

		if (event.getEntity() instanceof Player) {
			Player shooter = (Player) event.getEntity();
			{
				ItemStack seconditem = shooter.getInventory().getItemInOffHand();

				if (seconditem != null) {
					if (seconditem.getType() == Material.BOW) {
						shooter.sendMessage("You cannot shoot while you have a bow in your offhand");
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		
		try
		{
			Timestamp mzExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getPlayer());
			if (mzExpiry != null)
			{
				if (event.getPlayer() instanceof Player)
				{
					event.getPlayer().sendMessage("* You are mezzed!");
				}
				event.setCancelled(true);
				return;
			}
		} catch (CoreStateInitException e)
		{
			
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
			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) event.getRightClicked());
			if (solentity.getNpcid() > 0) {
				SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(
						solentity.getBukkitLivingEntity().getUniqueId(),
						StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()));
				solentity.processInteractionEvent(event.getPlayer(), InteractionType.CHAT, "hail");
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
				if (damager instanceof Wolf) {
					Wolf w = (Wolf) damager;
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
				int ilowlvl = ihighlvl - 7;

				if (ilowlvl < 1) {
					ilowlvl = 1;
				}

				System.out.println("Group Min: " + ilowlvl + " Group Max: " + ihighlvl);

				if (player.getLevel() < ilowlvl) {
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getLevel() >= player.getLevel() - 7) {
						player.increasePlayerExperience(experience);
						
						// Grant title for killing mob
						if (livingEntity.getNpcid() > 0)
						{
							ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
							if (npc != null && !npc.getDeathGrantsTitle().equals(""))
							{
								player.grantTitle(npc.getDeathGrantsTitle());
							}
							
							if (npc.isBoss())
							{
								player.grantTitle("the Vanquisher");
							}
						}
						
					} else {
						player.getBukkitPlayer()
								.sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from");
					}

				} else {
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl) {
								tgtplayer.sendMessage(
										"You were out of level range to gain experience in this group (Min: " + ilowlvl
												+ " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(player.getBukkitPlayer().getWorld())) {
								tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(player.getBukkitPlayer().getLocation()) <= 100) {
								if (livingEntity.getLevel() >= (tgtsolplayer.getLevel() - 7)) {
									tgtsolplayer.increasePlayerExperience(experience);
									
									// Grant title for killing mob
									if (livingEntity.getNpcid() > 0)
									{
										ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
										if (npc != null && !npc.getDeathGrantsTitle().equals(""))
										{
											tgtsolplayer.grantTitle(npc.getDeathGrantsTitle());
										}
										
										if (npc.isBoss())
										{
											tgtsolplayer.grantTitle("the Vanquisher");
										}
									}
									
								} else {
									tgtplayer.sendMessage(
											ChatColor.GRAY + "* The npc was too low level to gain experience from");
								}

							} else {
								tgtplayer.sendMessage("You were out of range for shared group xp (distance)");
								continue;
							}
						}
					}
				}
			} else {
				if (livingEntity.getLevel() >= (player.getLevel() - 7)) {
					player.increasePlayerExperience(experience);
					
					// Grant title for killing mob
					if (livingEntity.getNpcid() > 0)
					{
						ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
						if (npc != null && !npc.getDeathGrantsTitle().equals(""))
						{
							player.grantTitle(npc.getDeathGrantsTitle());
						}
						
						if (npc.isBoss())
						{
							player.grantTitle("the Vanquisher");
						}
					}
					
				} else {
					player.getBukkitPlayer()
							.sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from");
				}
			}

			if (livingEntity.getNpcid() > 0) {
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc.getFactionid() > 0)
				{
					ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(npc.getFactionid());
					player.decreaseFactionStanding(npc.getFactionid(),1);
					for (FactionStandingEntry factionEntry : faction.getFactionEntries())
					{
						if (factionEntry.getValue() >= 1500)
						{
							// If this is an ally of the faction, grant negative faction
							player.decreaseFactionStanding(factionEntry.getFactionId(),1);
						}
						
						if (factionEntry.getValue() <= -1500)
						{
							// If this is an enemy of the faction, grant positive faction
							player.increaseFactionStanding(factionEntry.getFactionId(),1);
						}
					}
				}
			}
			
			if (livingEntity.getNpcid() > 0)
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(livingEntity.getNpcid());
				if (npc != null && !npc.getDeathGrantsTitle().equals(""))
				{
					player.grantTitle(npc.getDeathGrantsTitle());
				}
				
				if (npc.isBoss())
				{
					player.grantTitle("the Vanquisher");
				}
				
				if (npc.isBoss())
				{
					Bukkit.broadcastMessage(ChatColor.RED + "[VICTORY] The foundations of the earth shake following the destruction of " + npc.getName() + " at the hands of " + player.getFullNameWithTitle() + "!" + ChatColor.RESET);
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
