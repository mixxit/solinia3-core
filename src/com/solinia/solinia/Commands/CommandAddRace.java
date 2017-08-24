package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaRaceCreationException;
import com.solinia.solinia.Factories.SoliniaRaceFactory;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

public class CommandAddRace implements CommandExecutor {
	
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
			ISoliniaRace soliniaRace = SoliniaRaceFactory.CreateRace(racename,strength,stamina,agility,dexterity,wisdom,intelligence,charisma,adminonly);
			StateManager.getInstance().getConfigurationManager().addRace(soliniaRace);
			sender.sendMessage("* Race created");
		} catch (CoreStateInitException | SoliniaRaceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage("Error: " + e.getMessage());
			return true;
		}
		return true;
	}
}
