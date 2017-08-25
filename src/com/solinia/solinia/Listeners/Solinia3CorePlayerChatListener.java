package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Events.SoliniaAsyncPlayerChatEvent;
import com.solinia.solinia.Managers.StateManager;

public class Solinia3CorePlayerChatListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CorePlayerChatListener(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnChat(SoliniaAsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		if (!(event.getRawEvent() instanceof AsyncPlayerChatEvent))
			return;
		
		// We control all chat!
		AsyncPlayerChatEvent rawEvent = (AsyncPlayerChatEvent)event.getRawEvent();
		rawEvent.setCancelled(true);
		
		// TODO - Support checking channel modes of player
		StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(event.getPlayer(), event.getMessage());
	}
}