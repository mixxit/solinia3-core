package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAAAbility;
import com.solinia.solinia.Utils.Utils;

public class CommandListAAs implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{

			Player player = (Player) sender;
			
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		try
		{
		
		if (args.length == 0)
		{
			// Return all
			for(ISoliniaAAAbility aa : StateManager.getInstance().getConfigurationManager().getAAAbilities())
			{
				sender.sendMessage("AAAbilityID: " + aa.getId() + " - " + aa.getName() + " Sysname: " + aa.getSysname());
			}
			
			return true;
		}
		
		if (args.length > 0 && args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getAAAbilities(), sender, args,SoliniaAAAbility.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Filter for name
		for(ISoliniaAAAbility aa : StateManager.getInstance().getConfigurationManager().getAAAbilities())
		{
			if (aa.getName().toUpperCase().contains(args[0].toUpperCase()))
			{
				sender.sendMessage("AAAbilityID: " + aa.getId() + " - " + aa.getName() + " Sysname: " + aa.getSysname());
			}
		}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
