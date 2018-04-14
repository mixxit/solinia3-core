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
				sender.sendMessage("Insufficient arugments  - /equip equip , /equip unequip");
				return true;
			}

			if (args[0].toUpperCase().equals("EQUIP")) {
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
				solPlayer.getBukkitPlayer().sendMessage("Neck Item: " + item.getDisplayname());
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Neck Item: EMPTY");
			}
			if (solPlayer.getFingersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getFingersItem());
				solPlayer.getBukkitPlayer().sendMessage("Fingers Item: " + item.getDisplayname());
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Fingers Item: EMPTY");
			}

			if (solPlayer.getEarsItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getEarsItem());
				solPlayer.getBukkitPlayer().sendMessage("Ears Item: " + item.getDisplayname());
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Ears Item: EMPTY");
			}

			if (solPlayer.getShouldersItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getShouldersItem());
				solPlayer.getBukkitPlayer().sendMessage("Shoulders Item: " + item.getDisplayname());
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Shoulders Item: EMPTY");
			}
		} catch (CoreStateInitException e) {

		}
	}
}
