package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaAsyncPlayerChatEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
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
		
		if (event.getPlayer().getLanguage() == null || event.getPlayer().getLanguage().equals("UNKNOWN"))
		{
			if (event.getPlayer().getRace() == null)
			{
				event.getPlayer().getBukkitPlayer().sendMessage("You cannot speak until you set a race /setrace");
				return;
			} else {
				event.getPlayer().setLanguage(event.getPlayer().getRace().getName().toUpperCase());
			}
		}
		
		if (!(event.getRawEvent() instanceof AsyncPlayerChatEvent))
			return;
		
		// We control all chat!
		AsyncPlayerChatEvent rawEvent = (AsyncPlayerChatEvent)event.getRawEvent();
		rawEvent.setCancelled(true);
		
		// TODO - Support checking channel modes of player
		if (event.getPlayer().getCurrentChannel().equals("LOCAL"))
		{
			StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(event.getPlayer(), event.getMessage());
		} else {
			StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(event.getPlayer(), event.getMessage());
		}
	}
}