package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaChunkAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;
import com.solinia.solinia.Models.SoliniaChunk;

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
			if (args.length == 0)
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
				
				player.sendMessage("Current Territory Overview:");
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
				
				if (alignment != null && solPlayer.isAlignmentEmperor() && alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
				{
					if (!alignmentChunk.isTradePost() && !isInMaterialZone)
					{
						createTradePostText = "You can toggle a tradepost here with /trader tradepost";
					}
				}
				
				if (alignment != null && solPlayer.isAlignmentEmperor() && alignment.getName().toUpperCase().equals(solPlayer.getRace().getAlignment().toUpperCase()))
				{
					if (alignmentChunk.isTradePost())
					{
						createTradePostText = "/trader browse";
					}
				}
				
				player.sendMessage("Economic Zone: [" + chunk.getChunkX() + "," + chunk.getChunkZ() + "]");
				player.sendMessage("Material Zone: " + isInMaterialZone);
				
				if (alignmentChunk != null)
				{
					player.sendMessage("Has Trade Post: " + alignmentChunk.isTradePost() + " " + createTradePostText + " " + browseTradePostText);
				} else {
					player.sendMessage("Has Trade Post: FALSE");
				}
				
				if (alignment != null)
				{
					player.sendMessage("Controlled by alliance: " + alignment.getName().toUpperCase());
				} else {
					player.sendMessage("Controlled by alliance: NONE");
				}
				
				if (alignmentChunk != null)
				{
					player.sendMessage("Coin generated: $" + alignmentChunk.getCoinGenerated());
				} else {
					player.sendMessage("Coin generated: $0");
				}

				if (args.length > 0)
				{
					player.sendMessage("Territory Option: " + args[0]);
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
							
							if (alignmentChunk.getAlignmentId() != playerAlignment.getId())
							{
								player.sendMessage("This territory is not a trade post of your alliance");
								return true;
							}
							
							player.sendMessage("DEBUG: Sending Merchant Items");
							
							break;
						case "tradepost":
							if (alignmentChunk == null)
							{
								player.sendMessage("This territory is not owned by an alliance");
								return true;
							}

							if (!solPlayer.isAlignmentEmperor())
							{
								player.sendMessage("You are not an alignment emperor");
								return true;							
							}
							
							if (alignmentChunk.getAlignmentId() != playerAlignment.getId())
							{
								player.sendMessage("This territory is not a trade post of your alliance");
								return true;
							}
							
							if (alignmentChunk.isTradePost())
							{
								player.sendMessage("This territory is already a trade post");
								return true;
							}
							
							alignmentChunk.setTradePost(true);
							player.sendMessage("This territory has been set as a trade post!");
							
							break;
						default:
							player.sendMessage("Invalid argument");
							break;
					}
				} else {
					player.sendMessage("Other arguments: /trader browse   /trader tradepost");
				}
		        return true;
			}
        } catch (CoreStateInitException e)
        {
        	
        }

		return true;
	}
}
