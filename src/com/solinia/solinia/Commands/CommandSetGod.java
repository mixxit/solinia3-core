package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandSetGod implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		sender.sendMessage("See /godinfo for more information");
		
		String godlist = "";
        List<ISoliniaGod> gods = new ArrayList<ISoliniaGod>();
    	try {
			for(ISoliniaGod god : StateManager.getInstance().getConfigurationManager().getGods())
			{
				godlist = godlist + " " + ChatColor.LIGHT_PURPLE + god.getName().toUpperCase() + ChatColor.RESET;
				gods.add(god);
			}
		} catch (CoreStateInitException e1) 
    	{
			sender.sendMessage("God command failed. " + e1.getMessage());
			return false;
		}
		
		if ((
				sender instanceof ConsoleCommandSender
				))

		{
			try {
				Utils.sendGodInfo(sender);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		Player player = (Player) sender;
        ISoliniaPlayer soliniaplayer;
		try {
			soliniaplayer = SoliniaPlayerAdapter.Adapt(player);
		} catch (CoreStateInitException e2) {
			player.sendMessage("God command failed. " + e2.getMessage());
			return false;
		}
    	
    	if (soliniaplayer.hasChosenGod() == true)
        {
        	player.sendMessage("You cannot choose a god as you have already selected one");
        	return true;            	
        }
        
        if (args.length == 0)
        {
        	try {
				Utils.sendGodInfo(sender);
			} catch (CoreStateInitException e1) {
				
			}
        	player.sendMessage("Insufficient arguments. Please provide correct god name");
        	if (soliniaplayer != null)
        	{
        		ISoliniaGod solgod;
				try {
					solgod = StateManager.getInstance().getConfigurationManager().getGod(soliniaplayer.getGodId());
				} catch (CoreStateInitException e) {
					player.sendMessage("God command failed. " + e.getMessage());
					return false;
				}
        		
				if (solgod == null)
					player.sendMessage("Your current god is: UNKNOWN");
				else
					player.sendMessage("Your current god is: " + solgod.getName());
        	}
        	return false;
        }
        
        String god = args[0].toUpperCase();
        
        boolean found = false;
        for(ISoliniaGod allowedgod : gods)
    	{
    		if (allowedgod.getName().equals(god))
    		{
    			found = true;
    		}
    	}
        
        if (found == false)
        {
        	try {
				Utils.sendGodInfo(sender);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	player.sendMessage("Insufficient arguments. Please provide correct god name");
        	return false;
        }
        
        try {
			if (StateManager.getInstance().getConfigurationManager().getGod(god) != null)
			{
				ISoliniaGod solGod = StateManager.getInstance().getConfigurationManager().getGod(god);
				
				soliniaplayer.setGodId(solGod.getId());
				soliniaplayer.setHasChosenGod(true);
				player.sendMessage("* God set to " + god);
				
				return true;
			} else {
				Utils.sendGodInfo(sender);
				player.sendMessage("Insufficient arguments. Please provide correct god name");
				return false;
			}
		} catch (CoreStateInitException e) {
			player.sendMessage("God command failed. " + e.getMessage());
			return false;
		}
	}
}
