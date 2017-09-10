package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;

import net.md_5.bungee.api.ChatColor;

public class CommandLocal implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            StringBuilder builder = new StringBuilder();
            for (String value : args) {
                builder.append(value + " ");
            }
            String message = builder.toString();
            message = message.substring(0, message.length());
            
            if (message.equals(""))
            {
            	return false;            	
            }
            
            try {
				SoliniaPlayerAdapter.Adapt((Player)sender).say(message);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return true;
		} else {
			StringBuilder builder = new StringBuilder();
            for (String value : args) {
                builder.append(value + " ");
            }
            String message = builder.toString();
            message = message.substring(0, message.length());
            
            if (message.equals(""))
            {
            	return false;            	
            }
			
			Bukkit.broadcastMessage(ChatColor.RED + "Console:" + message + ChatColor.RESET);
			
			return true;
		}
	}
}
