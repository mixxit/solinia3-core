package com.solinia.solinia.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.solina.solinia.Interfaces.ISoliniaPlayer;

public class SoliniaPlayerJoinEvent extends Event implements Cancellable {
	private boolean _cancelled;
	private static final HandlerList _handlers = new HandlerList();
	private ISoliniaPlayer _player;

	public SoliniaPlayerJoinEvent(ISoliniaPlayer player) 
    {
		_player = player;
		_cancelled = false;
    }
	
	public boolean isCancelled() {
        return _cancelled;
    }
 
    public void setCancelled(boolean cancelled) {
        this._cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList() {
        return _handlers;
    }
    
    public ISoliniaPlayer getPlayer()
    {
    	return _player;
    }
}
