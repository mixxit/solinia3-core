package com.solinia.solinia.Commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordContentTeamChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

public class CommandCommit implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender)) {
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.isOp()) {
				player.sendMessage("This is an operator only command");
				return true;
			}
		}

		sender.sendMessage("* Executing State Commit");

		try {
			StateManager.getInstance().Commit();
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}

		if (args.length > 0 && 
				(
						sender instanceof ConsoleCommandSender 
				|| sender instanceof DiscordDefaultChannelCommandSender 
				|| sender instanceof DiscordContentTeamChannelCommandSender 
				|| sender instanceof DiscordAdminChannelCommandSender
				)) {
			if (args[0].equals("provider")) {
				Utils.RecommitNpcs();
				
			}
			
			if (args[0].equals("patch")) {
				System.out.println("Patching");
				Utils.Patcher();
			}
		}

		sender.sendMessage("* Completed State Commit");
		return true;
	}
}
