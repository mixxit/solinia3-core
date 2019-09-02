package com.solinia.solinia.Timers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class PlayerInventoryValidatorTimer extends BukkitRunnable {

	@Override
	public void run() {
		try
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				validatePlayerItems(player);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void validatePlayerItems(Player player) {
		try
		{
		
			Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Debug: Validating player items");
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			List<Integer> slots = new ArrayList<Integer>();
			// hot bar
			slots.add(0);
			slots.add(1);
			slots.add(2);
			slots.add(3);
			slots.add(4);
			slots.add(5);
			slots.add(6);
			slots.add(7);
			slots.add(8);
			
			// armor
			slots.add(36);
			slots.add(37);
			slots.add(38);
			slots.add(39);
			// offhand
			slots.add(40);
			
			// Check worn stuff
			for (int slotId = 0; slotId <= 40; slotId++)
			{
				if (player.getInventory().getItem(slotId) == null)
				{
					continue;
				}
				
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(slotId));
					// Check temporary items
					if (i.isTemporary())
					{
						for(String loreLine : player.getInventory().getItem(slotId).getItemMeta().getLore())
						{
							if (!loreLine.startsWith("Temporary: "))
								continue;
							
							if (!loreLine.equals("Temporary: " + StateManager.getInstance().getInstanceGuid()))
							{
								// Delete temporary item
								player.sendMessage("Your temporary item has faded from existence");
								Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Removed temporary item: " + i.getDisplayname());
								player.getInventory().setItem(slotId, null);
								player.updateInventory();
								break;
							}
						}
						
					}
					
					// Only monitor the defined slots
					if (!slots.contains(slotId))
						continue;
					
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Validating player slot: " + slotId);
					
					// Check out of date items
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Checking isItemStackUptoDate: " + ItemStackUtils.isItemStackUptoDate(player.getInventory().getItem(slotId),i));
					if (!ItemStackUtils.isItemStackUptoDate(player.getInventory().getItem(slotId),i))
		    		{
						if (ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						int count = player.getInventory().getItem(slotId).getAmount();
						if (count == 0)
							count = 1;
						for (int x = 0; x < count; x++)
						{
							Utils.AddAccountClaim(player.getName(),i.getId());
						}
						Timestamp lastUpdatedTimeSolItem = i.getLastUpdatedTime();
						
						String solUp = "";
						String stackUp = "";

						if (lastUpdatedTimeSolItem != null)
						{
							solUp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(lastUpdatedTimeSolItem);
						}
						Timestamp lastUpdatedTimeStack = ItemStackUtils.GetSolLastUpdated(player.getInventory().getItem(slotId));
						if (lastUpdatedTimeStack != null)
						{
							stackUp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(lastUpdatedTimeStack);
						}
						
						
						player.sendMessage(ChatColor.GRAY + "Your out of date item " + i.getDisplayname() + " has been added to your claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved out of date item to claims: " + i.getId() + " " + i.getDisplayname() + solUp + " vs " + stackUp);
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						
						continue;
		    		}
					
					// Check class armour and offhand items for wrong level
					if (slotId > 35 && i.getMinLevel() > solplayer.getLevel() && !i.isSpellscroll())
		    		{
						if (ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						int count = player.getInventory().getItem(slotId).getAmount();
						if (count == 0)
							count = 1;
						for (int x = 0; x < count; x++)
						{
							Utils.AddAccountClaim(player.getName(),i.getId());
						}
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your /claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved minlevel item to claims: " + i.getDisplayname());
						continue;
		    		}
					
					if (i.getAllowedClassNames().size() < 1)
						continue;
					
					// Check class armour and offhand items for wrong class (where the player has no class)
					if (slotId > 35 && solplayer.getClassObj() == null && !i.isSpellscroll())
					{
						if (ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						int count = player.getInventory().getItem(slotId).getAmount();
						if (count == 0)
							count = 1;
						for (int x = 0; x < count; x++)
						{
							Utils.AddAccountClaim(player.getName(),i.getId());
						}
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your /claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved wrong class item claims: " + i.getDisplayname());
						continue;
					}
					
					// Check class armour and offhand items for wrong class (where the player has a class)
					if (slotId > 35 && !i.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()) && !i.isSpellscroll())
					{
						if (ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						int count = player.getInventory().getItem(slotId).getAmount();
						if (count == 0)
							count = 1;
						for (int x = 0; x < count; x++)
						{
							Utils.AddAccountClaim(player.getName(),i.getId());
						}
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your /claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved wrong class item to claims: " + i.getDisplayname());
						continue;
					}
					
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Finished validating player slot: " + slotId);
					
				} catch (SoliniaItemException e) {
					continue;
				}
			}
			
			// Check worn jewelry/additional armour
			validateEquipSlot(solplayer, solplayer.getArmsItem(),solplayer.getArmsItemInstance(), EquipmentSlot.Arms);
			validateEquipSlot(solplayer, solplayer.getEarsItem(),solplayer.getEarsItemInstance(), EquipmentSlot.Ears);
			validateEquipSlot(solplayer, solplayer.getFingersItem(),solplayer.getFingersItemInstance(), EquipmentSlot.Fingers);
			validateEquipSlot(solplayer, solplayer.getForearmsItem(),solplayer.getForearmsItemInstance(), EquipmentSlot.Forearms);
			validateEquipSlot(solplayer, solplayer.getHandsItem(),solplayer.getHandsItemInstance(), EquipmentSlot.Hands);
			validateEquipSlot(solplayer, solplayer.getNeckItem(),solplayer.getNeckItemInstance(), EquipmentSlot.Neck);
			validateEquipSlot(solplayer, solplayer.getShouldersItem(),solplayer.getShouldersItemInstance(), EquipmentSlot.Shoulders);
			validateEquipSlot(solplayer, solplayer.getWaistItem(),solplayer.getWaistItemInstance(), EquipmentSlot.Waist);

		
		} catch (CoreStateInitException e)
		{
			// try next loop
			return;
		}
	}

	private void validateEquipSlot(ISoliniaPlayer solPlayer, int itemId, String itemInstance, EquipmentSlot slot) {
		if (itemId > 0) {
			try
			{
				ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(itemId);
				if (i == null)
					return;
				
				if (i.isTemporary())
				{
					if (!itemInstance.equals(StateManager.getInstance().getInstanceGuid()))
					{
						// Delete temporary item
						solPlayer.getBukkitPlayer().sendMessage("Your temporary item has faded from existence");
						solPlayer.setEquipSlotItem(slot, 0);
					}
				}
				
				if (i.getMinLevel() > solPlayer.getLevel())
	    		{
					solPlayer.setEquipSlotItem(slot, 0);
					Utils.AddAccountClaim(solPlayer.getBukkitPlayer().getName(),i.getId());
					solPlayer.getBukkitPlayer().sendMessage(ChatColor.GRAY + "You cannot wear equip item " + i.getDisplayname() + " so it has been added to your /claims");
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",solPlayer.getBukkitPlayer().getName(),"Moved minlevel item to claims (equip): " + i.getDisplayname());
	    		}
				
			} catch (CoreStateInitException e) {
				
			}
		}
	}
}
