package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaZone;

public class PlayerZoneTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private ISoliniaPlayer player;
	private SoliniaZone zone;

	public PlayerZoneTickEvent(ISoliniaPlayer player, SoliniaZone zone) 
    {
		this.player = player;
		this.cancelled = false;
		this.zone = zone;
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
    
    public ISoliniaPlayer getPlayer()
    {
    	return this.player;
    }
    
    public SoliniaZone getZone()
    {
    	return zone;
    }
}