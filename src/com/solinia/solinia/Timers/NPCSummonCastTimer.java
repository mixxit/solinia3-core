package com.solinia.solinia.Timers;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class NPCSummonCastTimer extends BukkitRunnable {
	Plugin plugin;
	public NPCSummonCastTimer(Plugin plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void run() {

		try {
			StateManager.getInstance().getEntityManager().doNPCSummon();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
