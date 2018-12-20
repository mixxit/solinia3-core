package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

public class CommandListRaces implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listraces"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces())
			{
				sender.sendMessage("RaceID: " + race.getId() + " - " + race.getName() + " adminonly: " + race.isAdmin());
			}
			
			return true;
		}
		
		// Filter for name
		for(ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces())
		{
			if (race.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("RaceID: " + race.getId() + " - " + race.getName() + " adminonly: " + race.isAdmin());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
