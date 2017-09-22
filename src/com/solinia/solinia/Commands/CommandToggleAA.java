package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandToggleAA implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
	            Player player = (Player) sender;
	            try
	            {
	            
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (solplayer == null)
	            {
	            	player.sendMessage("Failed to toggle AA, player does not exist");
	            	return false;	
	            }
	            
	            if (solplayer.getLevel() >= 20)
	            {
		            if (solplayer.getAapct() > 0)
		            {
		            	player.sendMessage("* All of your experience will now go to normal experience");
		            	solplayer.setAapct(0);
		            } else {
		            	player.sendMessage("* All of your experience will now go to AA experience");
		            	solplayer.setAapct(100);
		            }
	            } else {
	            	player.sendMessage("You must be minimum level 20 to enable AA experience");
	            }
            } catch (CoreStateInitException e)
            {
            	e.printStackTrace();
            }
        }

        return true;
	}
}
