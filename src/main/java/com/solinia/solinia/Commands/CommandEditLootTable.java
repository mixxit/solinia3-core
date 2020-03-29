package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditLootTable implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.editloottable"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("Insufficient arguments: LootTableid");
			return false;
		}
		
		// Args
		// LootTableID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int LootTableid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaLootTable LootTable = StateManager.getInstance().getConfigurationManager().getLootTable(LootTableid);
				if (LootTable != null)
				{
					LootTable.sendLootTableSettingsToSender(sender);
				} else {
					sender.sendMessage("LootTable ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: LootTableid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (LootTableid < 1)
		{
			sender.sendMessage("Invalid LootTable id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getLootTable(LootTableid) == null)
			{
				sender.sendMessage("Cannot locate LootTable id: " + LootTableid);
				return false;
			}
			
			StateManager.getInstance().getConfigurationManager().editLootTable(LootTableid,setting,value);
			sender.sendMessage("Updating setting on LootTable");
		} catch (InvalidLootTableSettingException ne)
		{
			sender.sendMessage("Invalid LootTable setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
