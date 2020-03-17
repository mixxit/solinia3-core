package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandNPCGive implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);

			if (args.length < 1) {
				player.sendMessage("Invalid syntax missing <itemid>");
				return true;
			}

			if (solplayer.getEntityTarget() == null) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently targetting an NPC");
				return true;
			}

			Entity entity = solplayer.getEntityTarget();

			if (entity == null) {
				player.sendMessage(ChatColor.GRAY
						+ "* The npc you are trying to interact with appears to no longer be available");
				return true;
			}

			if (!(entity instanceof LivingEntity)) {
				player.sendMessage(ChatColor.GRAY
						+ "* The npc you are trying to interact with appears to no longer be living");
				return true;
			}

			ISoliniaLivingEntity solentity = SoliniaLivingEntityAdapter.Adapt((LivingEntity) entity);
			if (solentity.getNpcid() < 1) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently targetting an NPC");
				return true;
			}

			ISoliniaNPC solnpc = StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid());
			
			int itemid = Integer.parseInt(args[0]);
			if (itemid < 1) {
				player.sendMessage("ItemID must be greater than 0");
				return true;
			}

			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
			if (PlayerUtils.getPlayerTotalCountOfItemId(player, itemid) < 1) {
				player.sendMessage("Sorry but you do not have the quantity you are trying to give");
				return true;
			}
			
			// Check if the npc actually wants to receive this item
			boolean npcWantsItem = false;
			for (ISoliniaNPCEventHandler eventHandler : solnpc.getEventHandlers())
			{
				if (!eventHandler.getInteractiontype().equals(InteractionType.ITEM))
					continue;
				
				Utils.DebugLog("CommandNPCGive","onCommand",player.getName(),"Comparing item id: " + item.getId() + " to triggerdata " + eventHandler.getTriggerdata());
				if (Integer.parseInt(eventHandler.getTriggerdata()) != item.getId())
					continue;
				
				if (eventHandler.getChatresponse() != null && !eventHandler.getChatresponse().equals(""))
				{
					Utils.DebugLog("CommandNPCGive","onCommand",player.getName(),"Checking if player meets requirements to hand in item");
					if (!eventHandler.playerMeetsRequirements(player))
					{
						player.sendMessage(ChatColor.GRAY + "[Hint] You do not meet the requirements to hand this quest item in. Either you are missing a quest step, have already completed this step");
						continue;
					}
					
					ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(eventHandler.getNpcId());
					if (npc != null)
					{
						if (!npc.isSpeaksAllLanguages())
						{
							if (npc.getRaceid() > 0)
							{
								ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(npc.getRaceid());
								if (!solplayer.understandsLanguage(race.getLanguage()))
								{
									player.sendMessage(ChatColor.AQUA + " * " + npc.getName() + " does not want this item as you do not speak their tongue and may wish to speak to you about it" + ChatColor.RESET);
									continue;
								}
							}
						}
					}
					
					Utils.DebugLog("CommandNPCGive","onCommand",player.getName(),"NPC wants the item");
					npcWantsItem = true;
					
					String response = eventHandler.getChatresponse();
					if (eventHandler.getResponseType().equals("SAY"))
						solentity.sayto(player,solnpc.replaceChatWordsWithHints(response), true);
					if (eventHandler.getResponseType().equals("EMOTE"))
						solentity.emote(solnpc.replaceChatWordsWithHints(response), false);
						
					eventHandler.awardPlayer((Player)player);
					PlayerUtils.removeItemsFromInventory(player, itemid, 1);
					
					if (eventHandler.getTeleportResponse() != null && !eventHandler.getTeleportResponse().equals("")) {
						String[] zonedata = eventHandler.getTeleportResponse().split(",");
						// Dissasemble the value to ensure it is correct
						String world = zonedata[0];
						double x = Double.parseDouble(zonedata[1]);
						double y = Double.parseDouble(zonedata[2]);
						double z = Double.parseDouble(zonedata[3]);
						Location loc = new Location(Bukkit.getWorld(world), x, y, z);
						player.teleport(loc);
					}
				}
			}
			
			if (npcWantsItem == false)
			{
				player.sendMessage(ChatColor.GRAY + "* This being does not want this item at this time");
			}

			return true;
		} catch (CoreStateInitException e) {
			player.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
