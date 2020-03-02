package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaPlayerSkill;
import com.solinia.solinia.Utils.PlayerUtils;

public class CommandForceSkill implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.forceskill"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 3)
        {
        	sender.sendMessage("Insufficient arguments (<mcname> <skill> <level>)");
        	return false;
        }
    	
        String name = args[0];
        String skill = args[1];
        int level = Integer.parseInt(args[2]);
        
        if (level < 0)
        {
        	sender.sendMessage("Level must be greater than -1");
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
	    			boolean foundSkill = false;
	    			for(SoliniaPlayerSkill skillEntry : solplayer.getSkills())
	    			{
	    				if (skillEntry.getSkillName().toUpperCase().equals(skill.toUpperCase()))
	    					foundSkill = true;
	    			}
	    			
	    			if (foundSkill == false)
	    			{
		            	sender.sendMessage("Force skill command failed. The player does not have that skill");
	    				return true;
	    			}
	    			
	    			solplayer.setSkill(skill.toUpperCase(), level);
	    			sender.sendMessage("* Player "+name+" set skill "+skill.toUpperCase()+" to " + level);
	            	return true;
	            } else {
	            	sender.sendMessage("Force skill command failed. Is that a valid level and a valid online mcaccount?");
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