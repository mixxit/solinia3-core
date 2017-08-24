package com.solinia.solinia;

import org.bukkit.plugin.java.JavaPlugin;

import com.solinia.solinia.Commands.CommandAddRace;
import com.solinia.solinia.Commands.CommandCommit;
import com.solinia.solinia.Commands.CommandForename;
import com.solinia.solinia.Commands.CommandLastname;
import com.solinia.solinia.Commands.CommandMana;
import com.solinia.solinia.Commands.CommandSetRace;
import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.EntityManager;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Repositories.JsonPlayerRepository;
import com.solinia.solinia.Repositories.JsonRaceRepository;
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
		try {
			StateManager.getInstance().Commit();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[Solinia3Core] Plugin Disabled");
    }
	
	private void initialise()
	{
		try {
			JsonPlayerRepository repo = new JsonPlayerRepository();
			repo.setJsonFile(getDataFolder() + "/" + "players.obj");
			repo.reload();
			
			JsonRaceRepository racerepo = new JsonRaceRepository();
			racerepo.setJsonFile(getDataFolder() + "/" + "races.obj");
			racerepo.reload();

			PlayerManager playerManager = new PlayerManager(repo);
			EntityManager entityManager = new EntityManager();
			ConfigurationManager configurationManager = new ConfigurationManager(racerepo);
			
			StateManager.getInstance().Initialise(playerManager, entityManager, configurationManager);
			
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
		this.getCommand("forename").setExecutor(new CommandForename());
		this.getCommand("lastname").setExecutor(new CommandLastname());
		this.getCommand("mana").setExecutor(new CommandMana());
		this.getCommand("addrace").setExecutor(new CommandAddRace());
		this.getCommand("setrace").setExecutor(new CommandSetRace());
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
