package com.solinia.solinia.Timers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;


public class PerkLoadTimer extends BukkitRunnable {
	@Override
	public void run() {
		StateManager.getInstance().reloadPerks();
	}
}
