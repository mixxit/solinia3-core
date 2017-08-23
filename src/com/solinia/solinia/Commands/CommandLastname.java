package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

public class CommandLastname implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		Player player = (Player)sender;
		try {
			player.sendMessage("Old Lastname: " + SoliniaPlayerAdapter.Adapt(player).getLastname());
			
			if (args.length == 0)
				return false;
			
			boolean isNameValid = args[0].matches("[A-Za-z_]{3,7}");
			if (!isNameValid)
			{
				player.sendMessage("Lastname must be a-z characters and between 3 and 7 characters long");
				return true;
			}
			
			SoliniaPlayerAdapter.Adapt(player).setLastname(args[0]);
			sender.sendMessage("* Lastname set");
			return true;
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}

		return false;
	}
}
