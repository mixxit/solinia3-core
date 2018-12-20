package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordContentTeamChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

public class CommandFlushDiscord implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender)) {
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}

		if (!sender.isOp() && !sender.hasPermission("solinia.flushdiscord"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}

		sender.sendMessage("* Flushing discord");

		StateManager.getInstance().getChannelManager().clearDiscordQueue();

		sender.sendMessage("* Completed Flush");
		return true;
	}
}
