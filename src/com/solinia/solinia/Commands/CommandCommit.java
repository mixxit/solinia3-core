package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
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
						sender.isOp() || sender instanceof ConsoleCommandSender 
				)) {
			if (args[0].equals("provider")) {
				Utils.RecommitNpcs();
				Utils.RecommitSpawnGroups();
				
			}
			
			if (args.length > 0 && args[0].equals("showtown")) {
				if (args.length > 1)
					StateManager.getInstance().renderTownsOnDynmap = args[1];
				else
					StateManager.getInstance().renderTownsOnDynmap = "";
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
