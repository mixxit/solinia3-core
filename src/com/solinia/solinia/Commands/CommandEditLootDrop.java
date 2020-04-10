package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditLootDrop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editlootdrop"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: lootdropid");
			return false;
		}
		
		// Args
		// LootDropID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int LootDropid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaLootDrop LootDrop = StateManager.getInstance().getConfigurationManager().getLootDrop(LootDropid);
				if (LootDrop != null)
				{
					LootDrop.sendLootDropSettingsToSender(sender);
				} else {
					sender.sendMessage("LootDrop ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: LootDropid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		if (args.length > 3)
		{
			value = "";
			int current = 0;
			for(String entry : args)
			{
				current++;
				if (current <= 2)
					continue;
				
				value = value + entry + " ";
			}
			
			value = value.trim();
		}
		
		
		if (LootDropid < 1)
		{
			sender.sendMessage("Invalid LootDrop id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getLootDrop(LootDropid) == null)
			{
				sender.sendMessage("Cannot locate LootDrop id: " + LootDropid);
				return false;
			}
			
			StateManager.getInstance().getConfigurationManager().editLootDrop(LootDropid,setting,value);
			sender.sendMessage("Updating setting on LootDrop");
		} catch (InvalidLootDropSettingException ne)
		{
			sender.sendMessage("Invalid LootDrop setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
