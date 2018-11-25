package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Events.SoliniaAsyncPlayerChatEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Utils.Utils;

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
		Utils.CancelEvent(rawEvent);
		
		String currentChannel = event.getPlayer().getCurrentChannel();
		
		// always redirect npc interactions to local
		if (event.getPlayer().getInteraction() != null)
		{
			currentChannel = "LOCAL";
		}
		
		// TODO - Support checking channel modes of player
		if (currentChannel.equals("LOCAL"))
		{
			if (event.getPlayer().getRace() == null)
			{
				event.getPlayer().getBukkitPlayer().sendMessage("You must set your race to speak in local chat - /race");
				return;
			}
			StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(event.getPlayer(), event.getMessage(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
		} else {
			StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(event.getPlayer(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
			return;
		}
		
		if (event.getPlayer().getRace() == null)
		{
			event.getPlayer().getBukkitPlayer().sendMessage("You must set your race to speak in local chat - /race");
			return;
		}
		
		// NPC responses (if applicable)
		if (event.getPlayer().getInteraction() != null)
		{
			Entity entity = Bukkit.getEntity(event.getPlayer().getInteraction());
			if (entity != null && entity instanceof LivingEntity)
			{
				LivingEntity livingEntity = (LivingEntity)entity;
				ISoliniaLivingEntity solentity;
				try {
					solentity = StateManager.getInstance().getEntityManager().getLivingEntity(livingEntity);
					solentity.processInteractionEvent(event.getPlayer().getBukkitPlayer(), InteractionType.CHAT, event.getMessage());
				} catch (CoreStateInitException e) {
					// skip
				}
			}
		}
	}
}