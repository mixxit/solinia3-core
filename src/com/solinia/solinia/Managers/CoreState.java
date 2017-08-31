package com.solinia.solinia.Managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IChannelManager;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IEntityManager;
import com.solinia.solinia.Interfaces.IPlayerManager;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class CoreState {
	private boolean isInitialised = false;
	private IPlayerManager playerManager;
	private IEntityManager entityManager;
	private IConfigurationManager configurationManager;
	private Economy economy;
	private Essentials essentials;
	private IChannelManager channelManager;
	private ConcurrentHashMap<UUID, BossBar> bossbars = new ConcurrentHashMap<UUID, BossBar>();

	public CoreState()
	{
		isInitialised = false;
	}
	
	public BossBar getBossBar(UUID uuid) {
		return this.bossbars.get(uuid);
	}

	public void setBossBar(UUID uuid, BossBar bossbar) {
		// TODO Auto-generated method stub
		this.bossbars.put(uuid, bossbar);
	}
	
	public void setEconomy(Economy economy) {
		// TODO Auto-generated method stub
		this.economy = economy;
	}

	public void setEssentials(Essentials essentials) {
		// TODO Auto-generated method stub
		this.essentials = essentials;
	}
	
	public Economy getEconomy() {
		// TODO Auto-generated method stub
		return this.economy;
	}

	public Essentials getEssentials() {
		return this.essentials;
	}
	
	public void Initialise(IPlayerManager playerManager, IEntityManager entityManager, IConfigurationManager configurationManager, ChannelManager channelManager) throws CoreStateInitException
	{
		if (isInitialised == true)
			throw new CoreStateInitException("State already initialised");
		
		this.playerManager = playerManager;
		this.entityManager = entityManager;
		this.configurationManager = configurationManager;
		this.channelManager = channelManager;
		isInitialised = true;
	}
	
	public IPlayerManager getPlayerManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return playerManager;
	}
	
	public IConfigurationManager getConfigurationManager() throws CoreStateInitException
	{
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return configurationManager;
	}

	public void Commit() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		System.out.println("Commit");
		try {
			playerManager.commit();
			configurationManager.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public IEntityManager getEntityManager() throws CoreStateInitException {
		if (isInitialised == false)
			throw new CoreStateInitException("State not initialised");
		
		return entityManager;
	}

	public double getWorldPerkXPModifier() {
		// TODO - Replace with lookups
		return 100;
	}

	public void giveEssentialsMoney(Player player, int amount) {
		if (getEssentials() == null || getEconomy() == null)
			return;
		
		int balancelimit = getEssentials().getConfig().getInt("max-money");
    	
    	if ((getEconomy().getBalance(player) + amount) > balancelimit)
    	{
    		return;
    	}
        
    	EconomyResponse responsedeposit = getEconomy().depositPlayer(player, amount);
		if(responsedeposit.transactionSuccess()) 
		{
			player.sendMessage(ChatColor.YELLOW + "* You recieve $" + amount);
		} else {
			System.out.println("giveEssentialsMoney - Error depositing money to users account " + String.format(responsedeposit.errorMessage));
		}
    	
		return;
		
	}

	public IChannelManager getChannelManager() {
		// TODO Auto-generated method stub
		return this.channelManager;
	}

	public int getWorldPerkDropCountModifier() {
		// TODO Auto-generated method stub
		return 1;
	}
}
