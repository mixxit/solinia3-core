package com.solinia.solinia.Timers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;


public class PlayerInteractionTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Check npc interactions
			UUID npcinteraction = SoliniaPlayerAdapter.Adapt(player).getInteraction();
			if (npcinteraction != null) {
				if (Bukkit.getEntity(npcinteraction) == null)
				{
					SoliniaPlayerAdapter.Adapt(player).setInteraction(null);
					continue;
				}
				
				if (player.getWorld().equals(Bukkit.getEntity(npcinteraction).getWorld())) {
					if (player.getLocation().distance(Bukkit.getEntity(npcinteraction).getLocation()) > 3) {
						SoliniaPlayerAdapter.Adapt(player).setInteraction(null);
						continue;
					}
				} else {
					SoliniaPlayerAdapter.Adapt(player).setInteraction(null);
					continue;
				}
			}
		}
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
	}
}
