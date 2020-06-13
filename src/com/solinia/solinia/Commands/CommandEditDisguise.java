package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidDisguiseSettingException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaDisguise;

public class CommandEditDisguise implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editdisguise"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: disguiseid");
			return false;
		}
		
		// Args
		// Disguiseid
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int Disguiseid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				SoliniaDisguise Disguise = StateManager.getInstance().getConfigurationManager().getDisguise(Disguiseid);
				if (Disguise != null)
				{
					Disguise.sendDisguiseSettingsToSender(sender);
				} else {
					sender.sendMessage("Disguise ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: Disguiseid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (Disguiseid < 1)
		{
			sender.sendMessage("Invalid Disguiseid");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getDisguise(Disguiseid) == null)
			{
				sender.sendMessage("Cannot locate Disguise id: " + Disguiseid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editDisguise(Disguiseid,setting,value);
			sender.sendMessage("Updating setting on Disguise");
		} catch (InvalidDisguiseSettingException ne)
		{
			sender.sendMessage("Invalid Disguise setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}