package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandSpawnNpc implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp() && !player.hasPermission("solinia.spawnnpc"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
		}

		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage("Insufficient arguments (npc)");
			return false;
		}

		int itemid = Integer.parseInt(args[0]);
		try {
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(itemid);
			
			if (npc != null) {
				
				npc.Spawn(player.getLocation(), 1);
			} else {
				player.sendMessage("Cannot find NPC by ID");
				return true;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}
