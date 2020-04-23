package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaRaceCreationException;
import com.solinia.solinia.Factories.SoliniaRaceFactory;
import com.solinia.solinia.Interfaces.ISoliniaRace;

public class CommandAddRace implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
		{
			sender.sendMessage("This is a Player/Console only command");
			return false;
		}
		
		if (!sender.isOp() && !sender.hasPermission("solinia.addrace"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 9)
			return false;
		
		String racename = args[0];
		int strength = Integer.parseInt(args[1]);
		int stamina = Integer.parseInt(args[2]);
		int agility = Integer.parseInt(args[3]);
		int dexterity = Integer.parseInt(args[4]);
		int wisdom = Integer.parseInt(args[5]);
		int intelligence = Integer.parseInt(args[6]);
		int charisma = Integer.parseInt(args[7]);
		boolean adminonly = Boolean.parseBoolean(args[8]);
		
		try {
			ISoliniaRace race = SoliniaRaceFactory.CreateRace(racename,strength,stamina,agility,dexterity,wisdom,intelligence,charisma,adminonly);
			sender.sendMessage("* Race created [" + race.getId() + "]");
		} catch (CoreStateInitException | SoliniaRaceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}
		return true;
	}
}
