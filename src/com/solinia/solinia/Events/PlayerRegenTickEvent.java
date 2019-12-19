package com.solinia.solinia.Events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerRegenTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private UUID playerUuid;

	public PlayerRegenTickEvent(UUID playerUuid) 
    {
		this.playerUuid = playerUuid;
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
}