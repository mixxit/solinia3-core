package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaNPC;

public class SoliniaNPCUpdatedEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private ISoliniaNPC npc;
	private boolean reloadMythicMobs = true;

	public SoliniaNPCUpdatedEvent(ISoliniaNPC npc, boolean reloadMythicMobs) 
    {
		this.npc = npc;
		this.cancelled = false;
		this.setReloadMythicMobs(reloadMythicMobs);
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
    
    public ISoliniaNPC getNPC()
    {
    	return this.npc;
    }

	public boolean getReloadMythicMobs() {
		return reloadMythicMobs;
	}

	public void setReloadMythicMobs(boolean reloadMythicMobs) {
		this.reloadMythicMobs = reloadMythicMobs;
	}
}
