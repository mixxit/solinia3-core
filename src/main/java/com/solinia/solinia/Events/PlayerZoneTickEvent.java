package com.solinia.solinia.Events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class PlayerZoneTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private UUID playerUUID;
	private int zoneId;

	public PlayerZoneTickEvent(UUID playerUUID, int zoneId) 
    {
		this.playerUUID = playerUUID;
		this.cancelled = false;
		this.zoneId = zoneId;
    }
	
	public boolean isCancelled() {
        return this.cancelled;
    }
 
    public void setCancelled(boolean cancelled) {
    	this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public UUID getPlayerUUID()
    {
    	return this.playerUUID;
    }
    
    public int getZoneId()
    {
    	return zoneId;
    }
    
    public SoliniaZone getZone()
    {
    	if (this.zoneId < 1)
    		return null;
    	
    	try {
			return StateManager.getInstance().getConfigurationManager().getZone(this.getZoneId());
		} catch (CoreStateInitException e) {
			return null;
		}
    }

	public ISoliniaPlayer getPlayer() {
		if (this.getPlayerUUID() == null)
			return null;
		
		Player player = Bukkit.getPlayer(this.getPlayerUUID());
		if (player ==null)
			return null;
		
		try {
			return SoliniaPlayerAdapter.Adapt(player);
		} catch (CoreStateInitException e) {
			return null;
		}
	}
}