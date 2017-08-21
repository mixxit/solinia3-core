package com.solinia.solinia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;

public class Solinia3CorePlugin extends JavaPlugin {
	@Override
    public void onEnable() {
		System.out.println("[Solinia3Core] Plugin Enabled");
		RegisterEvents();
	}
	
	@Override
    public void onDisable() {
		System.out.println("[Solinia3Core] Plugin Disabled");
    }
	
	public void RegisterEvents()
	{
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerListener(this), this);
		this.getCommand("solinia").setExecutor(new CommandSolinia());
	}
	
	public static Plugin getInstance()
	{
		return Bukkit.getPluginManager().getPlugin("Solinia3CorePlugin");
	}
}
