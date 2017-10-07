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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

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
				String classBuilder = "";
				for(ISoliniaClass solclass : classes)
				{
					if (solclass.getValidRaces().contains(race.getId()))
						classBuilder += solclass.getName() + " ";
				}
				
				TextComponent tc = new TextComponent();
				tc.setText(ChatColor.RED + "~ RACE: " + ChatColor.GOLD + race.getName().toUpperCase() + ChatColor.GRAY + " [" + race.getId() + "] - " + ChatColor.RESET);
				TextComponent tc2 = new TextComponent();
				tc2.setText("Hover for more details");
				String details = ChatColor.GOLD + race.getName() + ChatColor.RESET + "\nRecommended Alignment: " + ChatColor.GOLD + race.getAlignment() + ChatColor.RESET + "\n" + race.getDescription() + "\nSTR: " + ChatColor.GOLD + race.getStrength() + ChatColor.RESET + " STA: " + ChatColor.GOLD + race.getStamina() + ChatColor.RESET + " AGI: " + ChatColor.GOLD + race.getAgility() + ChatColor.RESET + " DEX: " + ChatColor.GOLD + race.getDexterity() + ChatColor.RESET + " INT: " + ChatColor.GOLD + race.getIntelligence() + ChatColor.RESET + " WIS: " + ChatColor.GOLD + race.getWisdom() + ChatColor.RESET + " CHA: " + ChatColor.GOLD + race.getCharisma() + ChatColor.GOLD + " \nClasses: " + ChatColor.RESET + classBuilder;
				tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
				tc.addExtra(tc2);
				sender.spigot().sendMessage(tc);	
			}
		
		} catch (Exception e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
