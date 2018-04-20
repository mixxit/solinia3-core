package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
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
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

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

			if (solplayer.getInteraction() == null) {
				player.sendMessage(ChatColor.GRAY + "* You are not currently interacting with an NPC");
				return true;
			}

			Entity entity = Bukkit.getEntity(solplayer.getInteraction());

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
				player.sendMessage(ChatColor.GRAY + "* You are not currently interacting with an NPC");
				return true;
			}

			ISoliniaNPC solnpc = StateManager.getInstance().getConfigurationManager().getNPC(solentity.getNpcid());
			
			int itemid = Integer.parseInt(args[0]);
			if (itemid < 1) {
				player.sendMessage("ItemID must be greater than 0");
				return true;
			}

			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
			if (Utils.getPlayerTotalCountOfItemId(player, itemid) < 1) {
				player.sendMessage("Sorry but you do not have the quantity you are trying to give");
				return true;
			}
			
			// Check if the npc actually wants to receive this item
			boolean npcWantsItem = false;
			for (ISoliniaNPCEventHandler eventHandler : solnpc.getEventHandlers())
			{
				if (!eventHandler.getInteractiontype().equals(InteractionType.ITEM))
					continue;
				
				System.out.println("Comparing item id: " + item.getId() + " to triggerdata " + eventHandler.getTriggerdata());
				if (Integer.parseInt(eventHandler.getTriggerdata()) != item.getId())
					continue;
				
				if (eventHandler.getChatresponse() != null && !eventHandler.getChatresponse().equals(""))
				{
					System.out.println("Checking if player meets requirements to hand in item");
					if (!eventHandler.playerMeetsRequirements(player))
					{
						player.sendMessage(ChatColor.GRAY + "[Hint] You do not meet the requirements to hand this quest item in. Either you are missing a quest step or have already completed this step");
						continue;
					}
					
					System.out.println("NPC wants the item");
					npcWantsItem = true;
					
					String response = eventHandler.getChatresponse();
					if (eventHandler.getResponseType().equals("SAY"))
						solentity.say(solnpc.replaceChatWordsWithHints(response),player, true);
					if (eventHandler.getResponseType().equals("EMOTE"))
						solentity.emote(solnpc.replaceChatWordsWithHints(response));
						
					eventHandler.awardPlayer((Player)player);
					Utils.removeItemsFromInventory(player, itemid, 1);
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
