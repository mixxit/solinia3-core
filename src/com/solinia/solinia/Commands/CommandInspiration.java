package com.solinia.solinia.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType.Sender;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Utils.ItemStackUtils;
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
				sendInspirationShopList(sender);
				return true;
			}
			
			switch(args[0].toLowerCase())
			{
				case "buy":
					if (args.length > 1)
					{
						//int cost = 0;
						
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
										int cost = 5;
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
								
							case "item":
								if (sender instanceof Player)
								{
									int id = Integer.parseInt(args[2].toLowerCase());
									ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(id);
									if (i == null)
									{
										sender.sendMessage("This item is no longer available");
										return true;
									}
									if (i.getInspirationWorth() < 1)
									{
										sender.sendMessage("This item is no longer available for purchase");
										return true;
									}

									ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
									
									if (solPlayer.getInspiration() >= i.getInspirationWorth())
									{
										solPlayer.setInspiration(solPlayer.getInspiration() - i.getInspirationWorth());
										PlayerUtils.addToPlayersInventory((Player)sender, i.asItemStack());
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

	private void sendInspirationShopList(CommandSender sender) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Inspiration Shop");
		sender.sendMessage("-----------------");
		
		String world = "world";
		try
		{
			if (sender instanceof Player)
			{
				world = ((Player)sender).getWorld().getName();
				
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				if (solPlayer.getActualLevel() >= 50)
					SendInspirationBuyAALine((Player)sender);
			}
			
			SoliniaWorld solWorld = StateManager.getInstance().getConfigurationManager().getWorld(world);
			if (solWorld != null)
			{
			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
					.getLootTable(solWorld.getInspirationLootTableId());
			if (loottable != null)
			for (ISoliniaLootTableEntry le : StateManager.getInstance().getConfigurationManager()
					.getLootTable(loottable.getId()).getEntries()) {
				ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(le.getLootdropid());
				for(ISoliniaLootDropEntry lde : ld.getEntries())
				{
					ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
					if (i != null && i.getInspirationWorth() > 0)
						SendInspirationBuyLine(sender,i);
				}
				
				}
			
			
			}
			sender.sendMessage("-----------------");
			sender.sendMessage("Sub Commands: " + ChatColor.LIGHT_PURPLE + "sites, buy, send");
			
			if (sender instanceof Player)
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt((Player)sender);
				sender.sendMessage("Points Available: " + ChatColor.LIGHT_PURPLE + solPlayer.getInspiration());
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	private void SendInspirationBuyAALine(Player sender) {
		TextComponent tc = new TextComponent();
		tc.setText(ChatColor.LIGHT_PURPLE + "Buy 1 AA Point " + ChatColor.RESET + " - Cost: 5 inspiration");
		
		TextComponent tc2 = new TextComponent();
		tc2.setText(ChatColor.AQUA + " [Click to Buy]" + ChatColor.RESET);
		String changetext = "/inspiration buy aa";
		tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	

		tc.addExtra(tc2);
		sender.spigot().sendMessage(tc);
	}

	private void SendInspirationBuyLine(CommandSender sender, ISoliniaItem item) {
		TextComponent tc = new TextComponent();
		tc.setText(ChatColor.LIGHT_PURPLE + "Buy " + item.getDisplayname() + ChatColor.RESET + " - Cost: "+item.getInspirationWorth());
		
		TextComponent tc2 = new TextComponent();
		tc2.setText(ChatColor.AQUA + " [Click to Buy]" + ChatColor.RESET);
		String changetext = "/inspiration buy item " + item.getId();
		tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, changetext));	
		tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(item.asItemStack())).create()));
		tc.addExtra(tc2);
		sender.spigot().sendMessage(tc);
	}
}
