package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Managers.StateManager;

public class CommandCreateAlignment implements CommandExecutor {
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
		
		if (args.length < 1)
			return false;
		
		String alignmentname = args[0];
		
		try {
			StateManager.getInstance().getConfigurationManager().addAlignment(alignmentname.toUpperCase());
			sender.sendMessage("* Alignment created");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}
		return true;
	}
}
