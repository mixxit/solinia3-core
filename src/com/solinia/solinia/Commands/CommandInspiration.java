package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

import net.md_5.bungee.api.ChatColor;

public class CommandInspiration implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is a Player only command");
			return false;
		}
		
		try {
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
			
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Inspiration Shop");
				sender.sendMessage("-----------------");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1 AA Point " + ChatColor.RESET + " - Cost: 5 inspiration : /inspiration buy aa");
				sender.sendMessage("-----------------");
				sender.sendMessage("Sub Commands: " + ChatColor.LIGHT_PURPLE + "sites, buy, send");
				sender.sendMessage("Points Available: " + ChatColor.LIGHT_PURPLE + solPlayer.getInspiration());
				return true;
			}
			
			switch(args[0].toLowerCase())
			{
				case "buy":
					if (args.length > 1)
					{
						switch(args[1].toLowerCase())
						{
							case "aa":
								if (solPlayer.getLevel() < 20)
								{
									sender.sendMessage("Only players level 20 and above can have AA points any lower and it would be useless");
									return true;
								} else {
									if (solPlayer.getInspiration() >= 5)
									{
										solPlayer.setInspiration(solPlayer.getInspiration() - 5);
										solPlayer.setAAPoints(solPlayer.getAAPoints()+1);
										sender.sendMessage("You have purchased an AA point!");
									} else {
										sender.sendMessage("You require 5 inspiration points to purchase an AA point");
										return true;
									}
								}
								break;
							default:
								sender.sendMessage("This is not a known shop item to buy. See /inspiration for valid commands");
								return true;
						}
					} else {
						sender.sendMessage("You must specify the shop item you want to buy. Ie: /inspiration buy aa");
						return true;
					}
					break;
				case "sites":
					sender.sendMessage(ChatColor.YELLOW + "Sites you can vote on:");
					sender.sendMessage("https://www.planetminecraft.com/server/fall-of-an-empire-heavyrp/vote/");
					sender.sendMessage("http://www.minecraftforum.net/servers/6305-fall-of-an-empire-heavy-rp/vote:");
					sender.sendMessage("https://minecraftlist.org/vote/5962");
					sender.sendMessage("https://minecraft-server.net/vote/fallofanempire");
					sender.sendMessage("http://minecraft-server-list.com/server/345651/vote/");
					sender.sendMessage(ChatColor.YELLOW + "You will earn 1 inspiration point for each vote");
					break;
				case "send":
					sender.sendMessage("Forwarding inspiration is currently disabled");
					break;
				default:
					sender.sendMessage("Unknown subcommand - Valid are: sites, buy, send");
					break;
					
			}
			
			return true;
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
			return true;
		}
	}
}
