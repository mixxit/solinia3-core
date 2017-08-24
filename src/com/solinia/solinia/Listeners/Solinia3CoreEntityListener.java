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
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
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
	}
	
	

	@EventHandler
	public void onShootBow(EntityShootBowEvent event)
	{
		if (event.isCancelled()) 
			return;
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
			// TODO - Temporary Level 1 npcs everywhere
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)damager);
			Double experience = Utils.getExperienceRewardAverageForLevel(1);
			player.increasePlayerExperience(experience);
			player.giveMoney(1);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
