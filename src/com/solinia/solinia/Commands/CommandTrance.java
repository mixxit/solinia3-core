package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CommandTrance implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			try
			{
				StateManager.getInstance().getEntityManager().toggleTrance(((Player) sender).getUniqueId());
			} catch (CoreStateInitException e)
			{
				return false;
			}
            return true;
		}
		return false;
	}
}
