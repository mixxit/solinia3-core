package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class CommandListItems implements CommandExecutor {
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
		
		if (args.length == 0)
		{
			sender.sendMessage("You must provide some text to filter the item name by");
			return true;
		}
		
		// Filter for name
		
		int found = 0;
		try {
			for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
			{
				found++;
				if (item.getDisplayname().toUpperCase().contains(args[0].toUpperCase()))
				{
					String itemmessage = item.getId() + " - " + item.getDisplayname();
					sender.sendMessage(itemmessage);
				}
			}
			
			if (found == 0)
			{
				sender.sendMessage("Item could not be located by search string");
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
