package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandSkillCheck implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to emote, player does not exist");
	            	return false;	
	            }
	            
	            List<String> skills = new ArrayList<String>();
	            skills.add("athletics");
	            skills.add("acrobatics");
	            skills.add("sleightofhand");
	            skills.add("stealth");
	            skills.add("arcana");
	            skills.add("history");
	            skills.add("investigation");
	            skills.add("nature");
	            skills.add("religion");
	            skills.add("animalhandling");
	            skills.add("insight");
	            skills.add("medicine");
	            skills.add("perception");
	            skills.add("survival");
	            skills.add("deception");
	            skills.add("intimidation");
	            skills.add("performance");
	            skills.add("persuasion");
	            
	            String skill = "perception";
	            
	            if (args.length == 0)
	            {
	            	player.sendMessage("Insufficient arguments, must provide skill from this list: " + String.join(",", skills));
	            	return false;
	            } else {
	            	skill = args[0].toLowerCase();
	            	if (!skills.contains(skill))
	            	{
	            		player.sendMessage("Invalid argument [" + skill + "], must provide skill from this list: " + String.join(", ", skills));
	            		return false;
	            	}
	            }
	            
	            String message = ChatColor.AQUA + " * " + solplayer.getFullName() + " makes a skill check for " + skill + ". They roll: " + Utils.RandomBetween(0, 20) + "/20" + ChatColor.RESET;
	            solplayer.emote(message);
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}
}
