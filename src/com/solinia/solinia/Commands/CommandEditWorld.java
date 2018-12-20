package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidWorldSettingException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaWorld;

public class CommandEditWorld implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editworld"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: worldid");
			return false;
		}
		
		// Args
		// worldid
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int id = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				SoliniaWorld entry = StateManager.getInstance().getConfigurationManager().getWorld(id);
				if (entry != null)
				{
					entry.sendWorldSettingsToSender(sender);
				} else {
					sender.sendMessage("ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: id setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (id < 1)
		{
			sender.sendMessage("Invalid id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getWorld(id) == null)
			{
				sender.sendMessage("Cannot locate id: " + id);
				return false;
			}
			
			StateManager.getInstance().getConfigurationManager().editWorld(id,setting,value);
			sender.sendMessage("Updating setting");
		} catch (InvalidWorldSettingException ne)
		{
			sender.sendMessage("Invalid setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
