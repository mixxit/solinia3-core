package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.NPCSpellList;

public class CommandListNpcSpells implements CommandExecutor 
{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listnpcspells"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
			if (args.length == 0)
			{
				// Return all
				for(NPCSpellList npc : StateManager.getInstance().getConfigurationManager().getNPCSpellLists())
				{
					sender.sendMessage("ID: " + npc.getId() + " - " + npc.getName());
				}
				
				return true;
			}
			
			// Filter for name
			for(NPCSpellList npc : StateManager.getInstance().getConfigurationManager().getNPCSpellLists())
			{
				if (npc.getName().toUpperCase().contains(args[0].toUpperCase()))
				{
					sender.sendMessage("ID: " + npc.getId() + " - " + npc.getName());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
