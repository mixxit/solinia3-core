package com.solinia.solinia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.solinia.solinia.Commands.CommandAddClass;
import com.solinia.solinia.Commands.CommandAddRace;
import com.solinia.solinia.Commands.CommandAddRaceClass;
import com.solinia.solinia.Commands.CommandCommit;
import com.solinia.solinia.Commands.CommandForename;
import com.solinia.solinia.Commands.CommandLastname;
import com.solinia.solinia.Commands.CommandMana;
import com.solinia.solinia.Commands.CommandSetClass;
import com.solinia.solinia.Commands.CommandSetRace;
import com.solinia.solinia.Commands.CommandSolinia;
import com.solinia.solinia.Commands.CommandStats;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Listeners.Solinia3CoreEntityListener;
import com.solinia.solinia.Listeners.Solinia3CorePlayerListener;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.EntityManager;
import com.solinia.solinia.Managers.PlayerManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Repositories.JsonClassRepository;
import com.solinia.solinia.Repositories.JsonPlayerRepository;
import com.solinia.solinia.Repositories.JsonRaceRepository;
import com.solinia.solinia.Timers.StateCommitTimer;

import net.milkbowl.vault.economy.Economy;

public class Solinia3CorePlugin extends JavaPlugin {
	
	private StateCommitTimer commitTimer;
	private Essentials essentials;
	private Economy economy;
	
	@Override
    public void onEnable() {
		System.out.println("[Solinia3Core] Plugin Enabled");
		createConfigDir();
		initialise();
		registerEvents();
		
		setupEconomy();
		setupEssentials();
		
		StateManager.getInstance().setEconomy(this.economy);
		StateManager.getInstance().setEssentials(this.essentials);
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
	
	private boolean setupEssentials()
	{
	    Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
	    if (essentialsPlugin.isEnabled() && (essentialsPlugin instanceof Essentials)) {
	            this.essentials = (Essentials) essentialsPlugin;
	            return true;
	    }
	    return false;
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
		economy = economyProvider.getProvider();
		return (economy != null);
    }
	
	private void initialise()
	{
		try {
			JsonPlayerRepository repo = new JsonPlayerRepository();
			repo.setJsonFile(getDataFolder() + "/" + "players.json");
			repo.reload();
			
			JsonRaceRepository racerepo = new JsonRaceRepository();
			racerepo.setJsonFile(getDataFolder() + "/" + "races.json");
			racerepo.reload();

			JsonClassRepository classrepo = new JsonClassRepository();
			classrepo.setJsonFile(getDataFolder() + "/" + "classes.json");
			classrepo.reload();
			
			PlayerManager playerManager = new PlayerManager(repo);
			EntityManager entityManager = new EntityManager();
			ConfigurationManager configurationManager = new ConfigurationManager(racerepo,classrepo);
			
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
		getServer().getPluginManager().registerEvents(new Solinia3CoreEntityListener(this), this);
		this.getCommand("solinia").setExecutor(new CommandSolinia());
		this.getCommand("commit").setExecutor(new CommandCommit());
		this.getCommand("forename").setExecutor(new CommandForename());
		this.getCommand("lastname").setExecutor(new CommandLastname());
		this.getCommand("mana").setExecutor(new CommandMana());
		this.getCommand("addrace").setExecutor(new CommandAddRace());
		this.getCommand("setrace").setExecutor(new CommandSetRace());
		this.getCommand("addclass").setExecutor(new CommandAddClass());
		this.getCommand("setclass").setExecutor(new CommandSetClass());
		this.getCommand("addraceclass").setExecutor(new CommandAddRaceClass());
		this.getCommand("stats").setExecutor(new CommandStats());
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
