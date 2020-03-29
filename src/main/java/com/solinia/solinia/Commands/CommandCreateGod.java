package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaGodFactory;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateGod implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		try {

			if (!sender.isOp() && !sender.hasPermission("solinia.creategod"))
			{
				sender.sendMessage("You do not have permission to access this command");
				return false;
			}

			if (args.length < 1) {
				sender.sendMessage("Insufficient arguments: name");
				return false;
			}

			// args
			// defaultnpcid
			// spawngroupname
			String godname = args[0];

			if (godname.equals("")) {
				sender.sendMessage("Blank name not allowed when creating god");
				return false;
			}

			godname = godname.replace(" ", "_").toUpperCase();

			if (StateManager.getInstance().getConfigurationManager().getGod(godname) != null) {
				sender.sendMessage("God already exists");
				return true;
			}

			ISoliniaGod god = SoliniaGodFactory.CreateGod(godname);
			sender.sendMessage("Created god: " + god.getId());
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}

		return true;
	}
}
