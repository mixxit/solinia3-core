package com.solinia.solinia.Timers;

import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class DiscordMessageTimer extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
		StateManager.getInstance().getChannelManager().processNextDiscordMessage();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
