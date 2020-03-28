package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
	            		TextComponent tc = new TextComponent();
						tc.setText("- " + title);
						TextComponent tc2 = new TextComponent();
						tc2.setText(ChatColor.GRAY + " - Click here to set this title" + ChatColor.RESET);
						tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/settitle " + title));
						tc.addExtra(tc2);
						sender.spigot().sendMessage(tc);
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
	            
	            if (!targetTitle.equals("none"))
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
	            
	            System.out.println("* [" + player.getName() + "] Title set to: " + "'" + solplayer.getTitle() + "'");
	            player.sendMessage("* Title set to: '" + solplayer.getTitle() + "'");
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				player.sendMessage(e.getMessage());
			}
        	
        }

        return true;
	}
}
