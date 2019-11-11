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
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
		        	
		        	if (!(sender instanceof Player))
		        	{
		        		sender.sendMessage("["+currentplayer.getName()+"] "+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET);
		        		continue;
		        	}
		        	
		        	TextComponent tc = new TextComponent(TextComponent.fromLegacyText(hidden+"["+currentplayer.getName()+"] "+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET));
					
		        	String worship = "I worship: " + godname + "\n";
		        	
					String ideal = "Ideal: I have no ideal\n";
					String trait1 = "Trait: I have no primary trait\n";
					String trait2 = "Trait: I have no secondary trait\n";
					String bond = "Bond: I have no bond\n";
					String flaw = "Flaw: I have no flaw\n";
					String oath = "Oath: I have no oath\n";
					
					if (solplayer.getGodId() > 0)
						worship = "I worship: " + solplayer.getGod().getName() + "\n";
					
					if (solplayer.getPersonality().getIdealId() > 0)
					ideal = "Ideal:" + solplayer.getPersonality().getIdeal().description + "\n";
					if (solplayer.getPersonality().getFirstTraitId() > 0)
					trait1 = "Trait:" + solplayer.getPersonality().getFirstTrait().description + "\n";
					if (solplayer.getPersonality().getSecondTraitId() > 0)
					trait2 = "Trait:" + solplayer.getPersonality().getSecondTrait().description + "\n";
					if (solplayer.getPersonality().getBondId() > 0)
					bond = "Bond:" + solplayer.getPersonality().getBond().description + "\n";
					if (solplayer.getPersonality().getFlawId() > 0)
					flaw = "Flaw:" + solplayer.getPersonality().getFlaw().description + "\n";
					String custom = "";
					for(String customTrait : solplayer.getPersonality().getCustomPersonalityTraits())
					{
						custom += "Custom:" + customTrait + "\n";
					}
					if (solplayer.getClassObj() != null && solplayer.getClassObj().getOaths().size() > 0 && solplayer.getOathId() != 0)
					{
						oath = "Oath: " + solplayer.getOath().oathname + "\n";
						for(String tenet : solplayer.getOath().tenets)
						{
							oath += " " + tenet;
						}
					}
					
					String details = ChatColor.GOLD + solplayer.getFullName().toUpperCase() + " Level " + lvl + " " + racename + " " + classname + ChatColor.RESET + "\n" + 
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
		        	
		        	if (solplayer.getRace() != null)
		        		racename = solplayer.getRace().getName();
		        	if (solplayer.getClassObj() != null)
			        	classname = solplayer.getClassObj().getName();
		        	
		        	if (!(sender instanceof Player))
		        	{
		        		sender.sendMessage("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET);
		        		continue;
		        	}
		        	
		        	TextComponent tc = new TextComponent();
					tc.setText("["+currentplayer.getName()+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ currentplayer.getWorld().getName() +"] - LVL " + ChatColor.AQUA + lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET);
					
					String ideal = "Ideal: I have no ideal\n";
					String trait1 = "Trait: I have no primary trait\n";
					String trait2 = "Trait: I have no secondary trait\n";
					String bond = "Bond: I have no bond\n";
					String flaw = "Flaw: I have no flaw\n";
					String oath = "Oath: I have no oath\n";
					
					if (solplayer.getPersonality().getIdealId() > 0)
					ideal = "Ideal:" + solplayer.getPersonality().getIdeal().description + "\n";
					if (solplayer.getPersonality().getFirstTraitId() > 0)
					trait1 = "Trait:" + solplayer.getPersonality().getFirstTrait().description + "\n";
					if (solplayer.getPersonality().getSecondTraitId() > 0)
					trait2 = "Trait:" + solplayer.getPersonality().getSecondTrait().description + "\n";
					if (solplayer.getPersonality().getBondId() > 0)
					bond = "Bond:" + solplayer.getPersonality().getBond().description + "\n";
					if (solplayer.getPersonality().getFlawId() > 0)
					flaw = "Flaw:" + solplayer.getPersonality().getFlaw().description + "\n";
					String custom = "";
					for(String customTrait : solplayer.getPersonality().getCustomPersonalityTraits())
					{
						custom += "Custom:" + customTrait + "\n";
					}
					if (solplayer.getClassObj() != null && solplayer.getClassObj().getOaths().size() > 0 && solplayer.getOathId() != 0)
					{
						oath = "Oath: " + solplayer.getOath().oathname + "\n";
						for(String tenet : solplayer.getOath().tenets)
						{
							oath += "- " + tenet + "\n";
						}
					}
					
					String details = ChatColor.GOLD + solplayer.getFullName().toUpperCase() + " Level " + lvl + " " + racename + " " + classname + ChatColor.RESET + "\n" + 
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
