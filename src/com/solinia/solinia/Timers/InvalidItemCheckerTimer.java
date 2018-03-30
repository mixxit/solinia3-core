package com.solinia.solinia.Timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.Utils;

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
		        	player.sendMessage("You appear to have items in your inventory that contain a respiration enchantment greater than 999, please drop and pick this item back up");
		        }
			}
		}
	}
}
