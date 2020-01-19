package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Events.SoliniaLivingEntityPassiveEffectTickEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Utils.Utils;

public class SoliniaLivingEntityPassiveEffectTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			runPassiveEffectTimer();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void runPassiveEffectTimer()
	{
		List<String> completedEntities = new ArrayList<String>();
		
		// Check each player and check entities near player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Player first
			processLivingEntityPassiveEffects(player);
			
			try
			{
				// Then nearby npcs
				for(Entity entity : player.getNearbyEntities(25, 25, 25))
				{
					if (entity instanceof Player)
						continue;
					
					if (!(entity instanceof LivingEntity))
						continue;
					
					LivingEntity livingEntity = (LivingEntity)entity;
					
					if(entity.isDead())
						continue;
					
					if (!Utils.isLivingEntityNPC(livingEntity))
						continue;
					
					if (completedEntities.contains(livingEntity.getUniqueId().toString()))
						continue;
					
					completedEntities.add(livingEntity.getUniqueId().toString());
					processLivingEntityPassiveEffects(livingEntity);
				}
			
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processLivingEntityPassiveEffects(LivingEntity livingEntity) {
		try
		{
			if (livingEntity.isDead())
				return;
			
			ISoliniaLivingEntity solLivingEntity = SoliniaLivingEntityAdapter.Adapt(livingEntity);
			if (solLivingEntity == null)
				return;
			
			SoliniaLivingEntityPassiveEffectTickEvent soliniaevent = new SoliniaLivingEntityPassiveEffectTickEvent(solLivingEntity);
			Bukkit.getPluginManager().callEvent(soliniaevent);
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
