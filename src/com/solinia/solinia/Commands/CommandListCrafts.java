package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.Utils;

public class CommandListCrafts implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listcrafts"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(SoliniaCraft entity : StateManager.getInstance().getConfigurationManager().getCrafts())
			{
				sender.sendMessage("CraftID: " + entity.getId() + " - " + entity.getRecipeName() + " outputId: " + entity.getOutputItem());
			}
			
			return true;
		}
		
		if (args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getCrafts(), sender, args,SoliniaCraft.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(SoliniaCraft entity : StateManager.getInstance().getConfigurationManager().getCrafts())
		{
			if (entity.getRecipeName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("CraftID: " + entity.getId() + " - " + entity.getRecipeName() + " outputId: " + entity.getOutputItem());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
