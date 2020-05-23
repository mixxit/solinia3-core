package com.solinia.solinia.Utils;

import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	public static int getInventoryPage(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return 0;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return Integer.parseInt(identifierStack.getItemMeta().getLore().get(1));
	}

	public static int getInventoryNextPage(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return 0;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return Integer.parseInt(identifierStack.getItemMeta().getLore().get(2));
	}
	
	public static boolean isInventoryMerchant(Inventory inventory) {
		if (inventory.getSize() != 27) {
			// System.out.println("Inventory size not 27");
			return false;
		}

		if (inventory.getStorageContents()[19] == null) {
			// System.out.println("Identifier is null");
			return false;
		}

		try {
			ItemStack identifierStack = inventory.getStorageContents()[19];
			if (!identifierStack.getItemMeta().getDisplayName().startsWith("MERCHANT:")) {
				// System.out.println("Missing start with merchant on identifier");
				return false;
			}

			if (identifierStack.getEnchantmentLevel(Enchantment.DURABILITY) != 999) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static UUID getInventoryUniversalMerchant(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return null;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return UUID.fromString(identifierStack.getItemMeta().getLore().get(0));
	}
}
