package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerEquipmentTickEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerEquipmentTickTimer extends BukkitRunnable {
	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					continue;

				PlayerEquipmentTickEvent soliniaevent = new PlayerEquipmentTickEvent(solPlayer, solPlayer.getEquippedSoliniaItems());					
				Bukkit.getPluginManager().callEvent(soliniaevent);
				
			} catch (CoreStateInitException e)
			{
				
			}
		}
	}
}