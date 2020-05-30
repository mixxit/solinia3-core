package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Events.SoliniaLivingEntityHPRegenTickEvent;
import com.solinia.solinia.Events.SoliniaLivingEntityMPRegenTickEvent;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.EntityUtils;

public class SoliniaLivingEntityRegenTickTimer extends BukkitRunnable {
	Plugin plugin;
	public SoliniaLivingEntityRegenTickTimer(Plugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		generateTickEvents();
		
	}

	private void generateTickEvents() {
		List<String> completedEntities = new ArrayList<String>();
		
		// Check each player and check entities near player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				// Player first
				ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(player);
				if (solLivingEntity != null)
					generateTickEvent(solLivingEntity);

				
				// Then nearby npcs
				for(Entity entityThatWillRegen : player.getNearbyEntities(25, 25, 25))
				{
					if (entityThatWillRegen instanceof Player)
						continue;
					
					if (!(entityThatWillRegen instanceof LivingEntity))
						continue;
					
					LivingEntity livingEntityThatWillRegen = (LivingEntity)entityThatWillRegen;
					
					if (!(entityThatWillRegen instanceof Creature))
						continue;
					
					if(entityThatWillRegen.isDead())
						continue;
					
					if (!EntityUtils.isLivingEntityNPC(livingEntityThatWillRegen))
						continue;
					
					if (completedEntities.contains(livingEntityThatWillRegen.getUniqueId().toString()))
						continue;
					
					completedEntities.add(livingEntityThatWillRegen.getUniqueId().toString());
					ISoliniaLivingEntity solLe = SoliniaLivingEntityAdapter.Adapt(livingEntityThatWillRegen);
					if (solLe == null)
						continue;
					
					generateTickEvent(solLe);
				}
			
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void generateTickEvent(ISoliniaLivingEntity solLivingEntity) {
		if (solLivingEntity == null || solLivingEntity.getBukkitLivingEntity() == null)
			return;
		
		UUID uniqueId = solLivingEntity.getBukkitLivingEntity().getUniqueId();
		
		final SoliniaLivingEntityHPRegenTickEvent soliniaevent = new SoliniaLivingEntityHPRegenTickEvent(uniqueId);
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(soliniaevent);
			}

		}.runTaskLater(plugin, 10);
		
		final SoliniaLivingEntityMPRegenTickEvent soliniaevent2 = new SoliniaLivingEntityMPRegenTickEvent(uniqueId);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(soliniaevent2);
			}

		}.runTaskLater(plugin, 10);
	}
}
