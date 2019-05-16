package com.solinia.solinia.Commands;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Utils.Utils;

public class CommandListItems implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listitems"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length == 0)
		{
			sender.sendMessage("You must provide some text to filter the item name by");
			return true;
		}
		
		if (args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getItems(), sender, args,SoliniaItem.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		
		int found = 0;
		try {
				for(ISoliniaItem item : StateManager.getInstance().getConfigurationManager().getItems())
				{
					found++;
					if (item.getDisplayname().toUpperCase().contains(StringUtils.join(args, " ").toUpperCase()))
					{
						String itemmessage = item.getId() + " - " + item.getDisplayname();
						sender.sendMessage(itemmessage);
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
