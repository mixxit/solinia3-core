package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaNPCMerchant;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandListMerchantLists implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listmerchantlists"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		String filter = "";
		if (args.length > 0)
		{
			filter = args[0].toUpperCase();
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getNPCMerchants(), sender, args,SoliniaNPCMerchant.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
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
						sender.sendMessage("ID: " + ChatColor.GOLD + entitylist.getId() + ChatColor.RESET + " - " + entitylist.getName());
						/*for(ISoliniaNPCMerchantEntry entry : entitylist.getEntries())
						{
							sender.sendMessage("- ID: " + entry.getId() + " - ItemID: " + entry.getItemid() + StateManager.getInstance().getConfigurationManager().getItem(entry.getItemid()).getDisplayname());
						}*/
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
