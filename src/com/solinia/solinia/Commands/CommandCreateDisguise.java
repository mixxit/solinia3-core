package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaDisguiseCreationException;
import com.solinia.solinia.Factories.SoliniaDisguiseFactory;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaDisguise;

public class CommandCreateDisguise implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.createdisguise"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// NameNoSpaces
		// disguisetype
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: name disguisetype");
			return false;
		}
		
		String name = args[0].toUpperCase();
		String disguisetype = args[1].toUpperCase();
		
		if (name.equals(""))
		{
			sender.sendMessage("Name of disguise cannot be null");
			return false;
		}
		if (disguisetype.equals(""))
		{
			sender.sendMessage("Type of disguise cannot be null");
			return false;
		}
		
		try {
			
			SoliniaDisguise existing = StateManager.getInstance().getConfigurationManager().getDisguise(name);
			if (existing != null)
			{
				sender.sendMessage("A disguise already exists with this name");
				return false;
			}
			
			SoliniaDisguise disguise = SoliniaDisguiseFactory.Create(name,disguisetype);
			
			sender.sendMessage("Created Disguise: " + disguise.getId());
		} catch (CoreStateInitException | SoliniaDisguiseCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
