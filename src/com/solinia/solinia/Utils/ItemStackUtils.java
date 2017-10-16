package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemStackUtils {
	public static int getWeaponDamage(ItemStack itemStack) {

		if (isMeleeWeapon(itemStack)) {
			net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
			return compound.getCompound("generic.attackDamage").getInt("Amount");
		}

		return 0;
	}
	
	public static Integer getAugmentationItemId(ItemStack itemStack)
	{
		try
		{
			ISoliniaItem i = SoliniaItemAdapter.Adapt(itemStack);
			
			if (i.isTemporary())
			{
				for(String loreLine : itemStack.getItemMeta().getLore())
				{
					if (!loreLine.startsWith("Attached Augmentation: "))
						continue;
					
					String[] temporaryData = loreLine.split(" ");
					return Integer.parseInt(temporaryData[1]);
				}
			}
		} catch (SoliniaItemException e) {
			return null;
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}
	
	public static String getTemporaryItemGuid(ItemStack itemStack)
	{
		try
		{
			ISoliniaItem i = SoliniaItemAdapter.Adapt(itemStack);
			
			if (i.isTemporary())
			{
				for(String loreLine : itemStack.getItemMeta().getLore())
				{
					if (!loreLine.startsWith("Temporary: "))
						continue;
					
					String[] temporaryData = loreLine.split(" ");
					return temporaryData[1];
				}
			}
		} catch (SoliniaItemException e) {
			return null;
		} catch (CoreStateInitException e) {
			return null;
		}
		return null;
	}

	public static boolean isMeleeWeapon(ItemStack itemStack) {
		if (itemStack.getType().equals(Material.WOOD_SWORD) || itemStack.getType().equals(Material.STONE_SWORD)
				|| itemStack.getType().equals(Material.IRON_SWORD) || itemStack.getType().equals(Material.GOLD_SWORD)
				|| itemStack.getType().equals(Material.DIAMOND_SWORD) || itemStack.getType().equals(Material.WOOD_AXE)
				|| itemStack.getType().equals(Material.STONE_AXE) || itemStack.getType().equals(Material.IRON_AXE)
				|| itemStack.getType().equals(Material.GOLD_AXE) || itemStack.getType().equals(Material.DIAMOND_AXE)
				|| itemStack.getType().equals(Material.WOOD_SPADE) || itemStack.getType().equals(Material.STONE_SPADE)
				|| itemStack.getType().equals(Material.IRON_SPADE) || itemStack.getType().equals(Material.GOLD_SPADE)
				|| itemStack.getType().equals(Material.DIAMOND_SPADE))
			return true;

		return false;
	}

	public static boolean isPotion(ItemStack itemStack) {
		if (itemStack.getType().equals(Material.POTION) || itemStack.getType().equals(Material.SPLASH_POTION)
				|| itemStack.getType().equals(Material.LINGERING_POTION))
			return true;

		return false;
	}

	public static ItemStack restoreTemporaryStamp(ItemStack pickedUpItemStack, String temporaryGuid) {
		List<String> lore = pickedUpItemStack.getItemMeta().getLore();
		ItemMeta newMeta = pickedUpItemStack.getItemMeta();
		
		List<String> newLore = new ArrayList<String>();
		for(int i = 0; i < lore.size(); i++)
		{
			if (lore.get(i).startsWith("Temporary: "))
			{
				newLore.add("Temporary: " + temporaryGuid);
			} else {
				newLore.add(lore.get(i));
			}
		}
		
		newMeta.setLore(newLore);
		pickedUpItemStack.setItemMeta(newMeta);
		return pickedUpItemStack;
	}
	
	public static ItemStack restoreAugmentationId(ItemStack pickedUpItemStack, Integer itemId) {
		List<String> lore = pickedUpItemStack.getItemMeta().getLore();
		ItemMeta newMeta = pickedUpItemStack.getItemMeta();
		
		List<String> newLore = new ArrayList<String>();
		for(int i = 0; i < lore.size(); i++)
		{
			if (lore.get(i).startsWith("Attached Augmentation: "))
			{
				newLore.add("Attached Augmentation: " + itemId);
			} else {
				newLore.add(lore.get(i));
			}
		}
		
		newMeta.setLore(newLore);
		pickedUpItemStack.setItemMeta(newMeta);
		return pickedUpItemStack;
	}

	public static ItemStack applyAugmentationToItemStack(ItemStack targetItemStack,
			ISoliniaItem sourceAugSoliniaItem) {
		ItemMeta newMeta = targetItemStack.getItemMeta();
		List<String> lore = targetItemStack.getItemMeta().getLore();
		lore.add("Attached Augmentation: " + sourceAugSoliniaItem.getId());
		
		newMeta.setLore(lore);
		targetItemStack.setItemMeta(newMeta);
		return targetItemStack;
	}
}
