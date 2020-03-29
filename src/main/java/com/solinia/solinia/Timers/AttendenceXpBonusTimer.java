package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class AttendenceXpBonusTimer extends BukkitRunnable {
	@Override
	public void run() {

		try {
			StateManager.getInstance().getPlayerManager().grantPlayerAttendenceBonus();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
