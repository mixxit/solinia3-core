package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		// Args
		// ITEMID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int itemid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
				if (item != null)
				{
					item.sendItemSettingsToSender(sender);
				} else {
					sender.sendMessage("ITEM ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: itemid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (args.length > 3 && setting.toLowerCase().contains("lore")) {
			value = "";
			int current = 0;
			for (String entry : args) {
				current++;
				if (current <= 2)
					continue;

				value = value + entry + " ";
			}

			value = value.trim();
		}
		
		if (itemid < 1)
		{
			sender.sendMessage("Invalid item id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getItem(itemid) == null)
			{
				sender.sendMessage("Cannot locate item id: " + itemid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editItem(itemid,setting,value);
			sender.sendMessage("Updating setting on item");
		} catch (InvalidItemSettingException ne)
		{
			sender.sendMessage("Invalid item setting");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
