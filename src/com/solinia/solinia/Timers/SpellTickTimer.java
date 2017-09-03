package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class SpellTickTimer extends BukkitRunnable {
	@Override
	public void run() {

		StateManager.getInstance().spellTick();
	}
}
