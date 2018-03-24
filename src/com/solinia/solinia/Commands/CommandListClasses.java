package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

public class CommandListClasses implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listclasses"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaClass classObj : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				sender.sendMessage("ClassID: " + classObj.getId() + " - " + classObj.getName() + " adminonly: " + classObj.isAdmin());
			}
			
			return true;
		}
		
		// Filter for name
		for(ISoliniaClass classObj : StateManager.getInstance().getConfigurationManager().getClasses())
		{
			if (classObj.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("ClassID: " + classObj.getId() + " - " + classObj.getName() + " adminonly: " + classObj.isAdmin());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
