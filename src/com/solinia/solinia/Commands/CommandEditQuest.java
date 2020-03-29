package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditQuest implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editquest"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		// Args
		// ID
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
				ISoliniaQuest entry = StateManager.getInstance().getConfigurationManager().getQuest(id);
				if (entry != null)
				{
					entry.sendQuestSettingsToSender(sender);
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
		
		String[] additional = new String[0];
		if (args.length > 3)
		{
			additional = new String[args.length - 3];
			for(int i = 0; i < args.length; i++)
			{
				if (i < 3)
					continue;
				
				additional[i-3] = args[i];
			}
		}
		
		if (id < 1)
		{
			sender.sendMessage("Invalid id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getQuest(id) == null)
			{
				sender.sendMessage("Cannot locate id: " + id);
				return false;
			}
			
			StateManager.getInstance().getConfigurationManager().editQuest(sender, id,setting,value,additional);
			sender.sendMessage("Updating setting");
		} catch (InvalidQuestSettingException ne)
		{
			sender.sendMessage("Invalid setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
