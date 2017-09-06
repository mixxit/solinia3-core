package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CommandUpdateSpawnGroupLoc implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!player.isOp())
		{
			player.sendMessage("This is an operator only command");
			return false;
		}
		
		if (args.length < 1)
		{
			player.sendMessage("Insufficient arguments: spawngroupid");
			return false;
		}
		
		int spawngroupid = Integer.parseInt(args[0]);
		
		if (spawngroupid < 1)
		{
			player.sendMessage("Invalid spawngroup id");
			return false;
		}

		try
		{
			if (StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupid) == null)
			{
				player.sendMessage("Cannot locate spawngroup id: " + spawngroupid);
				return false;
			}
			
			StateManager.getInstance().getConfigurationManager().updateSpawnGroupLoc(spawngroupid,player.getLocation());
			player.sendMessage("Spawngroup setting updated");
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
			return true;
		}
		return true;
	}
}
