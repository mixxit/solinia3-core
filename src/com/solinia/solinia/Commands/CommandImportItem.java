package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EQItem;

public class CommandImportItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.importitem"))
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
			EQItem eqitem = StateManager.getInstance().getConfigurationManager().getImportItems(Integer.parseInt(args[0]));
			if (eqitem == null)
			{
				sender.sendMessage("Cannot find importitem by that ID");
				return true;
			}
				
			
			ISoliniaItem item = null;
			try {
				item = SoliniaItemAdapter.Adapt(eqitem, true);
				sender.sendMessage("Item Created as ID: " + item.getId());
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