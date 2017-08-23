package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

public class CommandForename implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		Player player = (Player)sender;
		try {
			player.sendMessage("Old Forename: " + SoliniaPlayerAdapter.Adapt(player).getForename());
			if (args.length == 0)
				return false;
			
			SoliniaPlayerAdapter.Adapt(player).setForename(args[0]);
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		
		sender.sendMessage("* Forename set");
		return true;
	}
}
