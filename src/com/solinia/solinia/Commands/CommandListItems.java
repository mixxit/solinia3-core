package com.solinia.solinia.Commands;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;

public class CommandListItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listitems"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		if (args.length == 0)
		{
			sender.sendMessage("You must provide some text to filter the item name by");
			return true;
		}
		
		// Filter for name
		
		int found = 0;
		try {
			
			if (args[0].equals(".criteria"))
			{
				if (args.length < 3)
				{
					sender.sendMessage("Criteria must include a search term and value - ie .criteria name aegolism");
				} else {
					String field = args[1];
					String value = args[2];
					
					try {
						Field f = SoliniaItem.class.getDeclaredField(field);
						f.setAccessible(true);
						
						for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
						{
							String matchedValue = f.get(item).toString(); 
							
							if (matchedValue.contains(value))
							{
								found++;
								String itemmessage = item.getId() + " - " + item.getDisplayname();
								sender.sendMessage(itemmessage);

							}
						}
						
					} catch (NoSuchFieldException e) {
						sender.sendMessage("Item could not be located by search criteria (field not found)");
					} catch (SecurityException e) {
						sender.sendMessage("Item could not be located by search criteria (field is private)");
					} catch (IllegalArgumentException e) {
						sender.sendMessage("Item could not be located by search criteria (argument issue)");
					} catch (IllegalAccessException e) {
						sender.sendMessage("Item could not be located by search criteria (access issue)");
					}
					
					if (found == 0)
					{
						sender.sendMessage("Spell could not be located by search criteria (no matches)");
					}
					
				}
			} else {
				for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
				{
					found++;
					if (item.getDisplayname().toUpperCase().contains(StringUtils.join(args, " ").toUpperCase()))
					{
						String itemmessage = item.getId() + " - " + item.getDisplayname();
						sender.sendMessage(itemmessage);
					}
				}
			}
			
			if (found == 0)
			{
				sender.sendMessage("Item could not be located by search string");
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
}
