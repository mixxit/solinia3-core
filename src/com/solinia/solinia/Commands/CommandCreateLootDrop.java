package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaLootFactory;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateLootDrop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createlootdrop"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: name");
			return false;
		}
		
		String lootdropname = "";
		int count = 0;
		for(String entry : args)
		{
			lootdropname  += entry;
			count++;
		}
		
		if (lootdropname.equals(""))
		{
			sender.sendMessage("Blank name not allowed when creating lootdrop");
			return false;
		}
		
		lootdropname = lootdropname.replace(" ", "_").toUpperCase();
		
		if (lootdropname.length() > 25)
		{
			sender.sendMessage("Lootdrop name cannot be longer than 25 characters");
			return true;
		}
		
		try
		{
			if (StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropname) != null)
			{
				sender.sendMessage("LootDrop already exists with the same name");
				return true;
			}
			
			SoliniaLootFactory.CreateLootDrop(lootdropname);
			sender.sendMessage("LootDrop created as " + StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropname).getId());
			
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}		
		
		return true;
	}
}
