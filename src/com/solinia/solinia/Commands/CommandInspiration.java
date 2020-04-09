package com.solinia.solinia.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;

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
					if (solPlayer.getLevel() >= 50)
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1 AA Point " + ChatColor.RESET + " - Cost: 5 inspiration : /inspiration buy aa");
				}
				
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Head) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manahead");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Chest) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manachest");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Legs) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manalegs");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy Mana Regen Aug (Feet) " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy manafeet");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1x 100% Experience Potion " + ChatColor.RESET + " - Cost: 2 inspiration : /inspiration buy xpbottle");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1x pre-tier gear set" + ChatColor.RESET + " - Cost: 6 inspiration : /inspiration buy pregear");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Buy 1x next tier gear set" + ChatColor.RESET + " - Cost: 35 inspiration : /inspiration buy gear");
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
							case "gear":
								sendGear(sender, cost);
								return true;
							case "pregear":
								sendPreGear(sender, cost);
								return true;
							case "aa":
								if (sender instanceof Player)
								{
									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
									if (solPlayer.getLevel() < 50)
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
					sender.sendMessage("https://www.planetminecraft.com/server/fall-of-an-empire-heavyrp/vote/");
					sender.sendMessage("https://minecraft-mp.com/server/118059/vote/");
					sender.sendMessage("https://minecraftlist.org/vote/5962");
					sender.sendMessage("http://minecraftservers.org/vote/464284");
					sender.sendMessage("https://minecraft-server.net/vote/fallofanempire");
					sender.sendMessage("http://minecraft-server-list.com/server/345651/vote/");
					sender.sendMessage("http://www.minecraft-servers-list.org/index.php?a=in&u=mixxit");
					sender.sendMessage("https://minebrowse.com/server/364");
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

	private boolean sendGear(CommandSender sender, int cost) {
		try
		{
		if (sender instanceof Player)
		{
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)sender);
			cost = 35;
			if (player.getInspiration() >= cost)
			{
				
				String suffix = "of Inspiration";
				
				int playertier = 1;
				if (player.getLevel() >= 1 && player.getLevel() < 11)
					playertier = 1;
				if (player.getLevel() >= 11 && player.getLevel() < 21)
					playertier = 2;
				if (player.getLevel() >= 21 && player.getLevel() < 31)
					playertier = 3;
				if (player.getLevel() >= 31 && player.getLevel() < 41)
					playertier = 4;
				if (player.getLevel() >= 41 && player.getLevel() < 51)
					playertier = 5;
				if (player.getLevel() >= 51 && player.getLevel() < 61)
					playertier = 6;
				if (player.getLevel() >= 61 && player.getLevel() < 71)
					playertier = 7;
				if (player.getLevel() >= 71 && player.getLevel() < 81)
					playertier = 8;
				if (player.getLevel() >= 81 && player.getLevel() < 91)
					playertier = 9;
				if (player.getLevel() >= 91 && player.getLevel() < 101)
					playertier = 10;
				if (player.getLevel() >= 101 && player.getLevel() < 111)
					playertier = 11;

				
				try {
					
					// always give the next tier up
					// this ability is for special seasonal rewards only
					playertier += 1;
					List<Integer> items = SoliniaItemFactory.CreateClassItemSet(player.getClassObj(), playertier, suffix, false, player.getBukkitPlayer().getName(), false);
					
					for(int itemid : items)
					{
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
						final String playerName = player.getBukkitPlayer().getName();
						final int minLevel = player.getLevel();
						final int finalitemid = itemid;
						if (item != null)
						{
							
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											try
											{
												ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(finalitemid);
												item.setMinLevel(minLevel);
												item.setLastUpdatedTimeNow();
												SoliniaAccountClaim claim = new SoliniaAccountClaim();
												claim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
												claim.setMcname(playerName);
												claim.setItemid(finalitemid);
												claim.setClaimed(false);
												Player claimPlayer = Bukkit.getPlayer(playerName);
												if (claimPlayer != null)
												{
													claimPlayer.sendMessage(ChatColor.GOLD + "You have been awarded with a claim item! See /claim");
												}
												StateManager.getInstance().getConfigurationManager().addAccountClaim(claim);
											} catch (CoreStateInitException e)
											{
												// skip
											}
										}
									});
						}
					}
					
					player.setInspiration(player.getInspiration() - cost);
					sender.sendMessage("You have purchased a full set of gear!");

				} catch (CoreStateInitException e)
				{
					sender.sendMessage("There was a problem generating your gear (Core), please report this to a developer");
				} catch (SoliniaItemException e)
				{
					sender.sendMessage("There was a problem generating your gear (Item), please report this to a developer");
				}
				
				return true;
			} else {
				sender.sendMessage("You require more inspiration points to purchase this");
				return true;
			}
		} else {
			sender.sendMessage("This is a player only subcommand");
			return true;

		}
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}

	private boolean sendPreGear(CommandSender sender, int cost) {
		try
		{
		if (sender instanceof Player)
		{
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player)sender);
			cost = 6;
			if (player.getInspiration() >= cost)
			{
				
				String suffix = "of Inspiration";
				
				int playertier = 1;
				if (player.getLevel() >= 1 && player.getLevel() < 11)
					playertier = 1;
				if (player.getLevel() >= 11 && player.getLevel() < 21)
					playertier = 2;
				if (player.getLevel() >= 21 && player.getLevel() < 31)
					playertier = 3;
				if (player.getLevel() >= 31 && player.getLevel() < 41)
					playertier = 4;
				if (player.getLevel() >= 41 && player.getLevel() < 51)
					playertier = 5;
				if (player.getLevel() >= 51 && player.getLevel() < 61)
					playertier = 6;
				if (player.getLevel() >= 61 && player.getLevel() < 71)
					playertier = 7;
				if (player.getLevel() >= 71 && player.getLevel() < 81)
					playertier = 8;
				if (player.getLevel() >= 81 && player.getLevel() < 91)
					playertier = 9;
				if (player.getLevel() >= 91 && player.getLevel() < 101)
					playertier = 10;
				if (player.getLevel() >= 101 && player.getLevel() < 111)
					playertier = 11;

				
				try {
					
					// always give the next tier up
					// this ability is for special seasonal rewards only
					playertier -= 1;
					if (playertier < 1)
						playertier = 1;
					
					List<Integer> items = SoliniaItemFactory.CreateClassItemSet(player.getClassObj(), playertier, suffix, false, player.getBukkitPlayer().getName(), false);
					
					for(int itemid : items)
					{
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemid);
						final String playerName = player.getBukkitPlayer().getName();
						final int minLevel = player.getLevel();
						final int finalitemid = itemid;
						if (item != null)
						{
							
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
									Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
										public void run() {
											try
											{
												ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(finalitemid);
												item.setMinLevel(minLevel);
												item.setLastUpdatedTimeNow();
												SoliniaAccountClaim claim = new SoliniaAccountClaim();
												claim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
												claim.setMcname(playerName);
												claim.setItemid(finalitemid);
												claim.setClaimed(false);
												Player claimPlayer = Bukkit.getPlayer(playerName);
												if (claimPlayer != null)
												{
													claimPlayer.sendMessage(ChatColor.GOLD + "You have been awarded with a claim item! See /claim");
												}
												StateManager.getInstance().getConfigurationManager().addAccountClaim(claim);
											} catch (CoreStateInitException e)
											{
												// skip
											}
										}
									});
						}
					}
					
					player.setInspiration(player.getInspiration() - cost);
					sender.sendMessage("You have purchased a full set of gear!");

				} catch (CoreStateInitException e)
				{
					sender.sendMessage("There was a problem generating your gear (Core), please report this to a developer");
				} catch (SoliniaItemException e)
				{
					sender.sendMessage("There was a problem generating your gear (Item), please report this to a developer");
				}
				
				return true;
			} else {
				sender.sendMessage("You require more inspiration points to purchase this");
				return true;
			}
		} else {
			sender.sendMessage("This is a player only subcommand");
			return true;
		}
		} catch (CoreStateInitException e)
		{
			return false;
		}
	}
}
