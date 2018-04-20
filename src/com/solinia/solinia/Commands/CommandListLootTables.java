package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;

public class CommandListLootTables implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listloottables"))
			{
				player.sendMessage("You do not have permission to access this command");
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
			for(ISoliniaLootTable entitylist : StateManager.getInstance().getConfigurationManager().getLootTables())
			{
				if (!filter.equals(""))
				{
					if (entitylist.getName().toUpperCase().contains(filter))
					{
						sender.sendMessage("ID: " + entitylist.getId() + " - " + entitylist.getName());
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
