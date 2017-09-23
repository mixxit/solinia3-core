package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.SoliniaAsyncPlayerChatEvent;
import com.solinia.solinia.Events.SoliniaPlayerJoinEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaGroup;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

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
	    	
	    	if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
		    {

				ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)event.getPlayer());
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	    		if (soliniaitem.getAllowedClassNames().size() == 0)
	    			return;
	
	    		if (solplayer.getClassObj() == null)
	    		{
	    			event.setCancelled(true);
	    			event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	    			return;
	    		}
	
	    		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	    		{
	    			event.setCancelled(true);
	    			event.getPlayer().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
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
		try
		{
			StateManager.getInstance().getEntityManager().clearEntityEffects(event.getEntity().getUniqueId());
		} catch (CoreStateInitException e)
		{
			
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt((Player)event.getView().getPlayer());
	
			// If armour slot modified, update MaxHP
			
			// Shift clicking
			if (event.isShiftClick())
			{
				ItemStack itemstack = event.getCurrentItem();
	        	if (itemstack == null)
	        		return;
	        	if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;
	        		
	        		if (solplayer.getClassObj() == null)
	        		{
	        			event.setCancelled(true);
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			event.setCancelled(true);
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
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
	        	if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;

	        		if (solplayer.getClassObj() == null)
	        		{
	        			event.setCancelled(true);
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}

	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			event.setCancelled(true);
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
	        	if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
			    {
	        		ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
	        		if (soliniaitem.getAllowedClassNames().size() == 0)
	        			return;
	        		
	        		if (solplayer.getClassObj() == null)
	        		{
	        			event.setCancelled(true);
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	        			return;
	        		}
	        		
	        		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName().toUpperCase()))
	        		{
	        			event.setCancelled(true);
	        			event.getView().getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
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

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		try {
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solplayer != null)
				solplayer.updateMaxHp();
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
					event.setCancelled(true);
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
