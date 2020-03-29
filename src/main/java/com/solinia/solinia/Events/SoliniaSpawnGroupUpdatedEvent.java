package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;

public class SoliniaSpawnGroupUpdatedEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private ISoliniaSpawnGroup spawngroup;
	private boolean providerReload;

	public SoliniaSpawnGroupUpdatedEvent(ISoliniaSpawnGroup spawngroup, boolean providerReload) 
    {
		this.spawngroup = spawngroup;
		this.cancelled = false;
		this.providerReload = providerReload;
    }
	
	public boolean isCancelled() {
        return this.cancelled;
    }
 
    public void setCancelled(boolean cancelled) {
    	this.cancelled = cancelled;
    }
    
    public boolean getProviderReload()
    {
    	return this.providerReload;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public ISoliniaSpawnGroup getSpawnGroup()
    {
    	return this.spawngroup;
    }
}
