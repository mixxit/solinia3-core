package com.solinia.solinia.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Events.SoliniaSyncPlayerChatEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.InteractionType;

public class Solinia3CorePlayerChatListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CorePlayerChatListener(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnChat(SoliniaSyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		String currentChannel = event.getPlayer().getCurrentChannel();
		
		// always redirect npc interactions to local
		if (event.getPlayer().getEntityTarget() != null)
		{
			currentChannel = "LOCAL";
		}
		
		if (event.getPlayer().getRace() == null)
		{
			event.getPlayer().getBukkitPlayer().sendMessage("You must set your race to speak in chat - /race");
			return;
		}
		
		// TODO - Support checking channel modes of player
		switch(currentChannel)
		{
			case "OOC":
				StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(event.getPlayer(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
				break;
			case "LOCAL":
				StateManager.getInstance().getChannelManager().sendToLocalChannelDecorated(event.getPlayer(), event.getMessage(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
				break;
			case "WHISPER":
				StateManager.getInstance().getChannelManager().sendToWhisperChannelDecorated(event.getPlayer(), event.getMessage(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
				break;
			case "SHOUT":
				StateManager.getInstance().getChannelManager().sendToShoutChannelDecorated(event.getPlayer(), event.getMessage(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
				break;
			default:
				StateManager.getInstance().getChannelManager().sendToGlobalChannelDecorated(event.getPlayer(), event.getMessage(), event.getPlayer().getBukkitPlayer().getInventory().getItemInMainHand());
				break;
		}
		
		// NPC responses (if applicable)
		if (event.getPlayer().getEntityTarget() != null)
		{
			Entity entity = event.getPlayer().getEntityTarget();
			if (entity != null && entity instanceof LivingEntity && event.getPlayer().getBukkitPlayer().getLocation().distance(entity.getLocation()) < 4)
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