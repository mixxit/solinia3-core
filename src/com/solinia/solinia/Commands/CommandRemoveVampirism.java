package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class CommandRemoveVampirism implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.removevampirism"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		if (args.length < 1)
        {
        	sender.sendMessage("Insufficient arguments (<mcname>)");
        	return false;
        }
    	
        String name = args[0];
        
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
	    			solplayer.cureVampirism();
	            	return true;
	            } else {
	            	sender.sendMessage("Removevampirism failed. Is that a valid online mcaccount?");
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
