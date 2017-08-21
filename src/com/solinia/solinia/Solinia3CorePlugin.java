package com.solinia.solinia;

import org.bukkit.plugin.java.JavaPlugin;

import com.solinia.solinia.Commands.CommandCommit;
import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Timers.StateCommitTimer;

public class Solinia3CorePlugin extends JavaPlugin {
	
	private StateCommitTimer commitTimer;
	
	@Override
    public void onEnable() {
		System.out.println("[Solinia3Core] Plugin Enabled");
		createConfigDir();
		initialise();
		registerEvents();
	}
	
	@Override
    public void onDisable() {
		System.out.println("[Solinia3Core] Plugin Disabled");
    }
	
	private void initialise()
	{
		try {
			PlayerManager playerManager = new PlayerManager();
			playerManager.setRepository(new ObjectStreamPlayerRepository(getDataFolder() + "/" + "players.obj"));
			
			StateManager.getInstance().Initialise(playerManager);
			
			commitTimer = new StateCommitTimer();
			commitTimer.runTaskTimer(this, 100L, 5000L);
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void registerEvents()
	{
		getServer().getPluginManager().registerEvents(new Solinia3CorePlayerListener(this), this);
		this.getCommand("solinia").setExecutor(new CommandSolinia());
		this.getCommand("commit").setExecutor(new CommandCommit());
	}
	
	private void createConfigDir() {
	    try {
	        if (!getDataFolder().exists()) {
	            getDataFolder().mkdirs();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();

	    }
	}
}
