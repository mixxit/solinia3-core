package com.solinia.solinia.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerValidatedModEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	
	public PlayerValidatedModEvent(Player entity) 
    {
		this.player = entity;
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
    
    public Player getPlayer()
    {
    	return this.player;
    }
}