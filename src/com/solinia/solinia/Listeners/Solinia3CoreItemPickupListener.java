package com.solinia.solinia.Listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.HINT;
import com.solinia.solinia.Utils.ChatUtils;
import com.solinia.solinia.Utils.EntityUtils;
import com.solinia.solinia.Utils.ItemStackUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
	        if (ItemStackUtils.IsSoliniaItem(pickedUpItemStack) && pickedUpItemStack.getType().equals(Material.ENCHANTED_BOOK))
		    {
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(pickedUpItemStack);
	            if (pickedUpItemStack != null)
	            {
	            	if (latestitem != null)
	            	{
	            		if (latestitem.isSpellscroll())
	            		{
	            			ChatUtils.SendHint(e.getPlayer(), HINT.PICKEDUP_SPELL, "PickedUpSpell", false);
	            		}
	            		
	            		ItemStack latestitemstack = latestitem.asItemStack();
	            		pickedUpItemStack.setItemMeta(latestitemstack.getItemMeta());
	            	} else {
	            		// this is an item that is broken       
	            		e.getPlayer().sendMessage("This item is no longer implemented");
	            		EntityUtils.CancelEvent(e);
	            		e.getItem().remove();
	            	}
	            }
	            
	            if (latestitem.getDiscoverer() == null || latestitem.getDiscoverer().equals(""))
	            {
	            	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(e.getPlayer());
	            	latestitem.setDiscoverer(solPlayer.getFullName());
					StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
					ChatUtils.SendHintToServer(HINT.ITEM_DISCOVERED, latestitem.getId()+"^"+solPlayer.getFullName());
	            }
		    }
	        if (ItemStackUtils.IsSoliniaItem(pickedUpItemStack) && !(pickedUpItemStack.getType().equals(Material.ENCHANTED_BOOK)))
		    {
	        	Map<Enchantment, Integer> oldenchantments = pickedUpItemStack.getEnchantments();
	        	ISoliniaItem latestitem = StateManager.getInstance().getConfigurationManager().getItem(pickedUpItemStack);
	        	
	        	if (latestitem.getDiscoverer() == null || latestitem.getDiscoverer().equals(""))
	            {
	            	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(e.getPlayer());
	            	latestitem.setDiscoverer(e.getPlayer().getCustomName());
					StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
					ChatUtils.SendHintToServer(HINT.ITEM_DISCOVERED, latestitem.getId()+"^"+solPlayer.getFullName());
	            }
	        	
	            if (pickedUpItemStack != null)
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
	            		pickedUpItemStack.setItemMeta(ItemStackUtils.applyAugmentation(latestitem, pickedUpItemStack, augmentationItemId).getItemMeta());
	        		}
	            }
		    }
	        
	        // group messages
	        if (ItemStackUtils.IsSoliniaItem(pickedUpItemStack))
		    {
	        	ISoliniaItem item;
				try {
					item = SoliniaItemAdapter.Adapt(pickedUpItemStack);
					if (item.getAllowedClassNamesUpper().size() > 0)
		        	{
		        		ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(e.getPlayer());
		        		if (solPlayer.getGroup() != null && solPlayer.getGroup().getMembersWithoutPets() != null)
		        			for(UUID playerUuid : solPlayer.getGroup().getMembersWithoutPets())
		        			{
		        				if (playerUuid.equals(e.getPlayer().getUniqueId()))
		        					continue;
		        				
		        				Player groupMember = Bukkit.getPlayer(playerUuid);
		        				ISoliniaPlayer groupSolPlayer = SoliniaPlayerAdapter.Adapt(groupMember);
		        				
		        				if (groupSolPlayer != null)
		        				{
		        					if (groupSolPlayer.getClassObj() != null)
		        					if (item.getAllowedClassNamesUpper().contains(groupSolPlayer.getClassObj().getName().toUpperCase()))
		        					{
		        						TextComponent tc = new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "* " + solPlayer.getFullName() + " picked up an item of interest to your class: [" + ChatColor.AQUA + item.getDisplayname() + ChatColor.RESET + "]"));
		        						
		        						tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
		        								new ComponentBuilder(item.asJsonString()).create()));
		        						groupSolPlayer.getBukkitPlayer().spigot().sendMessage(tc);
		        					}
		        				}
		        			}
		        	}
				} catch (SoliniaItemException e1) {
				}
	        	
		    }
	        
        } catch (CoreStateInitException coreException)
        {
        	// do nothing
        }
    }
}
