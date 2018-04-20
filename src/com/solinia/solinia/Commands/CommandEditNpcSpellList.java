package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSpellListSettingException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.NPCSpellList;

public class CommandEditNpcSpellList implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
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
		// NPCSPELLLISTID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int npcspelllistid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				NPCSpellList npcspelllist = StateManager.getInstance().getConfigurationManager().getNPCSpellList(npcspelllistid);
				if (npcspelllist != null)
				{
					npcspelllist.sendSettingsToSender(sender);
				} else {
					sender.sendMessage("NPC Spell List ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: npcspelllistid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		// for 'text' based npc settings like trigger texts etc, get the whole thing as a string
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
		
		if (npcspelllistid < 1)
		{
			sender.sendMessage("Invalid npcspelllistid id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getNPCSpellList(npcspelllistid) == null)
			{
				sender.sendMessage("Cannot locate npcspelllist id: " + npcspelllistid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().getNPCSpellList(npcspelllistid).editSetting(setting,value);
			sender.sendMessage("Updating setting on npcspelllist");
			
		} catch (InvalidNpcSpellListSettingException ne)
		{
			sender.sendMessage("Invalid NPC setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
