package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Managers.StateManager;

public class Solinia3CorePlayerListener implements Listener {

	Solinia3CorePlugin _plugin;
	
	public Solinia3CorePlayerListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		_plugin = solinia3CorePlugin;
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) 
	{

	}
	
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) 
	{
	
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{

	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) 
	{
		if (event.isCancelled())
			return;
		
    }
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		
	}
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
		StateManager.getInstance().getPlayerManager().RegisterPlayer(event.getPlayer());
    }
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if (event.isCancelled())
			return;
		
    }
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return;

	}
	
	@EventHandler
	public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) 
	{
		if (event.isCancelled())
			return;
		
    }
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;
	}

}
