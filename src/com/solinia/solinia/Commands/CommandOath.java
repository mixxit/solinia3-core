package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaRaceCreationException;
import com.solinia.solinia.Factories.SoliniaRaceFactory;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Oath;

import net.md_5.bungee.api.ChatColor;

public class CommandOath implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof ConsoleCommandSender)
		{
			try
			{
				List<Oath> oaths = StateManager.getInstance().getConfigurationManager().getOaths();
				for(Oath oath : oaths)
				{
					sender.sendMessage(oath.id + ": " + oath.oathname.toUpperCase());
				}
			} catch (CoreStateInitException e)
			{
				
			}
			return true;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			if (solPlayer.getClassObj() == null)
			{
				sender.sendMessage("* This command require a class to be set");
				return true;
			}
			
			ISoliniaClass solclass = solPlayer.getClassObj();
			List<Integer> oathIds = solclass.getOaths();
			if (oathIds.size() < 1)
			{
				sender.sendMessage("* Your class is not required to make an oath");
				return true;
			}
			
			List<Oath> oaths = StateManager.getInstance().getConfigurationManager().getOaths();
			
			if (args.length == 0)
			{
				if (solPlayer.getOathId() != 0)
				{
					for(Oath oath : oaths)
					{
						if (solPlayer.getOathId() != oath.id)
							continue;
						
						sender.sendMessage("Oath Name: " + ChatColor.GOLD + oath.oathname.toUpperCase() + ChatColor.RESET);
						for(String tenet : oath.tenets)
						{
							sender.sendMessage(" - " + tenet);
						}
					}
					return true;
				} else {
					for(Oath oath : oaths)
					{
						if (!oathIds.contains(oath.id))
							continue;
						
						sender.sendMessage("Oath Name: " + ChatColor.GOLD + oath.oathname.toUpperCase() + ChatColor.RESET);
						for(String tenet : oath.tenets)
						{
							sender.sendMessage(" - " + tenet);
						}
					}
					
					sender.sendMessage("Syntax: /oath oathname");
					return true;
				}
			}
			
			if (args.length > 0)
			{
				if (solPlayer.getOathId() != 0)
				{
					sender.sendMessage("* You have already made your oath");
					return true;
				}
				
				for(Oath oath : oaths)
				{
					if (!args[0].toUpperCase().equals(oath.oathname.toUpperCase()))
						continue;
					
					if (!oathIds.contains(oath.id))
						continue;
					
					sender.sendMessage("Your oath has been set to : " + args[0] + " (" + oath.id + ")");
					solPlayer.setOathId(oath.id);
					return true;
				}
				
				sender.sendMessage("Could not set the oath: " + args[0]);
				return false;
			}

			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return true;
	}
}
