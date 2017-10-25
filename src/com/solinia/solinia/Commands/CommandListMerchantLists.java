package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Managers.StateManager;

public class CommandListMerchantLists implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		String filter = "";
		if (args.length > 0)
		{
			filter = args[0].toUpperCase();
		}
		
		try
		{
		
			// Filter for name
			for(ISoliniaNPCMerchant entitylist : StateManager.getInstance().getConfigurationManager().getNPCMerchants())
			{
				if (!filter.equals(""))
				{
					if (entitylist.getName().toUpperCase().contains(filter))
					{
						sender.sendMessage("ID: " + entitylist.getId() + " - " + entitylist.getName());
						for(ISoliniaNPCMerchantEntry entry : entitylist.getEntries())
						{
							sender.sendMessage("- ID: " + entry.getId() + " - ItemID: " + entry.getItemid() + StateManager.getInstance().getConfigurationManager().getItem(entry.getItemid()).getDisplayname());
						}
					}
				} else {
					sender.sendMessage("ID: " + entitylist.getId() + " - " + entitylist.getName());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
