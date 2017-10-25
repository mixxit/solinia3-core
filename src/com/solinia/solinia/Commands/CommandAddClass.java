package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaClassCreationException;
import com.solinia.solinia.Factories.SoliniaClassFactory;

public class CommandAddClass implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return true;
			}
		}
		
		if (args.length < 2)
			return false;
		
		String classname = args[0];
		boolean adminonly = Boolean.parseBoolean(args[1]);
		
		try {
			SoliniaClassFactory.CreateClass(classname,adminonly);
			sender.sendMessage("* Class created");
		} catch (CoreStateInitException | SoliniaClassCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}
		return true;
	}

}
