package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Events.PlayerHPRegenTickEvent;
import com.solinia.solinia.Events.PlayerMPRegenTickEvent;
import com.solinia.solinia.Managers.StateManager;

public class PlayerRegenTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		Plugin plugin = StateManager.getInstance().getPlugin();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				final PlayerHPRegenTickEvent soliniaevent = new PlayerHPRegenTickEvent(player.getUniqueId());
				
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(soliniaevent);
					}

				}.runTaskLater(plugin, 10);
				
				final PlayerMPRegenTickEvent soliniaevent2 = new PlayerMPRegenTickEvent(player.getUniqueId());
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(soliniaevent2);
					}

				}.runTaskLater(plugin, 10);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
