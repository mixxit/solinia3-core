package com.solinia.solinia.Commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditFaction implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (!player.isOp()) {
				player.sendMessage("This is an operator only command");
				return false;
			}
		}

		// Args
		// FACTIONID
		// Setting
		// NewValue

		if (args.length == 0) {
			return false;
		}

		int factionid = Integer.parseInt(args[0]);

		if (args.length == 1) {
			try {
				ISoliniaFaction solfaction = StateManager.getInstance().getConfigurationManager().getFaction(factionid);
				if (solfaction != null) {
					solfaction.sendFactionSettingsToSender(sender);
				} else {
					sender.sendMessage("FACTION ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
			}
		}

		if (args.length < 3) {
			sender.sendMessage("Insufficient arguments: factionid setting value");
			return false;
		}

		String setting = args[1];

		String value = args[2];

		// for 'text' based settings
		// a string
		if (args.length > 3 && (setting.toLowerCase().contains("description") || setting.toLowerCase().contains("allygrants") || setting.toLowerCase().contains("title"))) {
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

		if (factionid < 1) {
			sender.sendMessage("Invalid faction id");
			return false;
		}

		try {

			if (StateManager.getInstance().getConfigurationManager().getFaction(factionid) == null) {
				sender.sendMessage("Cannot locate faction id: " + factionid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editFaction(factionid, setting, value);
			sender.sendMessage("Updating setting on faction");
		} catch (InvalidFactionSettingException ne) {
			sender.sendMessage("Invalid faction setting");
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
