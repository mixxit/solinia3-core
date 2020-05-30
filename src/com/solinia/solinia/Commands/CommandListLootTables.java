package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaLootTable;
import com.solinia.solinia.Utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;

public class CommandListLootTables implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listloottables"))
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
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getLootTables(), sender, args,SoliniaLootTable.class);
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
			for(ISoliniaLootTable entitylist : StateManager.getInstance().getConfigurationManager().getLootTables())
			{
				if (!filter.equals(""))
				{
					if (entitylist.getName().toUpperCase().contains(filter))
					{
						sender.sendMessage("ID: " + ChatColor.GOLD + entitylist.getId() + ChatColor.RESET + " - " + entitylist.getName());
					}
				} else {
					sender.sendMessage("ID: " + ChatColor.GOLD + entitylist.getId() + ChatColor.RESET + " - " + entitylist.getName());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
