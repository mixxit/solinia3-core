package com.solinia.solinia.Commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaNPCAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Models.EQMob;
import com.solinia.solinia.Utils.EQUtils;

public class CommandImportNPC implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.importnpc"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
        {
        	sender.sendMessage("Insufficient arguments (<fileid>)");
        	return false;
        }
        
        try {
			EQMob eqmob = StateManager.getInstance().getConfigurationManager().getImportNPCs(Integer.parseInt(args[0]));
			if (eqmob == null)
			{
				sender.sendMessage("Cannot find importmob by that ID");
				return true;
			}

			ISoliniaNPC npc = null;
			try {
				npc = SoliniaNPCAdapter.Adapt(eqmob, true);
				sender.sendMessage("NPC Created as ID: " + npc.getId());
				return true;

			} catch (CoreStateInitException e) {
				sender.sendMessage(e.getMessage());
				return true;
			} catch (Exception e) {
				sender.sendMessage(e.getMessage());
				return true;
			}

		} catch (NumberFormatException e1) {
			sender.sendMessage(e1.getMessage());
		} catch (CoreStateInitException e1) {
			sender.sendMessage(e1.getMessage());
		}
        return true;

	}
}