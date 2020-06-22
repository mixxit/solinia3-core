package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateLootTable implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createloottable"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: name");
			return false;
		}
		
		String loottablename = "";
		for(String entry : args)
		{
			loottablename  += entry;
		}
		
		if (loottablename.equals(""))
		{
			sender.sendMessage("Blank name not allowed when creating loottable");
			return false;
		}
		
		loottablename = loottablename.replace(" ", "_").toUpperCase();
		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getLootTable(loottablename) != null)
			{
				sender.sendMessage("LootTable already exists with the same name");
				return true;
			}

			SoliniaLootFactory.CreateLootTable(loottablename);
			sender.sendMessage("Loot Table created as " + StateManager.getInstance().getConfigurationManager().getLootTable(loottablename).getId());
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
