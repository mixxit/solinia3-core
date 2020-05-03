package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandInspiration implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Inspiration Shop");
				sender.sendMessage("-----------------");
				if (sender instanceof Player)
				{
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
					if (solPlayer.getActualLevel() >= 50)
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1 AA Point " + ChatColor.RESET + " - Cost: 5 inspiration : /inspiration buy aa");
				}
				
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Head) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manahead");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Chest) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manachest");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Legs) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manalegs");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Feet) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manafeet");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1x 100% Experience Potion " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy xpbottle");
				sender.sendMessage("-----------------");
				sender.sendMessage("Sub Commands: " + ChatColor.LIGHT_PURPLE + "sites, buy, send");
				
				if (sender instanceof Player)
				{
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
					sender.sendMessage("Points Available: " + ChatColor.LIGHT_PURPLE + solPlayer.getInspiration());
				}
				return true;
			}
			
			switch(args[0].toLowerCase())
			{
				case "buy":
					if (args.length > 1)
					{
						int cost = 0;
						
						switch(args[1].toLowerCase())
						{
							case "aa":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
									if (solPlayer.getActualLevel() < 50)
									{
										sender.sendMessage("Only players level 50 and above can have AA points any lower and it would be useless");
										return true;
									} else {
										cost = 5;
										if (solPlayer.getInspiration() >= cost)
										{
											solPlayer.setInspiration(solPlayer.getInspiration() - cost);
											solPlayer.setAAPoints(solPlayer.getAAPoints()+1);
											sender.sendMessage("You have purchased an AA point!");
											return true;
										} else {
											sender.sendMessage("You require more inspiration points to purchase this");
											return true;
										}
									}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
								
							case "manahead":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
									cost = 2;
									if (solPlayer.getInspiration() >= cost)
									{
										solPlayer.setInspiration(solPlayer.getInspiration() - cost);
										ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(18019);
										PlayerUtils.addToPlayersInventory((Player)sender, item.asItemStack());										sender.sendMessage("You have purchased an item!");
										return true;
									} else {
										sender.sendMessage("You require more inspiration points to purchase this");
										return true;
									}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
							case "manachest":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
								cost = 2;
								if (solPlayer.getInspiration() >= cost)
								{
									solPlayer.setInspiration(solPlayer.getInspiration() - cost);
									ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(18020);
									PlayerUtils.addToPlayersInventory((Player)sender, item.asItemStack());									sender.sendMessage("You have purchased an item!");
									return true;
								} else {
									sender.sendMessage("You require more inspiration points to purchase this");
									return true;
								}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
							case "manalegs":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
								cost = 2;
								if (solPlayer.getInspiration() >= cost)
								{
									solPlayer.setInspiration(solPlayer.getInspiration() - cost);
									ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(18021);
									PlayerUtils.addToPlayersInventory((Player)sender, item.asItemStack());
									sender.sendMessage("You have purchased an item!");
									return true;
								} else {
									sender.sendMessage("You require more inspiration points to purchase this");
									return true;
								}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
							case "manafeet":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
								cost = 2;
								if (solPlayer.getInspiration() >= cost)
								{
									solPlayer.setInspiration(solPlayer.getInspiration() - cost);
									ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(18022);
									PlayerUtils.addToPlayersInventory((Player)sender, item.asItemStack());
									sender.sendMessage("You have purchased an item!");
									return true;
								} else {
									sender.sendMessage("You require more inspiration points to purchase this");
									return true;
								}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
							case "xpbottle":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
									cost = 2;
									if (solPlayer.getInspiration() >= cost)
									{
										solPlayer.setInspiration(solPlayer.getInspiration() - cost);
										ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(18024);
										PlayerUtils.addToPlayersInventory((Player)sender, item.asItemStack());
										sender.sendMessage("You have purchased an item!");
										return true;
									} else {
										sender.sendMessage("You require more inspiration points to purchase this");
										return true;
									}
								} else {
									sender.sendMessage("This is a player only subcommand");
									return true;

								}
								
								
							default:
								sender.sendMessage("This is not a known shop item to buy. See /inspiration for valid commands");
								return true;
						}
					} else {
						sender.sendMessage("You must specify the shop item you want to buy. Ie: /inspiration buy aa");
						return true;
					}
				case "sites":
					sender.sendMessage(ChatColor.YELLOW + "Sites you can vote on:");
					String urls = "https://www.fallofanempire.com/docs/guides/voting/";
					
					TextComponent tc = new TextComponent(TextComponent.fromLegacyText("[ Click here to visit our voting site ]"));
					tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,urls));
					
					sender.spigot().sendMessage(tc);
					
					sender.sendMessage(ChatColor.YELLOW + "You will earn 1 inspiration point for each vote");
					break;
				case "send":
					if (args.length > 2)
					{
						if (sender instanceof Player)
						{
							ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
							String mcAccount = args[1];
							int amount = Integer.parseInt(args[2]);
							
							Player player = Bukkit.getPlayer(mcAccount);
							if (player == null)
							{
								sender.sendMessage("Cannot find that player to forward to. It must be their minecraft account name and they must be online (ie /inspiration send mixxit 1)");
								return true;
							}
							
							if (solPlayer.getInspiration() >= amount)
							{
								ISoliniaPlayer targetPlayer = SoliniaPlayerAdapter.Adapt(player);
								solPlayer.setInspiration(solPlayer.getInspiration() - amount);
								targetPlayer.setInspiration(targetPlayer.getInspiration() + amount);
								sender.sendMessage("You have sent " + amount + " inspiration to " + player.getName());
								player.sendMessage("You have received " + amount + " inspiration from " + sender.getName());
								return true;
							} else {
								sender.sendMessage("You do not have that amount of inspiration to send");
								return true;
							}
						} else {
							sender.sendMessage("This is a player only subcommand");
						}
					} else {
						sender.sendMessage("You must specify how many you wish to transfer and the mc account name of the person you want to send it to (ie /inspiration send mixxit 1)");
						return true;
					}
				case "grant":
					if (args.length > 2)
					{
						String mcAccount = args[1];
						int amount = Integer.parseInt(args[2]);
						
						Player player = Bukkit.getPlayer(mcAccount);
						if (player == null)
						{
							sender.sendMessage("Cannot find that player to grant to. It must be their minecraft account name and they must be online (ie /inspiration grant mixxit 1)");
							return true;
						}
						
						if (sender.isOp() || sender.hasPermission("solinia.inspirationgrant"))
						{
							ISoliniaPlayer targetPlayer = SoliniaPlayerAdapter.Adapt(player);
							targetPlayer.setInspiration(targetPlayer.getInspiration() + amount);
							sender.sendMessage("You have granted " + amount + " inspiration to " + player.getName());
							player.sendMessage("You have received " + amount + " inspiration from " + sender.getName());
							return true;
						} else {
							sender.sendMessage("You do not have permission to perform this command");
							return true;
						}
					} else {
						sender.sendMessage("You must specify how many you wish to grant and the mc account name of the person you want to send it to (ie /inspiration grant mixxit 1)");
						return true;
					}
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
