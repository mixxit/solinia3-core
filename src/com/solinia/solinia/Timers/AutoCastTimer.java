package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class AutoCastTimer extends BukkitRunnable {
	@Override
	public void run() {

		try {
			StateManager.getInstance().getPlayerManager().doAutoCastTimers();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
