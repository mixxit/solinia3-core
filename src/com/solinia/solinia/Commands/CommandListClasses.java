package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaClass;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandListClasses implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listclasses"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaClass classObj : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				sender.sendMessage("ClassID: " + ChatColor.GOLD + classObj.getId() + ChatColor.RESET + " - " + classObj.getName() + " adminonly: " + classObj.isAdmin());
			}
			
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getClasses(), sender, args,SoliniaClass.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(ISoliniaClass classObj : StateManager.getInstance().getConfigurationManager().getClasses())
		{
			if (classObj.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("ClassID: " + ChatColor.GOLD + classObj.getId() + ChatColor.RESET + " - " + classObj.getName() + " adminonly: " + classObj.isAdmin());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
