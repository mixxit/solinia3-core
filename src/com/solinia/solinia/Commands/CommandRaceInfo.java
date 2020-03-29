package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Utils.Utils;

public class CommandRaceInfo implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		sender.sendMessage("Race Information:");
		
		try
		{
			Utils.sendRaceInfo(sender);
		
		} catch (Exception e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
