package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerTrackEvent;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerTrackingTimer extends BukkitRunnable {

	@Override
	public void run() {

		Plugin plugin = Bukkit.getPluginManager().getPlugin("Solinia3Core");
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				if (player.isDead())
					continue;
				
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					continue;
				
				if (!solPlayer.isTracking())
					continue;
				
				
				final PlayerTrackEvent soliniaevent = new PlayerTrackEvent(player.getUniqueId(),solPlayer.getTrackingLocation());
				
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(soliniaevent);
					}

				}.runTaskLater(plugin, 10);
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
