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
import com.solinia.solinia.Utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.Tuple;

public class CommandWho implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String filter = "";
		if (args.length > 0)
			filter = args[0];
		
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
        		if (!currentplayer.getDisplayName().contains(filter) && !currentplayer.getName().contains(filter))
        			continue;
        	
        	try
        	{
	        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(currentplayer);
	        	if (solPlayer == null)
	        		continue;
	        	
	        	String zone = "";
	        	SoliniaZone solZone = solPlayer.getZone();
				if (solZone != null)
					zone = solZone.getName();
	        	
	        	Tuple<String,TextComponent> characterText = PlayerUtils.GetCharacterText(solPlayer, hidden, currentplayer.getName(), player.getWorld().getName(), zone);
	        	if (sender instanceof Player)
	        	{
	        		sender.spigot().sendMessage(characterText.b());
	        	} else {
	        		sender.sendMessage(characterText.a());
	        	}
        	} catch (CoreStateInitException e)
        	{
        		
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
			
			if (solPlayer.getBackStory() == null && solPlayer.getBackStory().equals(""))
			{
				sender.sendMessage("- You have no backstory set - set with /personality backstory <backstory>");
			}
        } catch (CoreStateInitException e)
        {
        	
        }
		
	    return true;
	}
}
