package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CommandIamVersion implements CommandExecutor {
	Solinia3CorePlugin plugin;

	public CommandIamVersion(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a player only command");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage("Missing version no");
			return true;
		}

		try {
			StateManager.getInstance().getPlayerManager().setPlayerVersion(((Player) sender).getUniqueId(), args[0]);
			StateManager.getInstance().getPlayerManager().checkPlayerModVersion((Player) sender);
			return true;
		} catch (CoreStateInitException e) {

		}
		return false;
	}
}
