package com.solinia.solinia.Events;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTrackEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private UUID playerUuid;
	private Location trackingLocation;

	public PlayerTrackEvent(UUID playerUuid, Location location) 
    {
		this.playerUuid = playerUuid;
		this.trackingLocation = location;
		this.cancelled = false;
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
    
    public UUID getPlayerUuid()
    {
    	return this.playerUuid;
    }
    
    public Location getTrackingLocation()
    {
    	return this.trackingLocation;
    }
}