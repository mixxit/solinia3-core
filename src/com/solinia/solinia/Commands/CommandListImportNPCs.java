package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQMob;

public class CommandListImportNPCs implements CommandExecutor {
	private Solinia3CorePlugin plugin;

	public CommandListImportNPCs(Solinia3CorePlugin solinia3CorePlugin) {
		this.plugin = solinia3CorePlugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.importnpcs"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
        {
        	sender.sendMessage("Insufficient arguments (<name>)");
        	return false;
        }
    	
        String name = args[0];
        
		try {
			for(EQMob item : StateManager.getInstance().getConfigurationManager().getImportNPCs(name))
			{
				sender.sendMessage((int)item.getId() + " " + item.getName());		
			}
			
			return true;
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		return false;
	}

}
