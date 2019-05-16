package com.solinia.solinia.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Utils.Utils;

public class CommandListNPCs implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listnpcs"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
			{
				sender.sendMessage("NPCID: " + npc.getId() + " - " + npc.getName());
			}
			
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getNPCs(), sender, args,SoliniaNPC.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs())
		{
			if (npc.getName().toUpperCase().contains(StringUtils.join(args, " ").toUpperCase()))
			{
				sender.sendMessage("NPCID: " + npc.getId() + " - " + npc.getName());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
