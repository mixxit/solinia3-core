package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditSpawngroup implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.editspawngroup"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		// Args
		// SPAWNGROUPID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int spawngroupid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaSpawnGroup spawngroup = StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupid);
				if (spawngroup != null)
				{
					spawngroup.sendSpawnGroupSettingsToSender(sender);
				} else {
					sender.sendMessage("SPAWNGROUP ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: spawngroupid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		// for 'text' based spawngroup settings like trigger texts etc, get the whole thing as a string
		if (args.length > 3 && setting.toLowerCase().contains("text"))
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
		
		if (spawngroupid < 1)
		{
			sender.sendMessage("Invalid spawngroupid");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupid) == null)
			{
				sender.sendMessage("Cannot locate spawngroupid: " + spawngroupid);
				return false;
			}

			if (StateManager.getInstance().getConfigurationManager().getSpawnGroup(spawngroupid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This spawngroup was op created and you are not an op. Only ops can edit spawngroup npcs");
				return false;
			}
			
			try
			{
			
				StateManager.getInstance().getConfigurationManager().editSpawnGroup(spawngroupid,setting,value);
				sender.sendMessage("Updating setting on spawngroup");
			} catch (java.io.IOException e)
			{
				sender.sendMessage("Failed to update SpawnGroup");
			}
		} catch (InvalidSpawnGroupSettingException ne)
		{
			sender.sendMessage("Invalid SpawnGroup setting " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
