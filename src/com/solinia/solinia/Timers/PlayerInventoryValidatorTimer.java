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
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class PlayerInventoryValidatorTimer extends BukkitRunnable {

	@Override
	public void run() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			validatePlayerItems(player);
		}
	}

	private void validatePlayerItems(Player player) {
		try
		{
		
			Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Debug: Validating player items");
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			
			List<Integer> slots = new ArrayList<Integer>();
			slots.add(36);
			slots.add(37);
			slots.add(38);
			slots.add(39);
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
					
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Checking isItemStackUptoDate: " + ItemStackUtils.isItemStackUptoDate(player.getInventory().getItem(slotId),i));
					if (!ItemStackUtils.isItemStackUptoDate(player.getInventory().getItem(slotId),i))
		    		{
						if (ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						Utils.AddAccountClaim(player.getName(),i.getId());
						Timestamp lastUpdatedTimeSolItem = i.getLastUpdatedTime();
						
						String solUp = "";
						String stackUp = "";

						if (lastUpdatedTimeSolItem != null)
						{
							solUp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(lastUpdatedTimeSolItem);
						}
						Timestamp lastUpdatedTimeStack = Utils.GetSolLastUpdated(player.getInventory().getItem(slotId));
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
					
					if (i.getMinLevel() > solplayer.getLevel())
		    		{
						if (ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						Utils.AddAccountClaim(player.getName(),i.getId());
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved minlevel item to claims: " + i.getDisplayname());
						continue;
		    		}
					
					if (i.getAllowedClassNames().size() < 1)
						continue;
					
					if (solplayer.getClassObj() == null)
					{
						if (ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						Utils.AddAccountClaim(player.getName(),i.getId());
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved wrong class item claims: " + i.getDisplayname());
						continue;
					}
					
					if (!i.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
					{
						if (ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId)) != null)
						{
							Integer augmentationId = ItemStackUtils.getNBTAugmentationItemId(player.getInventory().getItem(slotId));
							ISoliniaItem augItem = null;
							if (augmentationId != null && augmentationId != 0) {
								augItem = StateManager.getInstance().getConfigurationManager().getItem(augmentationId);
								Utils.AddAccountClaim(player.getName(),augItem.getId());
							}
						}
						
						Utils.AddAccountClaim(player.getName(),i.getId());
						player.getInventory().setItem(slotId, null);
						player.updateInventory();
						player.sendMessage(ChatColor.GRAY + "You cannot wear " + i.getDisplayname() + " so it has been added to your claims");
						Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Moved wrong class item to claims: " + i.getDisplayname());
						continue;
					}
					
					Utils.DebugLog("PlayerInventoryValidatorTimer","validatePlayerItems",player.getName(),"Finished validating player slot: " + slotId);
					
				} catch (SoliniaItemException e) {
					continue;
				}
			}
			
			// Check worn jewelry/additional armour
			
			if (solplayer.getEarsItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getEarsItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getEarsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setEarsItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			if (solplayer.getNeckItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getNeckItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getNeckItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setNeckItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}
			
			if (solplayer.getFingersItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getFingersItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getFingersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setFingersItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			if (solplayer.getShouldersItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getShouldersItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getShouldersItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setShouldersItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			if (solplayer.getForearmsItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getForearmsItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getForearmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setForearmsItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			if (solplayer.getArmsItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getArmsItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getArmsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setArmsItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			if (solplayer.getHandsItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getHandsItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getHandsItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setHandsItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}
			
			if (solplayer.getWaistItem() > 0) {
				try
				{
					ISoliniaItem i = SoliniaItemAdapter.Adapt(player.getInventory().getItem(solplayer.getWaistItem()));
					
					if (i.isTemporary())
					{
						if (!solplayer.getWaistItemInstance().equals(StateManager.getInstance().getInstanceGuid()))
						{
							// Delete temporary item
							player.sendMessage("Your temporary item has faded from existence");
							solplayer.setWaistItem(0);
						}
					}
				} catch (SoliniaItemException e) {
					
				}
			}

			// Check reagents
		
		} catch (CoreStateInitException e)
		{
			// try next loop
			return;
		}
	}
}
