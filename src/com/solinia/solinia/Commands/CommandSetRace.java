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
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class CommandSetRace implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		sender.sendMessage("See /raceinfo for more information");
		
		String racelist = "";
        List<ISoliniaRace> races = new ArrayList<ISoliniaRace>();
    	try {
			for(ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces())
			{
				if (!race.isAdmin())
				{
					racelist = racelist + " " + ChatColor.LIGHT_PURPLE + race.getName().toUpperCase() + ChatColor.RESET;
					races.add(race);
				}
			}
		} catch (CoreStateInitException e1) 
    	{
			sender.sendMessage("Race command failed. " + e1.getMessage());
			return false;
		}
		
		if ((sender instanceof ConsoleCommandSender || sender instanceof DiscordDefaultChannelCommandSender || sender instanceof DiscordAdminChannelCommandSender))
		{
			try {
				Utils.sendRaceInfo(sender);
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
			player.sendMessage("Race command failed. " + e2.getMessage());
			return false;
		}
    	
    	if (soliniaplayer.hasChosenRace() == true)
        {
        	player.sendMessage("You cannot choose a race as you have already selected one");
        	return true;            	
        }
        
        if (args.length == 0)
        {
        	try {
				Utils.sendRaceInfo(sender);
			} catch (CoreStateInitException e1) {
				
			}
        	player.sendMessage("Insufficient arguments. Please provide correct race name");
        	if (soliniaplayer != null)
        	{
        		ISoliniaRace solrace;
				try {
					solrace = StateManager.getInstance().getConfigurationManager().getRace(soliniaplayer.getRaceId());
				} catch (CoreStateInitException e) {
					player.sendMessage("Race command failed. " + e.getMessage());
					return false;
				}
        		
				if (solrace == null)
					player.sendMessage("Your current race is: UNKNOWN");
				else
					player.sendMessage("Your current race is: " + solrace.getName());
        	}
        	return false;
        }
        
        String race = args[0].toUpperCase();
        
        boolean found = false;
        for(ISoliniaRace allowedrace : races)
    	{
    		if (allowedrace.getName().equals(race))
    		{
    			found = true;
    		}
    	}
        
        if (found == false)
        {
        	try {
				Utils.sendRaceInfo(sender);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	player.sendMessage("Insufficient arguments. Please provide correct race name");
        	return false;
        }
        
        try {
			if (StateManager.getInstance().getConfigurationManager().getRace(race) != null)
			{
				soliniaplayer.setRaceId(StateManager.getInstance().getConfigurationManager().getRace(race).getId());
				soliniaplayer.setChosenRace(true);
				player.sendMessage("* Race set to " + race);
				return true;
			} else {
				Utils.sendRaceInfo(sender);
				player.sendMessage("Insufficient arguments. Please provide correct race name");
				return false;
			}
		} catch (CoreStateInitException e) {
			player.sendMessage("Race command failed. " + e.getMessage());
			return false;
		}
	}
}
