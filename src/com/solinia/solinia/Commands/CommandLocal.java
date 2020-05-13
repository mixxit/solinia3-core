package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.PlayerUtils;

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
            	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
            	if (!solPlayer.getLastChatCheck())
            		sender.sendMessage(ChatColor.GRAY + "You are talking in roleplay-chat too fast");
            	else
            		solPlayer.say(message);
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
			
            PlayerUtils.BroadcastPlayers("Console:" + message);
            
			
			return true;
		}
	}
}
