package com.solinia.solinia.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandEquip implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;

		try {
			Player player = (Player) sender;
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);

			if (args.length < 1) {
				showCurrentEquippedItems(solPlayer);
				sender.sendMessage("Insufficient arugments  - /equip equip , /equip unequip, /equip unequip neck");
				return true;
			}

			if (
					args[0].toUpperCase().equals("EQUIP") || 
					args[0].toUpperCase().equals("EARS") || 
					args[0].toUpperCase().equals("NECK") || 
					args[0].toUpperCase().equals("FINGERS") || 
					args[0].toUpperCase().equals("SHOULDERS") ||
					args[0].toUpperCase().equals("ARMS") ||
					args[0].toUpperCase().equals("FOREARMS") ||
					args[0].toUpperCase().equals("HANDS")
					) {
				try {
					ItemStack primaryItem = player.getInventory().getItemInMainHand();
					if (primaryItem.getType().equals(Material.AIR)) {
						player.sendMessage(ChatColor.GRAY
								+ "Empty item in primary hand. You must hold the item you want to equip in your main hand");
						return false;
					}

					if (!Utils.IsSoliniaItem(primaryItem)) {
						player.sendMessage("You can only equip solinia items this way");
						return true;
					}

					ISoliniaItem item = SoliniaItemAdapter.Adapt(primaryItem);
					if (item == null) {
						player.sendMessage("You cannot equip this item this way");
						return true;
					}

					if (item.getMinLevel() > solPlayer.getLevel()) {
						player.sendMessage("You cannot equip this item (minlevel: " + item.getMinLevel() + ")");
						return true;
					}

					if (solPlayer.getClassObj() == null) {
						player.sendMessage(ChatColor.GRAY + "Your class cannot wear this equipment");
						return true;
					}

					if (item.isSpellscroll()) {
						player.sendMessage("You cannot equip this item");
						return true;
					}

					if (item.getAllowedClassNames() != null && item.getAllowedClassNames().size() > 0)
						if (!item.getAllowedClassNames().contains(solPlayer.getClassObj().getName().toUpperCase())) {
							player.sendMessage(ChatColor.GRAY + "Your class cannot wear this equipment");
							return true;
						}

					if (item.getMinLevel() > solPlayer.getLevel()) {
						player.sendMessage(ChatColor.GRAY + "Your are not sufficient level to wear this equipment");
						return true;
					}

					if (item.isFingersItem())
						if (solPlayer.getFingersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setFingersItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.isShouldersItem())
						if (solPlayer.getShouldersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setShouldersItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.isEarsItem())
						if (solPlayer.getEarsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setEarsItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.isNeckItem())
						if (solPlayer.getNeckItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setNeckItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.isForearmsItem())
						if (solPlayer.getForearmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setForearmsItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.isArmsItem())
						if (solPlayer.getArmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setArmsItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.isHandsItem())
						if (solPlayer.getHandsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setHandsItem(item.getId());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					return false;

				} catch (SoliniaItemException e) {
					player.sendMessage("You cannot equip this item");
					return true;
				}
			}

			
			if (args[0].toUpperCase().equals("UNEQUIP")) 
			{
				if (args.length < 2) {
					if (solPlayer.getEarsItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getEarsItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setEarsItem(0);
						sender.sendMessage("Your earrings have been moved to the /claim list");
					}
					if (solPlayer.getNeckItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getNeckItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setNeckItem(0);
						sender.sendMessage("Your necklace has been moved to the /claim list");
					}
					if (solPlayer.getFingersItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getFingersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setFingersItem(0);
						sender.sendMessage("Your rings have been moved to the /claim list");
					}
					if (solPlayer.getShouldersItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getShouldersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setShouldersItem(0);
						sender.sendMessage("Your cloak has been moved to the /claim list");
					}
					
					if (solPlayer.getForearmsItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getShouldersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setForearmsItem(0);
						sender.sendMessage("Your forearms item has been moved to the /claim list");
					}
					if (solPlayer.getArmsItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getShouldersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setArmsItem(0);
						sender.sendMessage("Your arms item has been moved to the /claim list");
					}
					if (solPlayer.getHandsItem() > 0) {
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getShouldersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setHandsItem(0);
						sender.sendMessage("Your hands item has been moved to the /claim list");
					}
				} else {
					switch(args[1].toUpperCase())
					{
						case "EARS":
							if (solPlayer.getEarsItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getEarsItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setEarsItem(0);
								sender.sendMessage("Your earrings have been moved to the /claim list");
							}
							break;
						case "NECK":
							if (solPlayer.getNeckItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getNeckItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setNeckItem(0);
								sender.sendMessage("Your necklace has been moved to the /claim list");
							}
							break;
						case "FINGERS":
							if (solPlayer.getFingersItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getFingersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setFingersItem(0);
								sender.sendMessage("Your rings have been moved to the /claim list");
							}
							break;
						case "SHOULDERS":
							if (solPlayer.getShouldersItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getShouldersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setShouldersItem(0);
								sender.sendMessage("Your cloak has been moved to the /claim list");
							}
							break;
						case "ARMS":
							if (solPlayer.getArmsItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getShouldersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setArmsItem(0);
								sender.sendMessage("Your arms item has been moved to the /claim list");
							}
							break;
						case "FOREARMS":
							if (solPlayer.getForearmsItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getShouldersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setForearmsItem(0);
								sender.sendMessage("Your forearms item has been moved to the /claim list");
							}
							break;
						case "HANDS":
							if (solPlayer.getHandsItem() > 0) {
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getShouldersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setHandsItem(0);
								sender.sendMessage("Your hands item has been moved to the /claim list");
							}
							break;
						default:
							sender.sendMessage("Unknown slot to unequip");
							break;
					}
				}
				
				solPlayer.updateMaxHp();

				return true;

			}

			showCurrentEquippedItems(solPlayer);

		} catch (CoreStateInitException e) {

		}
		return true;
	}

	private void showCurrentEquippedItems(ISoliniaPlayer solPlayer) {
		try {
			if (solPlayer.getNeckItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getNeckItem());
				TextComponent tc = new TextComponent();
				tc.setText("Neck Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip NECK"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Neck Item: EMPTY");
			}
			if (solPlayer.getFingersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getFingersItem());
				TextComponent tc = new TextComponent();
				tc.setText("Fingers Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip FINGERS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Fingers Item: EMPTY");
			}

			if (solPlayer.getEarsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getEarsItem());
				TextComponent tc = new TextComponent();
				tc.setText("Ears Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip EARS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Ears Item: EMPTY");
			}

			if (solPlayer.getShouldersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getShouldersItem());
				TextComponent tc = new TextComponent();
				tc.setText("Shoulders Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip SHOULDERS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Shoulders Item: EMPTY");
			}
			
			if (solPlayer.getForearmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getForearmsItem());
				TextComponent tc = new TextComponent();
				tc.setText("Forearms Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip FOREARMS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Forearms Item: EMPTY");
			}
			
			if (solPlayer.getArmsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getArmsItem());
				TextComponent tc = new TextComponent();
				tc.setText("Arms Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip ARMS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Arms Item: EMPTY");
			}
			
			if (solPlayer.getHandsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getHandsItem());
				TextComponent tc = new TextComponent();
				tc.setText("Hands Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip HANDS"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Hands Item: EMPTY");
			}
		} catch (CoreStateInitException e) {

		}
	}
}
