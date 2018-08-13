package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CSVGenerationTimer extends BukkitRunnable {
	@Override
	public void run() {

		try {
			StateManager.getInstance().getConfigurationManager().commitCsvs();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
