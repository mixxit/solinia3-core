package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class CommandLastname implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		Player player = (Player)sender;
		try {
			player.sendMessage("Old Lastname: " + SoliniaPlayerAdapter.Adapt(player).getLastname());
			
			if (args.length == 0)
				return false;
			
			String lastname = "";
			
			if (lastname.equals("reset"))
			{
				lastname = "";
			} else {
				if (!StateManager.getInstance().getPlayerManager().IsNewNameValid(SoliniaPlayerAdapter.Adapt(player).getForename(), lastname))
				{
					player.sendMessage("Forename + Lastname length must be between 3 and 14 characters and not in use by other players");
					
					String newname = SoliniaPlayerAdapter.Adapt(player).getForename();
					if (!lastname.equals(""))
						newname = SoliniaPlayerAdapter.Adapt(player).getForename() + "_" + lastname;
					
					player.sendMessage("Target name [" + newname + "] length: " + newname.length());
					return false;
				}
			}
			
			SoliniaPlayerAdapter.Adapt(player).setLastname(lastname);
			sender.sendMessage("* Lastname set");
			
			SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
			return true;
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}

		return false;
	}
}
