package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;

public class CommandListSpawnGroups implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listspawngroups"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		try
		{
			if (args.length == 0)
			{
				// Return all
				for(ISoliniaSpawnGroup spawngroup : StateManager.getInstance().getConfigurationManager().getSpawnGroups())
				{
					sender.sendMessage("SpawnGroupID: " + spawngroup.getId() + " - " + spawngroup.getName() + " world: " + spawngroup.getWorld() + " XYZ: " + spawngroup.getX() + "," + spawngroup.getY() + "," + spawngroup.getZ());
				}
				
				return true;
			}
			
			// Filter for name
			for(ISoliniaSpawnGroup spawngroup : StateManager.getInstance().getConfigurationManager().getSpawnGroups())
			{
				if (spawngroup.getName().toUpperCase().contains(args[0].toUpperCase()))
				{
					sender.sendMessage("SpawnGroupID: " + spawngroup.getId() + " - " + spawngroup.getName() + " world: " + spawngroup.getWorld() + " XYZ: " + spawngroup.getX() + "," + spawngroup.getY() + "," + spawngroup.getZ());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
