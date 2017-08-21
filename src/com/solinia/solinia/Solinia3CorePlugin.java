package com.solinia.solinia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.solinia.solinia.Commands.CommandCommit;
import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Timers.StateCommitTimer;

public class Solinia3CorePlugin extends JavaPlugin {
	
	private StateCommitTimer _commitTimer;
	
	@Override
    public void onEnable() {
		System.out.println("[Solinia3Core] Plugin Enabled");
		Initialise();
		RegisterEvents();
	}
	
	@Override
    public void onDisable() {
		System.out.println("[Solinia3Core] Plugin Disabled");
    }
	
	public void Initialise()
	{
		try {
			StateManager.getInstance().Initialise(new PlayerManager());
			
			_commitTimer = new StateCommitTimer();
			_commitTimer.runTaskTimer(this, 100L, 5000L);
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void RegisterEvents()
	{
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerListener(this), this);
		this.getCommand("solinia").setExecutor(new CommandSolinia());
		this.getCommand("commit").setExecutor(new CommandCommit());
	}
}
