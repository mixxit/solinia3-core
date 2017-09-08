package com.solinia.solinia.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class Solinia3CoreEntityListener implements Listener {
	Solinia3CorePlugin plugin;
	
	public Solinia3CoreEntityListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent event) {
		if (event.isCancelled()) 
			return;
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.isCancelled()) 
			return;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) 
			return;
		
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		
		EntityDamageByEntityEvent damagecause = (EntityDamageByEntityEvent) event;
		
		if (damagecause.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			
			try {
				ISoliniaLivingEntity soliniaEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getEntity());
				soliniaEntity.modifyDamageEvent(SoliniaPlayerAdapter.Adapt((Player)damagecause.getDamager()), damagecause);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	

	@EventHandler
	public void onShootBow(EntityShootBowEvent event)
	{
		if (event.isCancelled()) 
			return;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		
		if (!(event.getRightClicked() instanceof LivingEntity))
		{
			return;
		}
		
        if(!(event.getRightClicked() instanceof LivingEntity))
        {
        	return;
        }
        
        if(event.getRightClicked() instanceof Player)
        {
        	return;
        }
        
        if (event.getHand() != EquipmentSlot.HAND || event.getRightClicked() == null)
		{
			return;
		}
        
        try
        {
	        ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)event.getRightClicked());
			if (solentity.getNpcid() > 0)
			{
				SoliniaPlayerAdapter.Adapt(event.getPlayer()).setInteraction(solentity.getBukkitLivingEntity().getUniqueId());
				if (StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()).getMerchantid() > 0)
				{
					StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid()).sendMerchantItemListToPlayer(event.getPlayer());
				}
			}
        } catch (CoreStateInitException e)
        {
        	e.printStackTrace();
        	return;
        }
    }

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		// TODO - Temporary reward
		if (!(event.getEntity() instanceof Monster))
			return;
		
		if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent entitykiller = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
		Entity damager = entitykiller.getDamager();
		if (damager instanceof Projectile) {

			Projectile projectile = (Projectile) damager;
			damager = (Entity) projectile.getShooter();
		}
		
		if (!(damager instanceof LivingEntity))
			return;
		
		if (!(damager instanceof Player))
			return;
		
		try {
			ISoliniaLivingEntity livingEntity = SoliniaLivingEntityAdapter.Adapt(event.getEntity());
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)damager);
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
				
				if (player.getLevel() < ilowlvl)
				{
					// Only award player the experience
					// as they are out of range of the group
					if (livingEntity.getLevel() >= player.getLevel() - 7) {
						player.increasePlayerExperience(experience);
					} else {
						player.getBukkitPlayer().sendMessage(ChatColor.GRAY
								+ "* The npc was too low level to gain experience from");
					}
					
				} else {
					for (UUID member : group.getMembers()) {
						Player tgtplayer = Bukkit.getPlayer(member);
						if (tgtplayer != null) {
							ISoliniaPlayer tgtsolplayer = SoliniaPlayerAdapter.Adapt(tgtplayer);
							int tgtlevel = tgtsolplayer.getLevel();

							if (tgtlevel < ilowlvl) {
								System.out.println(tgtplayer.getName() + " did not get XP due to out of range");
								tgtplayer.sendMessage(
										"You were out of level range to gain experience in this group (Min: "
												+ ilowlvl + " Max: " + ihighlvl + ")");
								continue;
							}

							if (!tgtplayer.getWorld().equals(player.getBukkitPlayer().getWorld())) {
								tgtplayer.sendMessage("You were out of range for shared group xp (world)");
								continue;
							}

							if (tgtplayer.getLocation().distance(player.getBukkitPlayer().getLocation()) <= 100) {
								if (livingEntity.getLevel() >= (tgtsolplayer.getLevel() - 7)) {
									tgtsolplayer.increasePlayerExperience(experience);
								} else {
									tgtplayer.sendMessage(ChatColor.GRAY
											+ "* The npc was too low level to gain experience from");
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
				} else {
					player.getBukkitPlayer().sendMessage(ChatColor.GRAY + "* The npc was too low level to gain experience from");
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
