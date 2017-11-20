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
			for(String loreLine : itemStack.getItemMeta().getLore())
			{
				if (!loreLine.startsWith("Attached Augmentation: "))
					continue;
				
				String[] temporaryData = loreLine.split(" ");
				return Integer.parseInt(temporaryData[2]);
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

	public static ItemMeta applyTemporaryStamp(ItemStack pickedUpItemStack, String temporaryGuid) {
		List<String> lore = pickedUpItemStack.getItemMeta().getLore();
		ItemMeta newMeta = pickedUpItemStack.getItemMeta();
		
		List<String> newLore = new ArrayList<String>();
		for(int i = 0; i < lore.size(); i++)
		{
			// skip, we will re-add it
			if (lore.get(i).startsWith("Temporary: "))
				continue;
			
			newLore.add(lore.get(i));
		}
		newLore.add("Temporary: " + temporaryGuid);
		newMeta.setLore(newLore);
		return newMeta;
	}

	public static ItemMeta applyAugmentationToItemStack(ItemStack targetItemStack,
			Integer sourceItemId) {
		ItemMeta newMeta = targetItemStack.getItemMeta();
		List<String> lore = targetItemStack.getItemMeta().getLore();
		List<String> newLore = new ArrayList<String>();
		for(int i = 0; i < lore.size(); i++)
		{
			// skip, we will re-add it
			if (lore.get(i).startsWith("Attached Augmentation: "))
				continue;

			if (lore.get(i).startsWith("AUG:"))
				continue;

			newLore.add(lore.get(i));
		}
		
		try
		{
			ISoliniaItem soliniaItem = StateManager.getInstance().getConfigurationManager().getItem(sourceItemId);
			if (soliniaItem != null)
			{
				newLore.add("Attached Augmentation: " + sourceItemId);
				
				String stattxt = "";

				if (soliniaItem.getStrength() > 0) {
					stattxt = ChatColor.WHITE + "STR: " + ChatColor.GREEN + soliniaItem.getStrength() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getAgility() > 0) {
					stattxt = ChatColor.WHITE + "AGI: " + ChatColor.GREEN + soliniaItem.getAgility() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getDexterity() > 0) {
					stattxt = ChatColor.WHITE + "DEX: " + ChatColor.GREEN + soliniaItem.getDexterity() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getIntelligence() > 0) {
					stattxt += ChatColor.WHITE + "INT: " + ChatColor.GREEN + soliniaItem.getIntelligence() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getWisdom() > 0) {
					stattxt += ChatColor.WHITE + "WIS: " + ChatColor.GREEN + soliniaItem.getWisdom() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getCharisma() > 0) {
					stattxt += ChatColor.WHITE + "CHA: " + ChatColor.GREEN + soliniaItem.getCharisma() + ChatColor.RESET + " ";
				}

				if (!stattxt.equals(""))
				{
					newLore.add("AUG: " + stattxt);
				}
				
				String actxt = "";
				if (soliniaItem.getAC() > 0) {
					actxt += ChatColor.WHITE + "Armour Class: " + ChatColor.AQUA + soliniaItem.getAC() + ChatColor.RESET + " ";
				}
				
				if (!actxt.equals("")) {
					newLore.add("AUG: " + actxt);
				}
				
				String hptxt = "";
				if (soliniaItem.getHp() > 0) {
					hptxt += ChatColor.WHITE + "HP: " + ChatColor.AQUA + soliniaItem.getHp() + ChatColor.RESET + " ";
				}
				
				if (!hptxt.equals("")) {
					newLore.add("AUG: " + hptxt);
				}
				
				String manatxt = "";
				if (soliniaItem.getMana() > 0) {
					manatxt += ChatColor.WHITE + "Mana: " + ChatColor.AQUA + soliniaItem.getMana() + ChatColor.RESET + " ";
				}
				
				if (!manatxt.equals("")) {
					newLore.add("AUG: " + manatxt);
				}
				
				String resisttxt = "";

				if (soliniaItem.getFireResist() > 0) {
					resisttxt += ChatColor.WHITE + "FR: " + ChatColor.AQUA + soliniaItem.getFireResist() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getColdResist() > 0) {
					resisttxt += ChatColor.WHITE + "CR: " + ChatColor.AQUA + soliniaItem.getColdResist() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getMagicResist() > 0) {
					resisttxt += ChatColor.WHITE + "MR: " + ChatColor.AQUA + soliniaItem.getMagicResist() + ChatColor.RESET + " ";
				}

				if (soliniaItem.getPoisonResist() > 0) {
					resisttxt += ChatColor.WHITE + "PR: " + ChatColor.AQUA + soliniaItem.getPoisonResist() + ChatColor.RESET + " ";
				}

				if (!resisttxt.equals("")) {
					newLore.add("AUG: " + resisttxt);
				}

				String regentxt = "";

				if (soliniaItem.getHpregen() > 0 || soliniaItem.getMpregen() > 0) {
					if (soliniaItem.getHpregen() > 0) {
						regentxt = ChatColor.WHITE + "HPRegen: " + ChatColor.YELLOW + soliniaItem.getHpregen()
								+ ChatColor.RESET;
					}

					if (soliniaItem.getMpregen() > 0) {
						if (!regentxt.equals(""))
							regentxt += " ";
						regentxt += ChatColor.WHITE + "MPRegen: " + ChatColor.YELLOW + soliniaItem.getMpregen()
								+ ChatColor.RESET;
					}
				}
				
				if (!regentxt.equals("")) {
					newLore.add("AUG: " + regentxt);
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		

		newMeta.setLore(newLore);
		return newMeta;
	}
}
