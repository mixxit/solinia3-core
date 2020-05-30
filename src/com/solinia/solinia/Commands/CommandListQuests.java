package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaQuest;
import com.solinia.solinia.Utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;

public class CommandListQuests implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listquests"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
			
		// Filter for name
		
		String searchTerm = null;
		if (args.length > 0)
		{
			searchTerm = args[0];
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				ChatUtils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getQuests(), sender, args,SoliniaQuest.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		int found = 0;
		try {
			if (searchTerm != null)
			{
				
					
					for(ISoliniaQuest entry : StateManager.getInstance().getConfigurationManager().getQuests())
					{
						if (entry.getName().toUpperCase().contains(searchTerm))
						{
							found++;
							String message = "" + ChatColor.GOLD + entry.getId() + ChatColor.RESET + " - " + entry.getName();
							sender.sendMessage(message);
						}
					}
					
					if (found == 0)
					{
						sender.sendMessage("Could not be located by search string");
					}
				
			} else {
				for(ISoliniaQuest entry : StateManager.getInstance().getConfigurationManager().getQuests())
				{
					found++;
					String message = "" + ChatColor.GOLD + entry.getId() + ChatColor.RESET + " - " + entry.getName();
					sender.sendMessage(message);
				}
				
				if (found == 0)
				{
					sender.sendMessage("Could not be located by search string");
				}
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
