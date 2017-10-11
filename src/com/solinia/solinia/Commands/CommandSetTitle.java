package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandSetTitle implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
            Player player = (Player) sender;
            try
            {
	            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
	            
	            if (args.length == 0)
	            {
	            	player.sendMessage("Available titles:");
	            	for(String title : solplayer.getAvailableTitles())
	            	{
	                	player.sendMessage("- " + title + "");
	            	}
	            	player.sendMessage("/settitle none to remove your current title");
	            	return true;
	            }
	            
	            String targetTitle = args[0];
	            
	            if (args.length > 0) {
	            	targetTitle = "";
	    			int current = 0;
	    			for (String entry : args) {
	    				current++;
	    				targetTitle = targetTitle + entry + " ";
	    			}

	    			targetTitle = targetTitle.trim();
	    		}
	            
	            if (targetTitle.equals("none"))
	            {
		            boolean found = false;
		            for(String title : solplayer.getAvailableTitles())
	            	{
		            	if (targetTitle.toUpperCase().equals(title.toUpperCase()))
		            	{
		            		targetTitle = title;
		            		found = true;
		            		break;
		            	}
	            	}
		            
		            if (found == false)
		            {
		            	player.sendMessage("You do not have this title");
		            	return true;
		            }

		            solplayer.setTitle(targetTitle);
	            } else {
		            solplayer.setTitle("");
	            }
	            
	            player.sendMessage("* Title set to: " + targetTitle);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				player.sendMessage(e.getMessage());
			}
        	
        }

        return true;
	}
}
