package com.solinia.solinia.Utils;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemStackUtils {
	public static int getWeaponDamage(ItemStack itemStack)
	{
		
		if (isMeleeWeapon(itemStack))
		{
			net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
	        NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
	        return compound.getCompound("generic.attackDamage").getInt("Amount");
		}
		
		return 0;
	}
	
	public static boolean isMeleeWeapon(ItemStack itemStack)
	{
		if (itemStack.getType().equals(Material.WOOD_SWORD) ||
				itemStack.getType().equals(Material.STONE_SWORD) ||
				itemStack.getType().equals(Material.IRON_SWORD) ||
				itemStack.getType().equals(Material.GOLD_SWORD) ||
				itemStack.getType().equals(Material.DIAMOND_SWORD) ||
				itemStack.getType().equals(Material.WOOD_AXE) ||
				itemStack.getType().equals(Material.STONE_AXE) ||
				itemStack.getType().equals(Material.IRON_AXE) ||
				itemStack.getType().equals(Material.GOLD_AXE) ||
				itemStack.getType().equals(Material.DIAMOND_AXE) ||
				itemStack.getType().equals(Material.WOOD_SPADE) ||
				itemStack.getType().equals(Material.STONE_SPADE) ||
				itemStack.getType().equals(Material.IRON_SPADE) ||
				itemStack.getType().equals(Material.GOLD_SPADE) ||
				itemStack.getType().equals(Material.DIAMOND_SPADE)
		)
			return true;
		
		return false;
	}
	
	public static boolean isPotion(ItemStack itemStack)
	{
		if (itemStack.getType().equals(Material.POTION) || 
				itemStack.getType().equals(Material.SPLASH_POTION) ||
				itemStack.getType().equals(Material.LINGERING_POTION)
		)
			return true;
		
		return false;
	}
}
