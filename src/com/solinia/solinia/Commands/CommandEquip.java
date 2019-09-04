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
import com.solinia.solinia.Models.EquipSlots;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.PacketEquipSlots;
import com.solinia.solinia.Models.PacketOpenSpellbook;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Utils.ForgeUtils;
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
					args[0].toUpperCase().equals("WAIST")
					) {
				try {
					ItemStack primaryItem = player.getInventory().getItemInMainHand();
					if (primaryItem.getType() == null || primaryItem.getType().equals(Material.AIR)) {
						player.sendMessage(ChatColor.GRAY
								+ "Empty item in primary hand. You must hold the item you want to equip in your main hand");
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

					if (item.getAllowedRaceNames() != null && item.getAllowedRaceNames().size() > 0)
						if (!item.getAllowedRaceNames().contains(solPlayer.getRace().getName().toUpperCase())) {
							player.sendMessage(ChatColor.GRAY + "Your race cannot wear this equipment");
							return true;
						}

					
					if (item.getMinLevel() > solPlayer.getLevel()) {
						player.sendMessage(ChatColor.GRAY + "Your are not sufficient level to wear this equipment");
						return true;
					}


					if (item.getEquipmentSlot().equals(EquipmentSlot.Fingers))
						if (solPlayer.getFingersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setFingersItem(item.getId());
							solPlayer.setFingersItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Waist))
						if (solPlayer.getWaistItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setWaistItem(item.getId());
							solPlayer.setWaistItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Shoulders))
						if (solPlayer.getShouldersItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setShouldersItem(item.getId());
							solPlayer.setShouldersItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Ears))
						if (solPlayer.getEarsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setEarsItem(item.getId());
							solPlayer.setEarsItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}

					if (item.getEquipmentSlot().equals(EquipmentSlot.Neck))
						if (solPlayer.getNeckItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setNeckItem(item.getId());
							solPlayer.setNeckItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Forearms))
						if (solPlayer.getForearmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setForearmsItem(item.getId());
							solPlayer.setForearmsItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Arms))
						if (solPlayer.getArmsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setArmsItem(item.getId());
							solPlayer.setArmsItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
							return true;
						}
					
					if (item.getEquipmentSlot().equals(EquipmentSlot.Hands))
						if (solPlayer.getHandsItem() > 0) {
							player.sendMessage("You have already equipped an item in that slot");
							return true;
						} else {
							solPlayer.setHandsItem(item.getId());
							solPlayer.setHandsItemInstance(StateManager.getInstance().getInstanceGuid());
							player.getInventory().setItemInMainHand(null);
							player.updateInventory();
							player.sendMessage("You have equipped this item");
							solPlayer.updateMaxHp();
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
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getEarsItem());
						if (item.isTemporary())
						if (!solPlayer.getEarsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setEarsItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getEarsItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setEarsItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
						
					}
					if (solPlayer.getNeckItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getNeckItem());
						if (item.isTemporary())
						if (!solPlayer.getNeckItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setNeckItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getNeckItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setNeckItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					if (solPlayer.getFingersItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getFingersItem());
						if (item.isTemporary())
						if (!solPlayer.getFingersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setFingersItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getFingersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setFingersItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					if (solPlayer.getShouldersItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getShouldersItem());
						if (item.isTemporary())
						if (!solPlayer.getShouldersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setShouldersItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getShouldersItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setShouldersItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					
					if (solPlayer.getForearmsItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getForearmsItem());
						if (item.isTemporary())
						if (!solPlayer.getForearmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setForearmsItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getForearmsItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setForearmsItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					if (solPlayer.getArmsItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getArmsItem());
						if (item.isTemporary())
						if (!solPlayer.getArmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setArmsItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getArmsItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setArmsItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					if (solPlayer.getHandsItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getHandsItem());
						if (item.isTemporary())
						if (!solPlayer.getHandsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setHandsItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getHandsItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setHandsItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
					
					if (solPlayer.getWaistItem() > 0) {
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
								.getItem(solPlayer.getWaistItem());
						if (item.isTemporary())
						if (!solPlayer.getWaistItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solPlayer.setWaistItem(0);
							return true;
						}
						
						SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
						newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
						newclaim.setMcname(player.getName());
						newclaim.setItemid(solPlayer.getWaistItem());
						newclaim.setClaimed(false);
						StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
						solPlayer.setWaistItem(0);
						TextComponent tc = new TextComponent();
						tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
						tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
						sender.spigot().sendMessage(tc);
					}
				} else {
					switch(args[1].toUpperCase())
					{
						case "EARS":
							if (solPlayer.getEarsItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getEarsItem());
								if (item.isTemporary())
								if (!solPlayer.getEarsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setEarsItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getEarsItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setEarsItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "NECK":
							if (solPlayer.getNeckItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getNeckItem());
								if (item.isTemporary())
								if (!solPlayer.getNeckItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setNeckItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getNeckItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setNeckItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "FINGERS":
							if (solPlayer.getFingersItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getFingersItem());
								if (item.isTemporary())
								if (!solPlayer.getFingersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setFingersItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getFingersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setFingersItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "SHOULDERS":
							if (solPlayer.getShouldersItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getShouldersItem());
								if (item.isTemporary())
								if (!solPlayer.getShouldersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setShouldersItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getShouldersItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setShouldersItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "ARMS":
							if (solPlayer.getArmsItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getArmsItem());
								if (item.isTemporary())
								if (!solPlayer.getArmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setArmsItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getArmsItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setArmsItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "FOREARMS":
							if (solPlayer.getForearmsItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getForearmsItem());
								if (item.isTemporary())
								if (!solPlayer.getForearmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setForearmsItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getForearmsItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setForearmsItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "HANDS":
							if (solPlayer.getHandsItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getHandsItem());
								if (item.isTemporary())
								if (!solPlayer.getHandsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setHandsItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getHandsItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setHandsItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
							}
							break;
						case "WAIST":
							if (solPlayer.getWaistItem() > 0) {
								ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
										.getItem(solPlayer.getWaistItem());
								if (item.isTemporary())
								if (!solPlayer.getWaistItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
								{
									// Delete temporary item
									player.sendMessage("Your temporary item has faded from existence");
									solPlayer.setWaistItem(0);
									return true;
								}
								
								SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
								newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
								newclaim.setMcname(player.getName());
								newclaim.setItemid(solPlayer.getWaistItem());
								newclaim.setClaimed(false);
								StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
								solPlayer.setWaistItem(0);
								TextComponent tc = new TextComponent();
								tc.setText("Item moved to your your /claim list " + ChatColor.AQUA + "[ Click here ]" + ChatColor.RESET);
								tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim list"));
								sender.spigot().sendMessage(tc);
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
			solPlayer.sendSlotsAsPacket();
			
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
			
			if (solPlayer.getWaistItem() > 0) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
						.getItem(solPlayer.getWaistItem());
				TextComponent tc = new TextComponent();
				tc.setText("Waist Item: " + ChatColor.LIGHT_PURPLE + item.getDisplayname() + ChatColor.AQUA + " [ Click here to remove ]");
				tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/equip unequip WAIST"));
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentBuilder(item.asJsonString()).create()));
				solPlayer.getBukkitPlayer().spigot().sendMessage(tc);
			} else {
				solPlayer.getBukkitPlayer().sendMessage("Waist Item: EMPTY");
			}
		} catch (CoreStateInitException e) {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
