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
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.ItemStackUtils;
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
					args[0].toUpperCase().equals("HANDS") ||
					args[0].toUpperCase().equals("WAIST") ||
					args[0].toUpperCase().equals("HEAD") ||
					args[0].toUpperCase().equals("CHEST") ||
					args[0].toUpperCase().equals("LEGS") ||
					args[0].toUpperCase().equals("FEET")
					) {
				try {
					ItemStack primaryItem = player.getItemOnCursor();
					if (primaryItem.getType() == null || primaryItem.getType().equals(Material.AIR)) {
						player.sendMessage(ChatColor.GRAY
								+ "Empty item in cursor. You must hold the item you want to equip in your cursor");
						return true;
					}
					
					if (!ItemStackUtils.IsSoliniaItem(primaryItem)) {
						player.sendMessage("You can only equip solinia items this way");
						return true;
					}

					ISoliniaItem item = SoliniaItemAdapter.Adapt(primaryItem);
					if (item == null) {
						player.sendMessage("You cannot equip this item this way");
						return true;
					}

					if (item.getMinLevel() > solPlayer.getActualLevel()) {
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

					if (item.getAllowedClassNamesUpper() != null && item.getAllowedClassNamesUpper().size() > 0)
						if (!item.getAllowedClassNamesUpper().contains(solPlayer.getClassObj().getName().toUpperCase())) {
							player.sendMessage(ChatColor.GRAY + "Your class cannot wear this equipment");
							return true;
						}

					if (item.getAllowedRaceNamesUpper() != null && item.getAllowedRaceNamesUpper().size() > 0)
						if (!item.getAllowedRaceNamesUpper().contains(solPlayer.getRace().getName().toUpperCase())) {
							player.sendMessage(ChatColor.GRAY + "Your race cannot wear this equipment");
							return true;
						}

					
					if (item.getMinLevel() > solPlayer.getActualLevel()) {
						player.sendMessage(ChatColor.GRAY + "Your are not sufficient level to wear this equipment");
						return true;
					}


					if (item.getEquipmentSlot().equals(EquipmentSlot.Fingers))
						if (solPlayer.getFingersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Fingers,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Waist))
						if (solPlayer.getWaistItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Waist,item);
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Shoulders))
						if (solPlayer.getShouldersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Shoulders,item);
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Ears))
						if (solPlayer.getEarsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Ears,item);
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Neck))
						if (solPlayer.getNeckItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Neck,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Forearms))
						if (solPlayer.getForearmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Forearms,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Arms))
						if (solPlayer.getArmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Arms,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Hands))
						if (solPlayer.getHandsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Hands,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Head))
						if (solPlayer.getHeadItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Head,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Chest))
						if (solPlayer.getChestItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Chest,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Legs))
						if (solPlayer.getLegsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Legs,item);
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Feet))
						if (solPlayer.getFeetItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							MoveItemToSlot(solPlayer, EquipmentSlot.Feet,item);
							return true;
						}
					
					
					player.sendMessage("You cannot equip this item for equipmentSlot: " + item.getEquipmentSlot());
					return true;

				} catch (SoliniaItemException e) {
					player.sendMessage("You cannot equip this item");
					return true;
				}
			}

			
			if (args[0].toUpperCase().equals("UNEQUIP")) 
			{
				if (args.length < 2) {
					if (solPlayer.getEarsItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Ears);
					}
					if (solPlayer.getNeckItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Neck);
					}
					if (solPlayer.getFingersItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Fingers);
					}
					if (solPlayer.getShouldersItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Shoulders);
					}
					if (solPlayer.getForearmsItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Forearms);
					}
					if (solPlayer.getArmsItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Arms);
					}
					if (solPlayer.getHandsItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Hands);
					}
					if (solPlayer.getWaistItem() > 0) {
						MoveSlotToClaims(solPlayer,EquipmentSlot.Waist);
					}
				} else {
					UnequipSlotByName(solPlayer,args[1].toUpperCase());
				}
				
				solPlayer.updateMaxHp();

				return true;

			}

			showCurrentEquippedItems(solPlayer);

		} catch (CoreStateInitException e) {

		}
		return true;
	}
	
	public EquipmentSlot getEquipmentSlotByName(String slotName)
	{
		switch(slotName.toUpperCase())
		{
			case "EARS":
				return EquipmentSlot.Ears;
			case "NECK":
				return EquipmentSlot.Neck;
			case "FINGERS":
				return EquipmentSlot.Fingers;
			case "SHOULDERS":
				return EquipmentSlot.Shoulders;
			case "ARMS":
				return EquipmentSlot.Arms;
			case "FOREARMS":
				return EquipmentSlot.Forearms;
			case "HANDS":
				return EquipmentSlot.Hands;
			case "WAIST":
				return EquipmentSlot.Waist;
			case "HEAD":
				return EquipmentSlot.Head;
			case "CHEST":
				return EquipmentSlot.Chest;
			case "LEGS":
				return EquipmentSlot.Legs;
			case "FEET":
				return EquipmentSlot.Feet;
			default:
				return EquipmentSlot.None;
		}
	}
	
	private void UnequipSlotByName(ISoliniaPlayer solPlayer, String equipmentSlot) {
		MoveSlotToClaims(solPlayer,getEquipmentSlotByName(equipmentSlot));
	}

	
	public boolean MoveSlotToClaims(ISoliniaPlayer solPlayer, EquipmentSlot equipSlot)
	{
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(solPlayer.getSoliniaItemByEquipmentSlot(equipSlot));
			if (item.isTemporary())
			if (!solPlayer.getWaistItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
			{
				// Delete temporary item
				solPlayer.getBukkitPlayer().sendMessage("Your temporary item has faded from existence");
				solPlayer.setSoliniaItemByEquipmentSlot(equipSlot, 0);
				return true;
			}
			
			SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
			newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
			newclaim.setMcname(solPlayer.getBukkitPlayer().getName());
			newclaim.setItemid(solPlayer.getSoliniaItemByEquipmentSlot(equipSlot));
			newclaim.setClaimed(false);
			StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
			solPlayer.setSoliniaItemByEquipmentSlot(equipSlot,0);
			TextComponent tc = new TextComponent();
			tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
			solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			
		}
		return true;
	}

	private void MoveItemToSlot(ISoliniaPlayer solPlayer, EquipmentSlot slot, ISoliniaItem item) {
		solPlayer.setSoliniaItemByEquipmentSlot(slot,item.getId());
		solPlayer.setSoliniaItemInstanceByEquipmentSlot(slot, StateManager.getInstance().getInstanceGuid());
		solPlayer.getBukkitPlayer().setItemOnCursor(null);
		solPlayer.getBukkitPlayer().updateInventory();
		solPlayer.getBukkitPlayer().sendMessage("You have equipped this item");
		solPlayer.updateMaxHp();
	}

	private void showCurrentEquippedItems(ISoliniaPlayer solPlayer) {
		try {
			solPlayer.sendSlotsAsPacket();
			
			if (solPlayer.getNeckItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Neck);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Neck Item: EMPTY");
			}
			if (solPlayer.getFingersItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Fingers);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Fingers Item: EMPTY");
			}

			if (solPlayer.getEarsItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Ears);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Ears Item: EMPTY");
			}

			if (solPlayer.getShouldersItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Shoulders);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Shoulders Item: EMPTY");
			}
			
			if (solPlayer.getForearmsItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Forearms);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Forearms Item: EMPTY");
			}
			
			if (solPlayer.getArmsItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Arms);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Arms Item: EMPTY");
			}
			
			if (solPlayer.getHandsItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Hands);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Hands Item: EMPTY");
			}
			
			if (solPlayer.getWaistItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Waist);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Waist Item: EMPTY");
			}

			if (solPlayer.getHeadItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Head);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Head Item: EMPTY");
			}

			if (solPlayer.getChestItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Chest);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Chest Item: EMPTY");
			}

			if (solPlayer.getLegsItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Legs);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Legs Item: EMPTY");
			}

			if (solPlayer.getFeetItem() > 0) {
				showEquippedItem(solPlayer,EquipmentSlot.Feet);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Feet Item: EMPTY");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showEquippedItem(ISoliniaPlayer solPlayer, EquipmentSlot slot) {
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
					.getItem(solPlayer.getSoliniaItemByEquipmentSlot(slot));
			TextComponent tc = new TextComponent();
			tc.setText(slot.name() + " Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip " + slot.name().toUpperCase()));
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
					new ComponentBuilder(item.asJsonString()).create()));
			solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
