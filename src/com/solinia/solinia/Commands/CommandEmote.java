package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.md_5.bungee.api.ChatColor;

public class CommandEmote implements CommandExecutor 
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
            Player player = (Player) sender;
            
            if (args.length < 1)
            {
            	player.sendMessage("Insufficient arguments (<emote>)");
            	return false;
            }
            
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to emote, player does not exist");
	            	return false;	
	            }
	            
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
	            
	            message = message.replaceAll("(?i)fuck", "shelderhoof");
	    		message = message.replaceAll("(?i)shit", "qaraf");
	            
	    		
	    		solplayer.emote(solplayer.getFullName() + " " + message, false);
            } catch (CoreStateInitException e)
            {
            	player.sendMessage(e.getMessage());
            }
        }

        return true;
	}
}
