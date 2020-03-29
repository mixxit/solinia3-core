package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;

import net.md_5.bungee.api.ChatColor;

public class CommandListZones implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listzones"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
			{
				sender.sendMessage("ZoneID: " + ChatColor.GOLD + zone.getId() + ChatColor.RESET + " - " + zone.getName() + " hotzone: " + zone.isHotzone());
			}
			
			return true;
		}
		
		// Filter for name
		for(SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
		{
			if (zone.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("ZoneID: " + ChatColor.GOLD + zone.getId() + ChatColor.RESET + " - " + zone.getName() + " hotzone: " + zone.isHotzone());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
