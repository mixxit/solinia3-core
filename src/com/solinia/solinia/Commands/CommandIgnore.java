package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class CommandIgnore implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			
			if (args.length < 1)
				return false;
			
			Player targetPlayer = Bukkit.getPlayer(args[0]);
			if (targetPlayer == null)
			{
				sender.sendMessage("Player is not online to ignore");
				return true;
			}
			
			if (targetPlayer.getUniqueId().equals(((Player) sender).getUniqueId()))
			{
				sender.sendMessage("Cannot ignore self");
				return true;
			}
			
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				solPlayer.ignorePlayer(targetPlayer);
				
				sender.sendMessage("Done");
			} catch (CoreStateInitException e)
			{
				return false;
			}
            return true;
		}
		return false;
	}
}
