package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CastingTimer extends BukkitRunnable {
	@Override
	public void run() {

		try {
			StateManager.getInstance().getEntityManager().processCastingTimer();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}