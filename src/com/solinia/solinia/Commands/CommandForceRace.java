package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Utils.Utils;

public class CommandForceRace implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.forcerace"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 2)
        {
        	sender.sendMessage("Insufficient arguments (<mcname> <race>)");
        	return false;
        }
    	
        String name = args[0];
        int raceid = Integer.parseInt(args[1]);
        
        if (raceid < 1)
        {
        	sender.sendMessage("Raceid must be greater than 0");
        }
        
        boolean found = false;
        for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
        {
        	if (!currentplayer.getName().toUpperCase().equals(name.toUpperCase()))
        		continue;
        	
        	try
        	{
	        	ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
	    		found = true;
	    		if (found)
	            {
	    			ISoliniaRace race = StateManager.getInstance().getConfigurationManager().getRace(raceid);
	    			if (race == null)
	    			{
	    				sender.sendMessage("race does not exist");
	    				return true;
	    			}
	    			
	    			solplayer.setRaceId(raceid);
	    			sender.sendMessage("* Player "+name+" set to race "+race.getName());
	            	return true;
	            } else {
	            	sender.sendMessage("Force race command failed. Is that a valid race and a valid online mcaccount?");
	            	return false;
	            }
        	} catch (CoreStateInitException e)
        	{
        		e.printStackTrace();
        		sender.sendMessage(e.getMessage());
        	}
        }
        
        if (found == false)
        {
        	sender.sendMessage("Failed to find player by that minecraft username");
        	return false;
        }

        return true;
	}
}
