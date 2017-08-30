package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditSpell implements CommandExecutor {
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
		// SPELLID
		// Setting
		// NewValue
		
		if (args.length == 0)
		{
			return false;
		}

		int spellid = Integer.parseInt(args[0]);
		
		if (args.length == 1)
		{
			try
			{
				ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(spellid);
				if (spell != null)
				{
					spell.sendSpellSettingsToSender(sender);
				} else {
					sender.sendMessage("SPELL ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e)
			{
				sender.sendMessage(e.getMessage());
			}
		}

		
		if (args.length < 3)
		{
			sender.sendMessage("Insufficient arguments: spellid setting value");
			return false;
		}
		
		String setting = args[1];
		
		String value = args[2];
		
		if (spellid < 1)
		{
			sender.sendMessage("Invalid spellid id");
			return false;
		}
		
		try
		{

			if (StateManager.getInstance().getConfigurationManager().getSpell(spellid) == null)
			{
				sender.sendMessage("Cannot locate spell id: " + spellid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editSpell(spellid,setting,value);
			sender.sendMessage("Updating setting on spell");
		} catch (InvalidSpellSettingException ne)
		{
			sender.sendMessage("Invalid Spell setting");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
