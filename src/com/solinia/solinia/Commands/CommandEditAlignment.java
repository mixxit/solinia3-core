package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditAlignment implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (!sender.isOp() && !sender.hasPermission("solinia.editalignment"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}

		// Args
		// ALIGNMENTID
		// Setting
		// NewValue

		if (args.length == 0) {
			return false;
		}

		String alignmentid = args[0];

		if (args.length == 1) {
			try {
				ISoliniaAlignment solalignment = StateManager.getInstance().getConfigurationManager().getAlignment(alignmentid);
				if (solalignment != null) {
					solalignment.sendAlignmentSettingsToSender(sender);
				} else {
					sender.sendMessage("ALIGNMENTID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
			}
		}

		if (args.length < 3) {
			sender.sendMessage("Insufficient arguments: alignmentid setting value");
			return false;
		}

		String setting = args[1];

		String value = args[2];

		// for 'text' based settings like trigger texts etc, get the whole thing as
		// a string
		if (args.length > 3 && setting.toLowerCase().contains("description")) {
			value = "";
			int current = 0;
			for (String entry : args) {
				current++;
				if (current <= 2)
					continue;

				value = value + entry + " ";
			}

			value = value.trim();
		}

		if (alignmentid == null || alignmentid.equals("")) {
			sender.sendMessage("Invalid alignment id");
			return false;
		}

		try {

			if (StateManager.getInstance().getConfigurationManager().getAlignment(alignmentid) == null) {
				sender.sendMessage("Cannot locate alignment id: " + alignmentid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editAlignment(alignmentid, setting, value);
			sender.sendMessage("Updating setting on alignment");
		} catch (InvalidAlignmentSettingException ne) {
			sender.sendMessage("Invalid alignmentid setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
