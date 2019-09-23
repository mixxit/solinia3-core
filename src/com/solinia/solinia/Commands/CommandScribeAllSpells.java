package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandScribeAllSpells implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.spawnitem"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// level
		
		if (args.length == 0)
			return false;

		int level = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				if (solPlayer == null || solPlayer.getClassObj() == null)
					return true;
				
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: level");
			return false;
		}
		
		return true;
	}
}
