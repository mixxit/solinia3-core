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

import net.md_5.bungee.api.ChatColor;

public class Solinia3CoreItemPickupListener implements Listener {
	Solinia3CorePlugin plugin;
	
    public Solinia3CoreItemPickupListener(Solinia3CorePlugin soliniaPlugin) {
    	this.plugin = soliniaPlugin;
	}
    
    @EventHandler
    public void PickupItem(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        
        try
        {
	        if (item.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && item.getType().equals(Material.ENCHANTED_BOOK))
		    {
	        	e.getPlayer().sendMessage(ChatColor.GRAY + "You have picked up an ability! To use it, hold it in your hand and right click!");
	        	
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(item);
	            if (item != null)
	            {
	            	if (latestitem != null)
	            	{
	            		ItemStack latestitemstack = latestitem.asItemStack();
	            		item.setItemMeta(latestitemstack.getItemMeta());
	            	} else {
	            		// this is an item that is broken       
	            		e.getPlayer().sendMessage("This item is no longer implemented");
	            		e.setCancelled(true);
	            		e.getItem().remove();
	            	}
	            }
		    }
	        
	        if (item.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !(item.getType().equals(Material.ENCHANTED_BOOK)))
		    {
	        	Map<Enchantment, Integer> oldenchantments = item.getEnchantments();
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(item);
	            if (item != null)
	            {
	            	if (latestitem != null)
	            	{
	            		ItemStack latestitemstack = latestitem.asItemStack();
	            		item.setItemMeta(latestitemstack.getItemMeta());
	            	} else {
	            		// this is an item that is broken      
	            		e.getPlayer().sendMessage("This item is no longer implemented");
	            		e.setCancelled(true);
	            		e.getItem().remove();
	            	}
	
	            	for (Map.Entry<Enchantment, Integer> entry : oldenchantments.entrySet()) {
	            		Enchantment key = entry.getKey();
	            		Integer value = entry.getValue();
	            		
	            		if (value < 1000)
	            		{
	            			item.addUnsafeEnchantment(key, value);
	            		}
	            	}
	            }
		    }
        } catch (CoreStateInitException coreException)
        {
        	// do nothing
        }
    }
}
