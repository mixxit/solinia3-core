package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Models.SoliniaZone;

public class ZoneTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private SoliniaZone zone;

	public ZoneTickEvent(SoliniaZone zone) 
    {
		this.zone = zone;
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
    
    public SoliniaZone getZone()
    {
    	return this.zone;
    }
}