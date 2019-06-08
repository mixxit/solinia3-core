package com.solinia.solinia.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class PlayerEquipmentTickEvent extends Event implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private ISoliniaPlayer player;
	private List<ISoliniaItem> items;
	
	public PlayerEquipmentTickEvent(ISoliniaPlayer player, List<ISoliniaItem> items) 
    {
		this.player = player;
		this.cancelled = false;
		this.items = items;
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
    
    public List<ISoliniaItem> getItems()
    {
    	return this.items;
    }
}