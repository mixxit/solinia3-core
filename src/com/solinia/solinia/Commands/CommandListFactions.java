package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;

public class CommandListFactions implements CommandExecutor {
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
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions())
			{
				sender.sendMessage("FactionID: " + faction.getId() + " - " + faction.getName() + " base: " + faction.getBase());
			}
			
			return true;
		}
		
		// Filter for name
		for(ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions())
		{
			if (faction.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("FactionID: " + faction.getId() + " - " + faction.getName() + " base: " + faction.getBase());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
