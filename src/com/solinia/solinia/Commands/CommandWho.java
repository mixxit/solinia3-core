package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Providers.DiscordAdminChannelCommandSender;
import com.solinia.solinia.Providers.DiscordDefaultChannelCommandSender;

import net.md_5.bungee.api.ChatColor;

public class CommandWho implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
	        Player player = (Player) sender;
	        
	        for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
		    {
	        	try {
		            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
		        	int lvl = (int) Math.floor(solplayer.getLevel());
		        	
		        	String racename = "UNKNOWN";
		        	String classname = "UNKNOWN";
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
	
		        	player.sendMessage("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET);
			    } catch (CoreStateInitException e) {
					
				}
		    }
	    }
		
		if ((sender instanceof ConsoleCommandSender || sender instanceof DiscordDefaultChannelCommandSender || sender instanceof DiscordAdminChannelCommandSender)) {
			ConsoleCommandSender player = (ConsoleCommandSender) sender;
        	
        	for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
		    {
        		ISoliniaPlayer solplayer;
				try {
					solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
				
		        	int lvl = (int) Math.floor(solplayer.getLevel());
		        	
		        	String racename = "UNKNOWN";
		        	String classname = "UNKNOWN";
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
		        	
		        	player.sendMessage("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET);
				} catch (CoreStateInitException e) {
					
				}
		    }
        }
	
	    return true;
	}
}
