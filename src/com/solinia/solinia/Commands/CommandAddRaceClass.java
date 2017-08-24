package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

public class CommandAddRaceClass implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return true;
			}
		}
		
		if (args.length < 2)
			return false;
		
		String racename = args[0];
		String classname = args[1];
		
		try {
			ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(racename);
			ISoliniaClass classobj = StateManager.getInstance().getConfigurationManager().getClassObj(classname);
			
			if (race == null)
			{
				sender.sendMessage("Race does not exist");
				return false;
			}

			if (classobj == null)
			{
				sender.sendMessage("Class does not exist");
				return false;
			}
			
			if (classobj.getValidRaces() != null)
			if (classobj.getValidRaces().contains(race.getId()))
			{
				sender.sendMessage("Race already contains class");
				return false;
			}

			StateManager.getInstance().getConfigurationManager().AddRaceClass(race.getId(),classobj.getId());
			sender.sendMessage("* Race class combination added");
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
		
	}

}
