package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateLootTable implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
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
		
		if (loottablename.length() > 15)
		{
			sender.sendMessage("Loottable name cannot be longer than 15 characters");
			return true;
		}
		
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
