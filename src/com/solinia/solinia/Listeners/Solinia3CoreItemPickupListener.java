package com.solinia.solinia.Listeners;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;

import net.md_5.bungee.api.ChatColor;

public class Solinia3CoreItemPickupListener implements Listener {
	Solinia3CorePlugin plugin;
	
    public Solinia3CoreItemPickupListener(Solinia3CorePlugin soliniaPlugin) {
    	this.plugin = soliniaPlugin;
	}
    
    @EventHandler
    public void PickupItem(PlayerPickupItemEvent e) {
        ItemStack pickedUpItemStack = e.getItem().getItemStack();
        
        String temporaryGuid = null;
        Integer augmentationItemId = null;
        
        try
        {
	        if (pickedUpItemStack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && pickedUpItemStack.getType().equals(Material.ENCHANTED_BOOK))
		    {
	        	e.getPlayer().sendMessage(ChatColor.GRAY + "You have picked up an ability! To use it, hold it in your hand and right click!");
	        	
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(pickedUpItemStack);
	            if (pickedUpItemStack != null)
	            {
	            	if (latestitem != null)
	            	{
	            		ItemStack latestitemstack = latestitem.asItemStack();
	            		pickedUpItemStack.setItemMeta(latestitemstack.getItemMeta());
	            	} else {
	            		// this is an item that is broken       
	            		e.getPlayer().sendMessage("This item is no longer implemented");
	            		e.setCancelled(true);
	            		e.getItem().remove();
	            	}
	            }
	            
	            if (latestitem.getDiscoverer().equals(""))
	            {
	            	latestitem.setDiscoverer(e.getPlayer().getDisplayName());
	            	e.getPlayer().getServer().broadcastMessage(ChatColor.YELLOW + "* " + latestitem.getDisplayname() + " was discovered by " + e.getPlayer().getDisplayName() + "!");
	            	StateManager.getInstance().getChannelManager().sendToDiscordMC(null,StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),latestitem.getDisplayname() + " was discovered by " + e.getPlayer().getDisplayName() + "!");
	            }
		    }
	        
	        if (pickedUpItemStack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !(pickedUpItemStack.getType().equals(Material.ENCHANTED_BOOK)))
		    {
	        	Map<Enchantment, Integer> oldenchantments = pickedUpItemStack.getEnchantments();
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(pickedUpItemStack);
	        	
	        	if (latestitem.getDiscoverer().equals(""))
	            {
	            	latestitem.setDiscoverer(e.getPlayer().getDisplayName());
	            	e.getPlayer().getServer().broadcastMessage(ChatColor.YELLOW + "* " + latestitem.getDisplayname() + " was discovered by " + e.getPlayer().getDisplayName() + "!");
	            	StateManager.getInstance().getChannelManager().sendToDiscordMC(null,StateManager.getInstance().getChannelManager().getDefaultDiscordChannel(),latestitem.getDisplayname() + " was discovered by " + e.getPlayer().getDisplayName() + "!");
	            }
	        	
	            if (pickedUpItemStack != null)
	            {
	            	if (latestitem != null)
	            	{
	            		ItemStack latestitemstack = latestitem.asItemStack();
	            		
	            		// We need to store this information before we change the itemmeta, so we can update it afterwards
	            		if (latestitem.isTemporary())
	            		{
	            			temporaryGuid = ItemStackUtils.getTemporaryItemGuid(pickedUpItemStack);
	            		}
	            		augmentationItemId = ItemStackUtils.getAugmentationItemId(pickedUpItemStack);
	            		
	            		// Now go and replace the itemmeta
	            		pickedUpItemStack.setItemMeta(latestitemstack.getItemMeta());
	            	} else {
	            		// this is an item that is broken      
	            		e.getPlayer().sendMessage("This item is no longer implemented");
	            		e.setCancelled(true);
	            		e.getItem().remove();
	            		e.getPlayer().updateInventory();
	            	}
	
	            	// Now re-apply enchantments that it had before
	            	for (Map.Entry<Enchantment, Integer> entry : oldenchantments.entrySet()) {
	            		Enchantment key = entry.getKey();
	            		Integer value = entry.getValue();
	            		
	            		if (value < 1000)
	            		{
	            			pickedUpItemStack.addUnsafeEnchantment(key, value);
	            		}
	            	}
	            	
	            	// Since the item is temporary, attempt to apply the temporary timestamp it had prior to this
	            	if (latestitem.isTemporary())
	        		{
	        			pickedUpItemStack.setItemMeta(ItemStackUtils.applyTemporaryStamp(pickedUpItemStack,temporaryGuid));
	        		}
	            	
	            	if (augmentationItemId != null && augmentationItemId != 0)
	        		{
	        			pickedUpItemStack.setItemMeta(ItemStackUtils.applyAugmentationToItemStack(pickedUpItemStack,augmentationItemId));
	        		}
	            }
		    }
        } catch (CoreStateInitException coreException)
        {
        	// do nothing
        }
    }
}
