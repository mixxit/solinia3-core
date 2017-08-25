package com.solinia.solinia.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

import net.md_5.bungee.api.ChatColor;

public class CommandRoll implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (args.length == 0)
            {
            	player.sendMessage("Insufficient arguments, must provide MAXNUMBER");
            	return false;
            }
            
            if (!StringUtils.isNumeric(args[0]))
            {
            	player.sendMessage("Invalid argument, must provide a number");
            	return false;
            }
            
            int maxnumber = 1;
            try
            {
            	maxnumber = Integer.parseInt(args[0]);
            } catch (Exception e)
            {
            	player.sendMessage("Invalid number");
            	return false;            	
            }

            int roll = 1 + (int)(Math.random() * ((maxnumber - 1) + 1));
            
            try
            {
                String message = ChatColor.AQUA + " * " + SoliniaPlayerAdapter.Adapt(player).getFullName() + " rolls 1d"+maxnumber+". It's a "+roll+"!";
            	SoliniaPlayerAdapter.Adapt(player).say(message);
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}
}
