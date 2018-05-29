package com.solinia.solinia.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandListNPCs implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp() && !player.hasPermission("solinia.listnpcs"))
			{
				player.sendMessage("You do not have permission to access this command");
				return false;
			}
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
