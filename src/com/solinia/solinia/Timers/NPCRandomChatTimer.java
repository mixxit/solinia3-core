package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class NPCRandomChatTimer extends BukkitRunnable {
	@Override
	public void run() {

		try {
			StateManager.getInstance().getEntityManager().doNPCRandomChat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
