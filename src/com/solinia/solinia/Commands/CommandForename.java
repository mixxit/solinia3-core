package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CommandForename implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		Player player = (Player)sender;
		try {
			player.sendMessage("Old Forename: " + SoliniaPlayerAdapter.Adapt(player).getForename());
			if (args.length == 0)
				return false;
			
			if (!StateManager.getInstance().getPlayerManager().IsNewNameValid(args[0], SoliniaPlayerAdapter.Adapt(player).getLastname()))
			{
				player.sendMessage("Forename + Lastname length must be between 3 and 14 characters and not in use by other players");
				
				String newname = args[0];
				if (!SoliniaPlayerAdapter.Adapt(player).getLastname().equals(""))
					newname = args[0] + "_" + SoliniaPlayerAdapter.Adapt(player).getLastname();
				
				player.sendMessage("Target name [" + newname + "] length: " + newname.length());
				return false;
			}
				
			SoliniaPlayerAdapter.Adapt(player).setForename(args[0]);
			sender.sendMessage("* Forename set");
			SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
			return true;
			
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		
		return false;
	}
}
