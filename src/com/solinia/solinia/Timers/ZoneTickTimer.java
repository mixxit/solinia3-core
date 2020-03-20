package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Events.ZoneTickEvent;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class ZoneTickTimer extends BukkitRunnable {

	@Override
	public void run() {
		try
		{
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (!zone.isPlayersInZone())
					continue;
				
				ZoneTickEvent soliniaevent = new ZoneTickEvent(zone.getId());
				Bukkit.getPluginManager().callEvent(soliniaevent);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
