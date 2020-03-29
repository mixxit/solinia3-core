package com.solinia.solinia.Timers;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Managers.StateManager;

public class SpellTickTimer extends BukkitRunnable {
	Plugin plugin;
	public SpellTickTimer(Plugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {

		try
		{
		StateManager.getInstance().spellTick(plugin);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
