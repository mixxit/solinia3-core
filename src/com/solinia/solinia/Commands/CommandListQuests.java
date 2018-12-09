package com.solinia.solinia.Commands;

import java.lang.reflect.Field;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaQuest;

public class CommandListQuests implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listquests"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
			
		// Filter for name
		
		String searchTerm = null;
		if (args.length > 0)
		{
			searchTerm = args[0];
		}
		
		int found = 0;
		try {
			if (searchTerm != null)
			{
				if (searchTerm.equals(".criteria"))
				{
					if (args.length < 3)
					{
						sender.sendMessage("Criteria must include a search term and value - ie .criteria name test");
					} else {
						String field = args[1];
						String value = args[2];
						
						try {
							Field f = SoliniaQuest.class.getDeclaredField(field);
							f.setAccessible(true);
							
							for(ISoliniaQuest entry : StateManager.getInstance().getConfigurationManager().getQuests())
							{
								String matchedValue = f.get(entry).toString(); 
								
								if (matchedValue.toLowerCase().equals(value.toLowerCase()))
								{
									found++;
									String message = entry.getId() + " - " + entry.getName();
									sender.sendMessage(message);
								}
							}
							
						} catch (NoSuchFieldException e) {
							sender.sendMessage("Could not be located by search criteria (field not found)");
						} catch (SecurityException e) {
							sender.sendMessage("Could not be located by search criteria (field is private)");
						} catch (IllegalArgumentException e) {
							sender.sendMessage("Could not be located by search criteria (argument issue)");
						} catch (IllegalAccessException e) {
							sender.sendMessage("Could not be located by search criteria (access issue)");
						}
						
						if (found == 0)
						{
							sender.sendMessage("Could not be located by search criteria (no matches)");
						}
						
					}
				} else {
					
					for(ISoliniaQuest entry : StateManager.getInstance().getConfigurationManager().getQuests())
					{
						if (entry.getName().toUpperCase().contains(searchTerm))
						{
							found++;
							String message = entry.getId() + " - " + entry.getName();
							sender.sendMessage(message);
						}
					}
					
					if (found == 0)
					{
						sender.sendMessage("Could not be located by search string");
					}
				}
			} else {
				for(ISoliniaQuest entry : StateManager.getInstance().getConfigurationManager().getQuests())
				{
					found++;
					String message = entry.getId() + " - " + entry.getName();
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
