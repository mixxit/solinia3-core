package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditClass implements CommandExecutor {
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
		// CLASSID
		// Setting
		// NewValue

		if (args.length == 0) {
			return false;
		}

		int classid = Integer.parseInt(args[0]);

		if (args.length == 1) {
			try {
				ISoliniaClass solclass = StateManager.getInstance().getConfigurationManager().getClassObj(classid);
				if (solclass != null) {
					solclass.sendClassSettingsToSender(sender);
				} else {
					sender.sendMessage("CLASS ID doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
			}
		}

		if (args.length < 3) {
			sender.sendMessage("Insufficient arguments: classid setting value");
			return false;
		}

		String setting = args[1];

		String value = args[2];

		// for 'text' based settings like trigger texts etc, get the whole thing as
		// a string
		if (args.length > 3 && (setting.toLowerCase().contains("text") || setting.toLowerCase().contains("description") || setting.toLowerCase().contains("title"))) {
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

		if (classid < 1) {
			sender.sendMessage("Invalid class id");
			return false;
		}

		try {

			if (StateManager.getInstance().getConfigurationManager().getClassObj(classid) == null) {
				sender.sendMessage("Cannot locate class id: " + classid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editClass(classid, setting, value);
			sender.sendMessage("Updating setting on class");
		} catch (InvalidClassSettingException ne) {
			sender.sendMessage("Invalid class setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
