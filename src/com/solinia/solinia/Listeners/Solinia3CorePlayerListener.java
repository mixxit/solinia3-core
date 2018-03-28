package com.solinia.solinia.Listeners;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaAsyncPlayerChatEvent;
import com.solinia.solinia.Events.SoliniaPlayerJoinEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;
import com.sun.javafx.css.CalculatedValue;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class Solinia3CorePlayerListener implements Listener {

	Solinia3CorePlugin plugin;

	public Solinia3CorePlayerListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ISoliniaGroup group = StateManager.getInstance().getGroupByMember(event.getPlayer().getUniqueId());
		if (group != null)
		{
			StateManager.getInstance().removePlayerFromGroup(event.getPlayer());
		}
		
		StateManager.getInstance().getChannelManager().sendToDiscordMC(null,StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),event.getPlayer().getName() + " has quit the game");
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)event.getPlayer());
			if (solplayer.getClassObj() != null)
			{
				if (solplayer.getClassObj().isSneakFromCrouch())
				{
					((Player)event.getPlayer()).sendMessage(ChatColor.GRAY + "* You toggle sneak (NPCs are less likely to see you)");
				}
			} 
		} catch (CoreStateInitException e)
		{
			// do nothing
		}
	}

	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) 
	{
		try
		{
			if (StateManager.getInstance().getEntityManager().getTrance(event.getPlayer().getUniqueId()) == true)
			{
				StateManager.getInstance().getEntityManager().setTrance(event.getPlayer().getUniqueId(), false);
			}
			
			Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getPlayer());
			if (mezExpiry != null)
			{
				event.getPlayer().sendMessage("* You are mezzed!");
				if (event.getTo().getY() < event.getFrom().getY())
				{
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
					
				} else {
					event.getTo().setX(event.getFrom().getX());
					event.getTo().setY(event.getFrom().getY());
					event.getTo().setZ(event.getFrom().getZ());
					event.getTo().setYaw(event.getFrom().getYaw());
					event.getTo().setPitch(event.getFrom().getPitch());
				}
				return;
			}

		} catch (CoreStateInitException e)
		{
			// do nothing
		}
	}
	
	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			ItemStack itemstack = event.getOffHandItem();
	    	if (itemstack == null)
	    		return;
	    	
	    	if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
		    {

				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)event.getPlayer());
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	    		if (soliniaitem.getAllowedClassNames().size() == 0)
	    			return;
	
	    		if (solplayer.getClassObj() == null)
	    		{
	    			Utils.CancelEvent(event);;
	    			event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	    			return;
	    		}
	
	    		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	    		{
	    			Utils.CancelEvent(event);;
	    			event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	    			return;
	    		}
	    		
	    		if (soliniaitem.getMinLevel() > solplayer.getLevel())
	    		{
	    			Utils.CancelEvent(event);;
	    			event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
	    			return;
	    		}
	    		
	    		solplayer.updateMaxHp();
		    }
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage("");		
		

		try
		{
			StateManager.getInstance().getEntityManager().clearEntityEffects(plugin, event.getEntity().getUniqueId());
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt(event.getEntity());
			if (player != null)
			{
				double experienceLoss = Utils.calculateExpLoss(player);
				player.reducePlayerNormalExperience(experienceLoss);
				player.dropResurrectionItem((int)experienceLoss);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (Utils.isInventoryMerchant(event.getInventory()))
		{
			onMerchantInventoryClose(event);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		
		// More hassle than it is worth, cancel it always
		if (Utils.isInventoryMerchant(event.getInventory()))
		{
			Utils.CancelEvent(event);;
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (Utils.isInventoryMerchant(event.getInventory()))
		{
			onMerchantInventoryClick(event);
			return;
		}
		
		if (event.isCancelled())
			return;
		
		try
		{
			if (StateManager.getInstance().getPlayerManager().getApplyingAugmentation(event.getView().getPlayer().getUniqueId()) != null &&
					StateManager.getInstance().getPlayerManager().getApplyingAugmentation(event.getView().getPlayer().getUniqueId()) > 0	
					)
			{
				event.getView().getPlayer().sendMessage("* Attempting to apply augmentation");
				ItemStack targetItemStack = event.getCurrentItem();
				ISoliniaItem sourceAugSoliniaItem = StateManager.getInstance().getConfigurationManager().getItem(StateManager.getInstance().getPlayerManager().getApplyingAugmentation(event.getView().getPlayer().getUniqueId()));
				
				if (!sourceAugSoliniaItem.isAugmentation())
				{
					event.getView().getPlayer().sendMessage("The item you are attempting to apply from is not an augmentation");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
				}
				
				if (!Utils.IsSoliniaItem(targetItemStack)
						|| targetItemStack.getType().equals(Material.ENCHANTED_BOOK))
			    {
					event.getView().getPlayer().sendMessage("This augmentation cannot be applied to this item type");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
			    } 
				
				if (targetItemStack.getAmount() != 1)
				{
					event.getView().getPlayer().sendMessage("You cannot apply an augmentation to multiple items at once, please seperate the target item");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
				}
				
				if (ItemStackUtils.getAugmentationItemId(targetItemStack) != null && ItemStackUtils.getAugmentationItemId(targetItemStack) != 0)
				{
					event.getView().getPlayer().sendMessage("This item already has an augmentation applied");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
				}
				
				ISoliniaItem targetSoliniaItem = StateManager.getInstance().getConfigurationManager().getItem(targetItemStack);
				if (!targetSoliniaItem.getAcceptsAugmentationSlotType().equals(sourceAugSoliniaItem.getAugmentationFitsSlotType()))
				{
					event.getView().getPlayer().sendMessage("This augmentation does not fit in this items slot type");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
				}
				
				if (Utils.getPlayerTotalCountOfItemId(((Player)event.getView().getPlayer()),sourceAugSoliniaItem.getId()) < 1)
				{
					event.getView().getPlayer().sendMessage("You do not have enough of this augmentation in your inventory to apply it to an item");
					StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
					System.out.println("Ended applying augmentation");
					event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
					Utils.CancelEvent(event);;
					return;
				}
				
				ItemMeta newMeta = ItemStackUtils.applyAugmentationToItemStack(targetItemStack, sourceAugSoliniaItem.getId());
				targetItemStack.setItemMeta(newMeta);
				((Player)event.getView().getPlayer()).getWorld().dropItemNaturally(((Player)event.getView().getPlayer()).getLocation(), targetItemStack);
				((Player)event.getView().getPlayer()).getInventory().setItem(event.getSlot(), null);
				((Player)event.getView().getPlayer()).updateInventory();
				Utils.removeItemsFromInventory(((Player)event.getView().getPlayer()), sourceAugSoliniaItem.getId(), 1);
				
				event.getView().getPlayer().sendMessage("Augmentation Applied to Item Successfully");
				StateManager.getInstance().getPlayerManager().setApplyingAugmentation(event.getView().getPlayer().getUniqueId(),0);
				System.out.println("Ended applying augmentation");
				event.getView().getPlayer().sendMessage("* Ended applying Augmentation");
				Utils.CancelEvent(event);;
				
				return;
			}
					

			
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)event.getView().getPlayer());
	
			// If armour slot modified, update MaxHP
			// Shift clicking
			if (event.isShiftClick())
			{
				ItemStack itemstack = event.getCurrentItem();
	        	if (itemstack == null)
	        		return;
	        	if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;
	        		
	        		if (solplayer.getClassObj() == null)
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (soliniaitem.getMinLevel() > solplayer.getLevel())
		    		{
		    			Utils.CancelEvent(event);;
		    			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
		    			return;
		    		}
	        		
	        		solplayer.updateMaxHp();
			    }
			}
			
			// Actual clicking
	        if (event.getSlotType().equals(SlotType.ARMOR)) {
	        	ItemStack itemstack = event.getCursor();
	        	if (itemstack == null)
	        		return;
	        	
	        	if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		
	        		if (soliniaitem.getMinLevel() > solplayer.getLevel())
		    		{
		    			Utils.CancelEvent(event);;
		    			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
		    			return;
		    		}
	        		
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;

	        		if (solplayer.getClassObj() == null)
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}

	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		solplayer.updateMaxHp();
			    }
	        }
	        
	        // shield changes to slotid 40
	        if (event.getSlot() == 40) {
	        	ItemStack itemstack = event.getCursor();
	        	if (itemstack == null)
	        		return;
	        	if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;
	        		
	        		if (solplayer.getClassObj() == null)
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			Utils.CancelEvent(event);;
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (soliniaitem.getMinLevel() > solplayer.getLevel())
		    		{
		    			Utils.CancelEvent(event);;
		    			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your are not sufficient level wear this armour");
		    			return;
		    		}
	        		
	        		solplayer.updateMaxHp();
			    }
	        }
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	private void onMerchantInventoryClose(InventoryCloseEvent event) {
		
	}
	
	@EventHandler
	public void onDropItemEvent(PlayerDropItemEvent event)
	{
		// This is to stop drops after closing shop
		if (Utils.IsSoliniaItem(event.getItemDrop().getItemStack()))
		if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().startsWith("Display Item: "))
		{
			event.getItemDrop().getItemStack().setAmount(0);
		}
	}
	
	private void onMerchantInventoryClick(InventoryClickEvent event) {
		int merchantid = 0;
		int npcid = 0;
		ISoliniaNPCMerchant merchant = null;
		ISoliniaNPC npc = null;
		int page = 0;
		int nextpage = 0;
		try
		{
			merchantid = Utils.getInventoryMerchantID(event.getInventory());
			npcid = Utils.getInventoryNPCID(event.getInventory());
			page = Utils.getInventoryPage(event.getInventory());
			nextpage = Utils.getInventoryPage(event.getInventory());
		} catch (Exception e)
		{
			event.getView().getPlayer().sendMessage(e.getMessage());
			e.printStackTrace();
			Utils.CancelEvent(event);;
			return;
		}
		
		if (merchantid == 0 || npcid == 0)
		{
			event.getView().getPlayer().sendMessage("Could not find npc " + npcid + " or merchant " + merchantid + " or page " + page);
			System.out.println("Could not find npc " + npcid + " or merchant " + merchantid);
			Utils.CancelEvent(event);;
			return;
		}
		
		try
		{
			merchant = StateManager.getInstance().getConfigurationManager().getNPCMerchant(merchantid);
			npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
		} catch (CoreStateInitException e)
		{
			event.getView().getPlayer().sendMessage("Cannot sell/buy right now");
			Utils.CancelEvent(event);;
			return;
		}
		
		//event.getView().getPlayer().sendMessage("Detected Merchant Inventory Click for Merchant:" + merchantid + " Slot: " + event.getSlot() + " Raw Slot: " + event.getRawSlot());
		if (event.getRawSlot() < 0)
		{
			Utils.CancelEvent(event);;
			return;
		}
		
		
		
		if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR))
		{
			//event.getView().getPlayer().sendMessage("Detected AIR Cursor, assuming picking up");
			if (event.getRawSlot() > 26)
			{
				// Picking up own item
				//event.getView().getPlayer().sendMessage("Picking up own item");
				
				try {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(event.getCurrentItem());
					if (item == null)
					{
						event.getView().getPlayer().sendMessage("Merchants are not interested in this item");
						Utils.CancelEvent(event);;
						return;
					}
					
					if (item.isTemporary())
					{
						event.getView().getPlayer().sendMessage("Merchants are not interested in temporary items");
						Utils.CancelEvent(event);;
						return;
					}
					
					// Picked up sellable item
					
				} catch (CoreStateInitException e) {
					Utils.CancelEvent(event);;
					event.getView().getPlayer().sendMessage("Cannot sell/buy right now");
					return;
				}
				
				return;
			} else {
				ItemStack pickingUpItem = event.getCurrentItem();
				if (pickingUpItem.getType().equals(Material.BARRIER))
				{
					//event.getView().getPlayer().sendMessage("Ignoring barrier");
					Utils.CancelEvent(event);;
					return;
				}

				// Do not allow movement of UI movers
				if (event.getRawSlot() == 18 || event.getRawSlot() == 26)
				{
					//event.getView().getPlayer().sendMessage("Moving Left or Right");
					if (event.getRawSlot() == 18)
					{
						if ((page - 1) > 0)
						{
							event.getView().getPlayer().closeInventory();
							npc.sendMerchantItemListToPlayer((Player)event.getView().getPlayer(), page - 1);
						}
					}
					
					if (event.getRawSlot() == 26)
					{
						if (nextpage != 0)
						{
							event.getView().getPlayer().closeInventory();
							npc.sendMerchantItemListToPlayer((Player)event.getView().getPlayer(), nextpage + 1);
						}
					}
					Utils.CancelEvent(event);;
					return;
				}
				
				// Do not allow movement of identifiers
				if (event.getRawSlot() == 19)
				{
					//event.getView().getPlayer().sendMessage("Ignoring identifier block");
					Utils.CancelEvent(event);;
					return;
				}
				
				// Picking up merchant item
				//event.getView().getPlayer().sendMessage("Picking up merchant item");
				event.setCursor(event.getCurrentItem());
				
				Utils.CancelEvent(event);;
				return;
			}
			
		}
		
		if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR))
		{
			// Clicking item in cursor onto a slot
			if (event.getRawSlot() > 26)
			{
				// Dropping own item or buying
				if (event.getCursor().getItemMeta().getDisplayName().startsWith("Display Item: "))
				{
					// Buying
					//event.getView().getPlayer().sendMessage("Buying item");
					
					try
					{
						if (!event.getCurrentItem().getType().equals(Material.AIR))
						{
							event.getView().getPlayer().sendMessage("You must place the item you wish to buy on an empty slot");
							event.setCursor(new ItemStack(Material.AIR));
							Utils.CancelEvent(event);;
							return;
						}
					
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(event.getCursor());
						int individualprice = item.getWorth();
	
						// Total price
						
						int price = individualprice * event.getCursor().getAmount();
						
	
						if (price > StateManager.getInstance().getEconomy().getBalance((Player)event.getView().getPlayer())) {
							event.getView().getPlayer().sendMessage("You do not have sufficient balance to buy this item in that quantity.");
							event.setCursor(new ItemStack(Material.AIR));
							Utils.CancelEvent(event);;
							return;
						}
						
						EconomyResponse responsewithdraw = StateManager.getInstance().getEconomy()
								.withdrawPlayer(Bukkit.getOfflinePlayer(((Player)event.getView().getPlayer()).getUniqueId()), price);
						if (responsewithdraw.transactionSuccess()) {
							ItemStack purchase = item.asItemStack();
							event.setCursor(new ItemStack(Material.AIR));
							Utils.CancelEvent(event);;
							event.getClickedInventory().setItem(event.getSlot(), purchase);
							event.getView().getPlayer().sendMessage(
									ChatColor.YELLOW + "* You pay $" + price + " for " + event.getCursor().getAmount() + " " + item.getDisplayname());
							return;
							
						} else {
							System.out.println(
									"Error withdrawing money from your account " + String.format(responsewithdraw.errorMessage));
							event.getView().getPlayer().sendMessage(ChatColor.YELLOW + "* Error withdrawing money from your account "
									+ String.format(responsewithdraw.errorMessage));
							
							event.setCursor(new ItemStack(Material.AIR));
							Utils.CancelEvent(event);;
							return;
						}
					} catch (CoreStateInitException e)
					{
						event.getView().getPlayer().sendMessage("Cannot buy items from the merchant right now");
						event.setCursor(new ItemStack(Material.AIR));
						Utils.CancelEvent(event);;
						return;
					}
				} else {
					// Dropping own item
					return;
				}
				
			} else {
				// Selling items or dropping item back
				if (event.getCursor().getItemMeta().getDisplayName().startsWith("Display Item: "))
				{
					// Returning store item
					event.setCursor(new ItemStack(Material.AIR));
					Utils.CancelEvent(event);;
					return;
					
				} else {
					// Selling
					//event.getView().getPlayer().sendMessage("Selling item to merchant");
					
					try
					{
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(event.getCursor());
						int individualprice = item.getWorth();
	
						// Total price
						int price = individualprice * event.getCursor().getAmount();
						
						EconomyResponse responsedeposit = StateManager.getInstance().getEconomy().depositPlayer((Player)event.getView().getPlayer(), price);
						if (responsedeposit.transactionSuccess()) {
							// Add to buy back list
							//StateManager.getInstance().getEntityManager().addTemporaryMerchantItem(npc.getId(), item.getId(), event.getCursor().getAmount());
							event.getView().getPlayer().sendMessage(ChatColor.YELLOW + "* You recieve $" + price + " as payment");
							event.setCursor(new ItemStack(Material.AIR));
							Utils.CancelEvent(event);;
							return;
						} else {
							System.out.println(
									"Error depositing money to users account " + String.format(responsedeposit.errorMessage));
							event.getView().getPlayer().sendMessage(ChatColor.YELLOW + "* Error depositing money to your account "
									+ String.format(responsedeposit.errorMessage));
							Utils.CancelEvent(event);;
							return;
						}
					} catch (CoreStateInitException e)
					{
						event.getView().getPlayer().sendMessage("Cannot sell item to merchant right now");
						Utils.CancelEvent(event);;
						return;
					}
				}
			}
		}
		
		event.getView().getPlayer().sendMessage("Please alert an admin of this message code: GMMI1");
		Utils.CancelEvent(event);;
		return;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solplayer != null)
			{
				solplayer.updateMaxHp();
				if (solplayer.getBindPoint() != null && !solplayer.getBindPoint().equals(""))
				{
					String[] loc = solplayer.getBindPoint().split(",");
					
					Location location = new Location(Bukkit.getWorld(loc[0]),Double.parseDouble(loc[1]),Double.parseDouble(loc[2]),Double.parseDouble(loc[3]));
					
					event.setRespawnLocation(location);
					event.getPlayer().teleport(location);
				}
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		SoliniaPlayerJoinEvent soliniaevent;
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			
			soliniaevent = new SoliniaPlayerJoinEvent(event, solplayer);
			solplayer.updateDisplayName();
			solplayer.updateMaxHp();
			Bukkit.getPluginManager().callEvent(soliniaevent);
			
			// patch
			if (solplayer.getClassObj() != null)
				solplayer.setChosenClass(true);
			else
				solplayer.setChosenClass(false);
				
			// patch
			if (solplayer.getRace() != null)
				solplayer.setChosenRace(true);
			else
				solplayer.setChosenRace(false);
			
			StateManager.getInstance().getChannelManager().sendToDiscordMC(solplayer,StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),event.getPlayer().getName() + "(" + solplayer.getFullName() + ") has joined the game");
			
		} catch (CoreStateInitException e) {
			event.getPlayer().kickPlayer("Server initialising");
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// Right click air is a cancelled event so we have to ignore it when checking iscancelled
		// We need it for spells
		if (event.getAction() != Action.RIGHT_CLICK_AIR)
		{
			if (event.isCancelled())
				return;
		}
		
		try
		{
			Timestamp mezExpiry = StateManager.getInstance().getEntityManager().getMezzed((LivingEntity) event.getPlayer());
			if (mezExpiry != null)
			{
				event.getPlayer().sendMessage("* You are mezzed!");
				Utils.CancelEvent(event);;
				return;
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		// Handle changing armour
		if ((event.getHand() == EquipmentSlot.HAND || event.getHand() == EquipmentSlot.OFF_HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			try {
				Utils.checkArmourEquip(SoliniaPlayerAdapter.Adapt(event.getPlayer()), event);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			SoliniaPlayerAdapter.Adapt(event.getPlayer()).interact(plugin, event);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
		if (event.isCancelled())
			return;
		
		try {
			ISoliniaItem item = SoliniaItemAdapter.Adapt(event.getItem());
			item.consume(plugin, event.getPlayer());
		} catch (SoliniaItemException | CoreStateInitException e) {
			
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		SoliniaAsyncPlayerChatEvent soliniaevent;
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			
			if (solplayer.getLanguage() == null || solplayer.getLanguage().equals("UNKNOWN"))
			{
				if (solplayer.getRace() == null)
				{
					event.getPlayer().sendMessage("You cannot speak until you set a race /setrace");
					Utils.CancelEvent(event);;
					return;
				} else {
					solplayer.setLanguage(solplayer.getRace().getName().toUpperCase());
				}
			}
			
			soliniaevent = new SoliniaAsyncPlayerChatEvent(event, solplayer,
					event.getMessage());
			Bukkit.getPluginManager().callEvent(soliniaevent);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
