package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaGod;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.Utils;

public class CommandListGods implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listgods"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaGod god : StateManager.getInstance().getConfigurationManager().getGods())
			{
				sender.sendMessage("GodID: " + god.getId() + " - " + god.getName());
			}
			
			return true;
		}
		
		if (args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getGods(), sender, args,SoliniaGod.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(ISoliniaGod god : StateManager.getInstance().getConfigurationManager().getGods())
		{
			if (god.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("GodID: " + god.getId() + " - " + god.getName());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
