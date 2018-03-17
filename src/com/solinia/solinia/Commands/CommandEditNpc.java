package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditNpc implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.editnpc"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}
		
		// Args
		// NPCID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int npcid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
				if (npc != null)
				{
					npc.sendNpcSettingsToSender(sender);
				} else {
					sender.sendMessage("NPC ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: npcid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		// for 'text' based npc settings like trigger texts etc, get the whole thing as a string
		if (args.length > 3 && (setting.toLowerCase().contains("text") || setting.toLowerCase().contains("title")))
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
		
		if (npcid < 1)
		{
			sender.sendMessage("Invalid npc id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getNPC(npcid) == null)
			{
				sender.sendMessage("Cannot locate npc id: " + npcid);
				return false;
			}
			
			if (StateManager.getInstance().getConfigurationManager().getNPC(npcid).isOperatorCreated() && !sender.isOp())
			{
				sender.sendMessage("This npc was op created and you are not an op. Only ops can edit op npcs");
				return false;
			}

			try
			{
			
				StateManager.getInstance().getConfigurationManager().editNPC(npcid,setting,value);
				sender.sendMessage("Updating setting on npc");
			} catch (java.io.IOException e)
			{
				sender.sendMessage("Failed to update NPC - If this was a request to change custom head data, the mojang servers may be unable to fetch the player skin, try the same command again in a few moments");
			}
		} catch (InvalidNpcSettingException ne)
		{
			sender.sendMessage("Invalid NPC setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
