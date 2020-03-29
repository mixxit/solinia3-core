package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerValidatedModEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PacketOpenCharacterCreation;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Utils.ForgeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerValidatorModListener implements Listener {
	Solinia3CorePlugin plugin;

	public PlayerValidatorModListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onPlayerNoMoveUntilModVersionEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;
		
		if (!event.getFrom().isWorldLoaded())
			return;

		if (!event.getTo().isWorldLoaded())
			return;

		if (!event.getFrom().getChunk().isLoaded())
			return;

		if (!event.getTo().getChunk().isLoaded())
			return;
		
		try
		{
			if (StateManager.getInstance().getPlayerManager().playerModVersion(event.getPlayer()) == null ||
					StateManager.getInstance().getPlayerManager().playerModVersion(event.getPlayer()).equals(""))
			{
				if (event.getTo().getY() < event.getFrom().getY()) {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());

				} else {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setY(event.getFrom().getY());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
				}
				event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "* Please wait while the server waits for your client to declare its mod version before moving. For help please ask in /ooc <msg>"));
				return;
			}
			
			ISoliniaPlayer soliniaPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (soliniaPlayer.getClassObj() == null)
			{
				if (event.getTo().getY() < event.getFrom().getY()) {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());

				} else {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setY(event.getFrom().getY());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
				}

				event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "* You must set your race and class before moving. For help please ask in /ooc <msg>"));
				return;
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@EventHandler
	public void onPlayerValidatedModEvent(PlayerValidatedModEvent event) {
		try {
			if (event.getPlayer() == null)
				return;
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solPlayer == null)
				return;
			
			if (solPlayer.getClassObj() == null)
			{
			    try {
			    	PacketOpenCharacterCreation packet = new PacketOpenCharacterCreation();
			    	packet.fromData(StateManager.getInstance().getConfigurationManager().getCharacterCreationChoices());
					ForgeUtils.QueueSendForgeMessage(event.getPlayer(),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CHARCREATION,packet.toPacketData(),0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e) {

		}
		
	}
	
}
