package com.solinia.solinia.Listeners;

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
			player.increasePlayerExperience(experience);
			player.giveMoney(1);
			livingEntity.dropLoot();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
