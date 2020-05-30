package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;

public class CommandListFactions implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listfactions"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions())
			{
				sender.sendMessage("FactionID: " + ChatColor.GOLD + faction.getId() + ChatColor.RESET + " - " + faction.getName() + " base: " + faction.getBase());
			}
			
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getFactions(), sender, args,SoliniaFaction.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(ISoliniaFaction faction : StateManager.getInstance().getConfigurationManager().getFactions())
		{
			if (faction.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("FactionID: " + ChatColor.GOLD + faction.getId() + ChatColor.RESET + " - " + faction.getName() + " base: " + faction.getBase());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
