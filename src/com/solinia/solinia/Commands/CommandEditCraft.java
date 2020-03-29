package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;

public class CommandEditCraft implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editcraft"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: craftid");
			return false;
		}
		
		// Args
		// craftid
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int craftid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				SoliniaCraft craft = StateManager.getInstance().getConfigurationManager().getCraft(craftid);
				if (craft != null)
				{
					craft.sendCraftSettingsToSender(sender);
				} else {
					sender.sendMessage("Craft ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: craftid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (craftid < 1)
		{
			sender.sendMessage("Invalid craftid");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getCraft(craftid) == null)
			{
				sender.sendMessage("Cannot locate craft id: " + craftid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editCraft(craftid,setting,value);
			sender.sendMessage("Updating setting on Craft");
		} catch (InvalidCraftSettingException ne)
		{
			sender.sendMessage("Invalid craft setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}