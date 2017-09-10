package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;


public class PerkLoadTimer extends BukkitRunnable {
	@Override
	public void run() {
		StateManager.getInstance().reloadPerks();
	}
}
