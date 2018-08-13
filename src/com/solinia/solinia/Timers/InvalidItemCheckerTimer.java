package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Utils.ItemStackUtils;

import net.md_5.bungee.api.ChatColor;

public class InvalidItemCheckerTimer  extends BukkitRunnable {
	@Override
	public void run() {
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			List<ItemStack> itemStackBonuses = new ArrayList<ItemStack>() {{ add(player.getInventory().getItemInMainHand()); add(player.getInventory().getItemInOffHand()); addAll(Arrays.asList(player.getInventory().getArmorContents())); }};
			
			for (ItemStack itemstack : itemStackBonuses) {
				if (itemstack == null)
					continue;
				
		        if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999)
		        {
		        	player.sendMessage(ChatColor.RED + "* You appear to have items in your inventory that contain a respiration enchantment greater than 999, please drop and pick this item back up");
		        }
		        
		        if (itemstack.getEnchantmentLevel(Enchantment.DURABILITY) > 999 || itemstack.getEnchantmentLevel(Enchantment.DURABILITY) < 0)
		        {
		        	player.sendMessage(ChatColor.RED + "* You appear to have items in your inventory that contain a durability enchantment greater than 999 or less than 0, please drop and pick this item back up");
		        }
		        
		     // Validate classic augmentation items
		        Integer newaugmentationItemId = ItemStackUtils.getNBTAugmentationItemId(itemstack);
		        Integer oldaugmentationItemId = ItemStackUtils.getClassicAugmentationItemId(itemstack);
		        
		        if (oldaugmentationItemId != null && oldaugmentationItemId > 0 && (newaugmentationItemId == null || newaugmentationItemId == 0))
		        {
		        	player.sendMessage(ChatColor.RED + "* You appear to have items with augmentations on them that need to be updated. They will not apply to your stats until you drop and pick these items back up");
		        }
			}
		}
	}
}
