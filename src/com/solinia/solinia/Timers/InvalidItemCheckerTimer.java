package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class InvalidItemCheckerTimer  extends BukkitRunnable {
	@Override
	public void run() {
		try
		{
			runItemChecker();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void runItemChecker() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			List<ItemStack> itemStackBonuses = new ArrayList<ItemStack>() {/**
				 * 
				 */
				private static final long serialVersionUID = -7448639939554936637L;

			{ add(player.getInventory().getItemInMainHand()); add(player.getInventory().getItemInOffHand()); addAll(Arrays.asList(player.getInventory().getArmorContents())); }};
			
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
			}
		}
	}
}
