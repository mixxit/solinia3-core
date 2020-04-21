package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Managers.StateManager;

public class CommandEditAA implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		if (!sender.isOp() && !sender.hasPermission("solinia.editaa"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}

		// Args
		// aaid
		// Setting
		// NewValue

		if (args.length == 0) {
			return false;
		}

		int aaid = Integer.parseInt(args[0]);

		if (args.length == 1) {
			try {
				ISoliniaAAAbility aaability = StateManager.getInstance().getConfigurationManager().getAAAbility(aaid);
				if (aaability != null) {
					aaability.sendAASettingsToSender(sender);
				} else {
					sender.sendMessage("aaid doesnt exist");
				}
				return true;
			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
			}
		}

		if (args.length < 3) {
			sender.sendMessage("Insufficient arguments: aaid setting value");
			return false;
		}

		String setting = args[1];

		String value = args[2];

		if (aaid < 1) {
			sender.sendMessage("Invalid aaid");
			return false;
		}

		try {

			if (StateManager.getInstance().getConfigurationManager().getAAAbility(aaid) == null) {
				sender.sendMessage("Cannot locate aaid: " + aaid);
				return false;
			}

			StateManager.getInstance().getConfigurationManager().editAAAbility(aaid, setting, value);
			sender.sendMessage("Updating setting on aaability");
		} catch (InvalidAASettingException ne) {
			sender.sendMessage("Invalid aaability setting: " + ne.getMessage());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}
