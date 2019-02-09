package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordBotspamChannelCommandSender;
import com.solinia.solinia.Providers.DiscordContentTeamChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

public class CommandOoc implements CommandExecutor {
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
				SoliniaPlayerAdapter.Adapt((Player)sender).ooc(message);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return true;
		} else {
			if ((
					sender instanceof ConsoleCommandSender || 
					sender instanceof DiscordDefaultChannelCommandSender || 
					sender instanceof DiscordContentTeamChannelCommandSender || 
					sender instanceof DiscordBotspamChannelCommandSender ||
					sender instanceof DiscordAdminChannelCommandSender
					))
			{
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
				
	            Utils.BroadcastPlayers("Console:" + message);
			}
		}
		return false;
	}
}
