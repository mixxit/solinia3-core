package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;

public class SoliniaLivingEntityPassiveEffectTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private ISoliniaLivingEntity entity;
	
	public SoliniaLivingEntityPassiveEffectTickEvent(ISoliniaLivingEntity entity) 
    {
		this.entity = entity;
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
    
    public ISoliniaLivingEntity getSoliniaLivingEntity()
    {
    	return this.entity;
    }
}