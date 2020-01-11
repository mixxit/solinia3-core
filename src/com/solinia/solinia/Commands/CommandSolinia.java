package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class CommandSolinia implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		sender.sendMessage("Solinia Stats:");
		try {
			sender.sendMessage("Cached Players: " + StateManager.getInstance().getPlayerManager().getCachedPlayersCount());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			if (args.length > 0 && args[0].equals("showspawns")) {
				StateManager.getInstance().toggleShowSpawns();
			}
				
			
			if (args[0].equals("patch")) {
				System.out.println("Patching");
				Utils.Patcher();
			}
		}
		
		return true;
	}
}
