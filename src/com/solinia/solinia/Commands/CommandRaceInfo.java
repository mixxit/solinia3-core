package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;

public class CommandRaceInfo implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;
		
		sender.sendMessage("Race Information:");
		
		try
		{
			List<ISoliniaClass> classes = StateManager.getInstance().getConfigurationManager().getClasses();
			for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces())
			{
				sender.sendMessage(race.getName().toUpperCase());
				sender.sendMessage("STR: " + race.getStrength() + " STA: " + race.getStamina() + " AGI: " + race.getAgility() + " DEX: " + race.getDexterity() + " INT: " + race.getIntelligence() + " WIS: " + race.getWisdom() + " CHA: " + race.getCharisma());
				String classBuilder = "";
				for(ISoliniaClass solclass : classes)
				{
					if (solclass.getValidRaces().contains(race.getId()))
						classBuilder += solclass.getName() + " ";
				}
				sender.sendMessage("Classes: " + race.getName() + ": " + classBuilder);
			}
		
		} catch (Exception e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
