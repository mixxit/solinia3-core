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

			StateManager.getInstance().getConfigurationManager().editNPC(npcid,setting,value);
			sender.sendMessage("NPC setting updated, please run /soliniareload for the npc changes to activate");
		} catch (InvalidNpcSettingException ne)
		{
			sender.sendMessage("Invalid NPC setting");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
