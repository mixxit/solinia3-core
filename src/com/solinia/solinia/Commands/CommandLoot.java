package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandLoot implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		if (args.length > 0)
		{
			String search = "";
			for(int i = 0; i < args.length; i++)
			{
				search += args[i] + " ";
			}
			
			sendLootListToSender(sender,search.trim());
		}
		
		return true;
	}
	
	private void sendLootListToSender(CommandSender sender, String itemMatch) {
		try
		{
			if (itemMatch.length() < 3)
			{
				sender.sendMessage("Item search must be at least 3 characters: " + itemMatch);
				return;
			}
			
			List<ISoliniaItem> items = StateManager.getInstance().getConfigurationManager().getItemsByPartialName(itemMatch);
			
			int itemIdLookup = 0;
			try
			{
				itemIdLookup = Integer.parseInt(itemMatch);
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemIdLookup);
				items.add(item);
			} catch (Exception e)
			{
				
			}
			
			if (items.size() < 1)
			{
				sender.sendMessage("Could not find item: " + itemMatch);
				return;
			}
			
			if (items.size() > 1)
			{
				sender.sendMessage("More than one item found with this string, please be more specific: " + itemMatch);
				return;
			}
			
			String itemname = "";
			for(ISoliniaItem item : items)
			{
				itemname = item.getDisplayname();
				List<Integer> lootDropIds = StateManager.getInstance().getConfigurationManager().getLootDropIdsWithItemId(item.getId());
				
				if (lootDropIds.size() < 1)
				{
					sender.sendMessage("Item [" + itemname + "] not found in any loot drops");
					return;
				}
				
				List<Integer> lootTableIds = StateManager.getInstance().getConfigurationManager().getLootTablesWithLootDrops(lootDropIds);
				
				if (lootTableIds.size() < 1)
				{
					sender.sendMessage("Item [" + itemname + "] not found in any loot tables");
					return;
				}
				
				List<String> matchingNpcList = new ArrayList<String>();
				String currentLine = "";
				
				for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
				{
					if (!lootTableIds.contains(npc.getLoottableid()))
						continue;
					
					if ((currentLine + npc.getName() + " ").length() > 2000)
					{
						matchingNpcList.add(currentLine);
						currentLine = "";
					}
					
					currentLine += npc.getName() + " ";
				}
				
				if (!currentLine.equals(""))
				{
					matchingNpcList.add(currentLine);
				}
				
				for(String line : matchingNpcList)
				{
					sender.sendMessage("Item [" + itemname + "] found on: " + line);
				}
			}
			
		} catch (CoreStateInitException e)
		{
			// ignore it
		}
		
	}

}