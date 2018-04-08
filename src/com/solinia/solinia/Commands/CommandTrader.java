package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.solinia.solinia.Adapters.SoliniaChunkAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;
import com.solinia.solinia.Models.SoliniaChunk;

import net.md_5.bungee.api.ChatColor;

public class CommandTrader implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		if (sender.isOp())
		{
			try {
				for(ISoliniaAlignment alignment : StateManager.getInstance().getConfigurationManager().getAlignments())
				{
					alignment.sendAlignmentStats(sender);
				}
			} catch (CoreStateInitException e) {
				
			}
		}
		
		if (!(sender instanceof Player))
			return false;
				
		Player player = (Player)sender;
		try
        {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			
			if (solPlayer.getRace() == null)
			{
				player.sendMessage("You cannot see trader info until you set your race");
				return true;
			}
			
			ISoliniaAlignment playerAlignment = StateManager.getInstance().getConfigurationManager().getAlignment(solPlayer.getRace().getAlignment().toUpperCase());
			
			if (solPlayer.isAlignmentEmperor())
			{
				playerAlignment.sendAlignmentStats(player);
			}
			
			player.sendMessage(ChatColor.AQUA + "Current Territory Overview:" + ChatColor.RESET);
			player.sendMessage("-----------------------------");
			
			SoliniaChunk chunk = SoliniaChunkAdapter.Adapt(player.getWorld().getChunkAt(player.getLocation()));
			if (chunk == null)
			{
				player.sendMessage("Territory does not exist!");
				return true;
			}
			
			boolean isInMaterialZone = chunk.isInZoneWithMaterials(player.getWorld());

			SoliniaAlignmentChunk alignmentChunk = null;
			ISoliniaAlignment alignment = null;
			
			for(ISoliniaAlignment alignmentEntry : StateManager.getInstance().getConfigurationManager().getAlignments())
			{
				SoliniaAlignmentChunk temporaryChunk = alignmentEntry.getChunk(chunk);
				if (temporaryChunk == null)
					continue;
				
				alignmentChunk = temporaryChunk;
				alignment = alignmentEntry;
			}
			
			String createTradePostText = "";
			String browseTradePostText = "";
			
			if (alignment != null && (solPlayer.isAlignmentEmperor() || player.isOp()) && alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
			{
				if (!alignmentChunk.isTradePost() && !isInMaterialZone)
				{
					createTradePostText = "- Toggle with /trader toggletradepost";
				}
			}
			
			if (alignment != null && alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
			{
				if (alignmentChunk.isTradePost())
				{
					createTradePostText = "- /trader browse";
				}
			}
			
			player.sendMessage("Economic Zone: [" + chunk.getChunkX() + "," + chunk.getChunkZ() + "]");
			player.sendMessage("Material Zone: " + ChatColor.GOLD + isInMaterialZone + ChatColor.RESET);
			
			if (alignmentChunk != null)
			{
				player.sendMessage("Has Trade Post: " + ChatColor.GOLD + alignmentChunk.isTradePost() + ChatColor.RESET + " " + createTradePostText + " " + browseTradePostText);
			} else {
				player.sendMessage("Has Trade Post: " + ChatColor.GOLD + "FALSE" + ChatColor.RESET);
			}
			
			if (alignment != null)
			{
				player.sendMessage("Controlled by alliance: " + ChatColor.GOLD + alignment.getName().toUpperCase() + ChatColor.RESET);
			} else {
				player.sendMessage("Controlled by alliance: " + ChatColor.GOLD + "NONE" + ChatColor.RESET);
			}
			
			if (alignmentChunk != null)
			{
				player.sendMessage("Coin generated: " + ChatColor.GOLD + "$" + alignmentChunk.getCoinGenerated() + ChatColor.RESET);
			} else {
				player.sendMessage("Coin generated: " + ChatColor.GOLD + "$0" + ChatColor.RESET);
			}

			
			if (args.length > 0)
			{
				player.sendMessage(ChatColor.AQUA + "Territory Option: " + args[0] + ChatColor.RESET);
				
				player.sendMessage("-----------------------------");
				
				switch (args[0])
				{
					case "browse":
						if (alignmentChunk == null)
						{
							player.sendMessage("This territory is not a trade post");
							return true;
						}
						
						if (!alignmentChunk.isTradePost())
						{
							player.sendMessage("This territory is not a trade post");
							return true;
						}
						
						if (!alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
						{
							player.sendMessage("This territory is not a trade post of your alliance");
							return true;
						}
						
						sendMerchantItemListToPlayer(alignmentChunk, player, 0);
						
						break;
					case "toggletradepost":
						if (alignmentChunk == null)
						{
							player.sendMessage("This territory is not owned by an alliance");
							return true;
						}
	
						if (!(solPlayer.isAlignmentEmperor() || player.isOp()))
						{
							player.sendMessage("You are not an alignment emperor");
							return true;							
						}
						
						if (!alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
						{
							player.sendMessage("This territory is not part of your alliance");
							return true;
						}
						
						alignmentChunk.setTradePost(!alignmentChunk.isTradePost());
						player.sendMessage("This territory tradepost flag has been flipped to " + alignmentChunk.isTradePost() + "!");
						
						break;
					default:
						player.sendMessage("Invalid argument");
						break;
				}
			} else {
				player.sendMessage("Other arguments: /trader browse   /trader toggletradepost");
			}
			
        } catch (CoreStateInitException e)
        {
        	
        }

		return true;
	}
	
	public void sendMerchantItemListToPlayer(SoliniaAlignmentChunk alignmentChunk, Player player, int pageno) 
	{
		player.sendMessage("DEBUG: Sending Merchant Items");
		Inventory alignmentChunkInventory;
		try {
			alignmentChunkInventory = StateManager.getInstance().getEntityManager().getTradeShopMerchantInventory(player.getUniqueId(),alignmentChunk, pageno);
			if (alignmentChunkInventory != null)
				player.openInventory(alignmentChunkInventory);
		} catch (CoreStateInitException e) {
			
		}
		
	}
}
