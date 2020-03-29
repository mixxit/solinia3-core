package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditRace implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (!player.isOp()) {
				player.sendMessage("This is an operator only command");
				return false;
			}
		}

		// Args
		// RACEID
		// Setting
		// NewValue

		if (args.length == 0) {
			return false;
		}

		int raceid = Integer.parseInt(args[0]);

		if (args.length == 1) {
			try {
				ISoliniaRace solrace = StateManager.getInstance().getConfigurationManager().getRace(raceid);
				if (solrace != null) {
					solrace.sendRaceSettingsToSender(sender);
				} else {
					sender.sendMessage("RACE ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
			}
		}

		if (args.length < 3) {
			sender.sendMessage("Insufficient arguments: raceid setting value");
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

		if (raceid < 1) {
			sender.sendMessage("Invalid race id");
			return false;
		}

		try {

			if (StateManager.getInstance().getConfigurationManager().getRace(raceid) == null) {
				sender.sendMessage("Cannot locate race id: " + raceid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editRace(raceid, setting, value);
			sender.sendMessage("Updating setting on race");
		} catch (InvalidRaceSettingException ne) {
			sender.sendMessage("Invalid race setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
