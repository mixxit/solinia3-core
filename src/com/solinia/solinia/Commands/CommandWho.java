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
import com.solinia.solinia.Models.SoliniaZone;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandWho implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String filter = "";
		if (args.length > 0)
			filter = args[0];
		
		if (sender instanceof Player) {
	        Player player = (Player) sender;
	        
	        sender.sendMessage("Current Online Players:");
	        sender.sendMessage("---------------------------------");
	        
	        for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
		    {
	        	String hidden = "";
	        	
	        	if (currentplayer.hasPermission("essentials.silentjoin"))
	        		hidden = "[HIDDEN]";
	        	
	        	if (currentplayer.hasPermission("essentials.silentjoin") && !sender.isOp())
	        	{
	        		continue;
	        	}
	        	
	        	if (!player.canSee(currentplayer))
	        		hidden = "[HIDDEN]";
	        	
	        	if (!player.canSee(currentplayer) && !sender.isOp())
	        	{
	        		continue;
	        	}
	        	
	        	if (!filter.equals(""))
	        		if (!currentplayer.getDisplayName().contains(filter))
	        			continue;
	        	
	        	try {
		            ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
		        	int lvl = (int) Math.floor(solplayer.getLevel());
		        	
		        	String racename = "UNKNOWN";
		        	String classname = "UNKNOWN";
		        	String godname = "UNKNOWN";
		        	String zone = "UNKNOWN";
		        	
		        	SoliniaZone solZone = solplayer.getZone();
					if (solZone != null)
						zone = solZone.getName();
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getShortName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getShortName();
		        	
		        	if (!(sender instanceof Player))
		        	{
		        		sender.sendMessage("["+currentplayer.getName()+"] "+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET + " Zone: " + ChatColor.AQUA + zone + ChatColor.RESET);
		        		
		        		continue;
		        	}
		        	
		        	TextComponent tc = new TextComponent(TextComponent.fromLegacyText(hidden+"["+currentplayer.getName()+"] "+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET + " Zone: " + ChatColor.AQUA + zone + ChatColor.RESET));
					
		        	String worship = "I worship: " + godname + System.lineSeparator();
		        	
					String ideal = "Ideal: I have no ideal" + System.lineSeparator();
					String trait1 = "Trait: I have no primary trait" + System.lineSeparator();
					String trait2 = "Trait: I have no secondary trait" + System.lineSeparator();
					String bond = "Bond: I have no bond" + System.lineSeparator();
					String flaw = "Flaw: I have no flaw" + System.lineSeparator();
					String oath = "Oath: I have no oath" + System.lineSeparator();
					
					if (solplayer.getGodId() > 0)
						worship = "I worship: " + solplayer.getGod().getName() + System.lineSeparator();
					
					if (solplayer.getPersonality().getIdealId() > 0)
					ideal = "Ideal:" + solplayer.getPersonality().getIdeal().description + System.lineSeparator();
					if (solplayer.getPersonality().getFirstTraitId() > 0)
					trait1 = "Trait:" + solplayer.getPersonality().getFirstTrait().description + System.lineSeparator();
					if (solplayer.getPersonality().getSecondTraitId() > 0)
					trait2 = "Trait:" + solplayer.getPersonality().getSecondTrait().description + System.lineSeparator();
					if (solplayer.getPersonality().getBondId() > 0)
					bond = "Bond:" + solplayer.getPersonality().getBond().description + System.lineSeparator();
					if (solplayer.getPersonality().getFlawId() > 0)
					flaw = "Flaw:" + solplayer.getPersonality().getFlaw().description + System.lineSeparator();
					String custom = "";
					for(String customTrait : solplayer.getPersonality().getCustomPersonalityTraits())
					{
						custom += "Custom:" + customTrait + System.lineSeparator();
					}
					if (solplayer.getClassObj() != null && solplayer.getClassObj().getOaths().size() > 0 && solplayer.getOathId() != 0)
					{
						oath = "Oath: " + solplayer.getOath().oathname + System.lineSeparator();
						for(String tenet : solplayer.getOath().tenets)
						{
							oath += " " + tenet;
						}
					}
					
					String details = ChatColor.GOLD + solplayer.getFullName().toUpperCase() + " Level " + lvl + " " + racename + " " + classname + ChatColor.RESET + System.lineSeparator() + 
					worship + 
					ideal +
					trait1 +
					trait2 + 
					bond +
					flaw + 
					custom +
					oath;
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
					player.spigot().sendMessage(tc);
			    } catch (CoreStateInitException e) {
					
				}
		    }
	        
	        try
	        {
		        ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer.getPersonality().getBondId() == 0 || 
						solPlayer.getPersonality().getFirstTraitId() == 0 ||
						solPlayer.getPersonality().getSecondTraitId() == 0 ||
						solPlayer.getPersonality().getFlawId() == 0 ||
						solPlayer.getPersonality().getIdealId() == 0
						)
				{
					player.sendMessage(ChatColor.GRAY + "* You have not set your personality. Please see /personality" + ChatColor.RESET);
				}
				
				if (solPlayer.getClassObj() != null && solPlayer.getClassObj().getOaths().size() > 0 && solPlayer.getOathId() == 0)
				{
					player.sendMessage(ChatColor.GRAY + "* You have not set your Oath. Please see /oath" + ChatColor.RESET);
				}
	        } catch (CoreStateInitException e)
	        {
	        	
	        }
	    }
		
		if ((
				sender instanceof ConsoleCommandSender
				))
		{
			CommandSender player = (CommandSender) sender;
        	
        	for(Player currentplayer : Bukkit.getServer().getOnlinePlayers())
		    {
	        	if (!filter.equals(""))
	        		if (!currentplayer.getDisplayName().contains(filter))
	        			continue;

        		
        		ISoliniaPlayer solplayer;
				try {
					solplayer = SoliniaPlayerAdapter.Adapt(currentplayer);
				
		        	int lvl = (int) Math.floor(solplayer.getLevel());
		        	
		        	String racename = "UNKNOWN";
		        	String classname = "UNKNOWN";
		        	String zone = "UNKNOWN";
		        	
		        	SoliniaZone solZone = solplayer.getZone();
					if (solZone != null)
						zone = solZone.getName();
					
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
		        	
		        	if (!(sender instanceof Player))
		        	{
		        		sender.sendMessage("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET + " Zone: " + ChatColor.AQUA + zone + ChatColor.RESET);
		        		continue;
		        	}
		        	
		        	TextComponent tc = new TextComponent();
					tc.setText("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET + " Zone: " + ChatColor.AQUA + zone + ChatColor.RESET);
					
					String ideal = "Ideal: I have no ideal" + System.lineSeparator();
					String trait1 = "Trait: I have no primary trait" + System.lineSeparator();
					String trait2 = "Trait: I have no secondary trait" + System.lineSeparator();
					String bond = "Bond: I have no bond" + System.lineSeparator();
					String flaw = "Flaw: I have no flaw" + System.lineSeparator();
					String oath = "Oath: I have no oath" + System.lineSeparator();
					
					if (solplayer.getPersonality().getIdealId() > 0)
					ideal = "Ideal:" + solplayer.getPersonality().getIdeal().description + System.lineSeparator();
					if (solplayer.getPersonality().getFirstTraitId() > 0)
					trait1 = "Trait:" + solplayer.getPersonality().getFirstTrait().description + System.lineSeparator();
					if (solplayer.getPersonality().getSecondTraitId() > 0)
					trait2 = "Trait:" + solplayer.getPersonality().getSecondTrait().description + System.lineSeparator();
					if (solplayer.getPersonality().getBondId() > 0)
					bond = "Bond:" + solplayer.getPersonality().getBond().description + System.lineSeparator();
					if (solplayer.getPersonality().getFlawId() > 0)
					flaw = "Flaw:" + solplayer.getPersonality().getFlaw().description + System.lineSeparator();
					String custom = "";
					for(String customTrait : solplayer.getPersonality().getCustomPersonalityTraits())
					{
						custom += "Custom:" + customTrait + System.lineSeparator();
					}
					if (solplayer.getClassObj() != null && solplayer.getClassObj().getOaths().size() > 0 && solplayer.getOathId() != 0)
					{
						oath = "Oath: " + solplayer.getOath().oathname + System.lineSeparator();
						for(String tenet : solplayer.getOath().tenets)
						{
							oath += "- " + tenet + System.lineSeparator();
						}
					}
					
					String details = ChatColor.GOLD + solplayer.getFullName().toUpperCase() + " Level " + lvl + " " + racename + " " + classname + ChatColor.RESET + System.lineSeparator() + 
					ideal +
					trait1 +
					trait2 + 
					bond +
					flaw +
					custom +
					oath;
					
					tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
					player.spigot().sendMessage(tc);
				} catch (CoreStateInitException e) {
					
				}
		    }
        }
	
	    return true;
	}
}
