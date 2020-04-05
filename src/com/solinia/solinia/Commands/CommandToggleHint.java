package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Models.HintSetting;

import net.md_5.bungee.api.ChatColor;

public class CommandToggleHint implements CommandExecutor {
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
		            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		            
		            if (solplayer == null)
		            {
		            	player.sendMessage("Failed to toggle, player does not exist");
		            	return false;	
		            }
		            
		            HINT hint = null;
		            if (args.length < 1)
	            	{
	            		sendHintStrings(player);
	            		player.sendMessage("Missing HINT type to toggle");
	            		return false;
	            	}
		            
		            for(HINT hintval : HINT.values())
		            {
		            	if (!hintval.name().toUpperCase().equals(args[0].toUpperCase()))
		            		continue;
		            	
		            	hint = hintval;
		            	break;
		            }
	            	
		            if (hint == null)
		            {
		            	player.sendMessage("HINT type not found");
		            	sendHintStrings(player);
		            	return true;
		            }
		            
		            HintSetting currentType = solplayer.getHintSetting(hint);
		            HintSetting newType = HintSetting.Off;
		            String currentTypeName = "OFF";
		            
		            if (currentType != HintSetting.Off)
		            {
			            switch(currentType)
			            {
						case ActionBar:
							currentTypeName = "ACTION_BAR";
							newType = HintSetting.Chat;
							break;
						case Chat:
							currentTypeName = "CHAT";
							newType = HintSetting.Off;
							break;
			            }
		            } else {
		            	currentTypeName = "OFF";
		            	newType = HintSetting.ActionBar;
		            }
		            
		            String newTypeName = "OFF";
		            if (newType != HintSetting.Off)
		            	newTypeName = newType.name();
		            
		            player.sendMessage("Toggling HINT " + ChatColor.AQUA + hint.name() + ChatColor.RESET + " from " + ChatColor.AQUA + currentTypeName + ChatColor.RESET + " to " + ChatColor.AQUA + newTypeName + ChatColor.RESET);
		            solplayer.setHintSetting(hint, newType);
		            
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        } else {
        	sender.sendMessage("For players only");
        }

        return true;
	}

	private void sendHintStrings(Player player) {
		String hintStrings = "";
		for(HINT hintval : HINT.values())
        {
        	hintStrings += hintval.name() + ", "; 
        }
		
		player.sendMessage("Available HINT toggles: " + hintStrings);
	}
}