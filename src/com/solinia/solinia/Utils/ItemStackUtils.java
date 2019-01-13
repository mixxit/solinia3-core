package com.solinia.solinia.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.AttributeModifier;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.GameProfileSerializer;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagString;

public class ItemStackUtils {
	
	public static int getWeaponDamage(ItemStack itemStack, EnumItemSlot itemSlot) {
        double attackDamage = 1.0;
        UUID uuid = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        net.minecraft.server.v1_13_R2.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_13_R2.Item item = craftItemStack.getItem();
        if(item instanceof net.minecraft.server.v1_13_R2.ItemSword || item instanceof net.minecraft.server.v1_13_R2.ItemTool || item instanceof net.minecraft.server.v1_13_R2.ItemHoe) {
            Multimap<String, AttributeModifier> map = item.a(itemSlot);
            Collection<AttributeModifier> attributes = map.get(GenericAttributes.ATTACK_DAMAGE.getName());
            if(!attributes.isEmpty()) {
                for(AttributeModifier am: attributes) {
                    if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 0) attackDamage += am.d();
                }
                double y = 1;
                for(AttributeModifier am: attributes) {
                    if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 1) y += am.d();
                }
                attackDamage *= y;
                for(AttributeModifier am: attributes) {
                    if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 2) attackDamage *= (1 + am.d());
                }
            }
        }
        
        Long rounded = Math.round(attackDamage);
        
        if (rounded > Integer.MAX_VALUE)
        	rounded = (long)Integer.MAX_VALUE;
        
        int damage = Integer.valueOf(rounded.intValue());
        
        return damage;
    }
	
	public static Integer getSoliniaItemId(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;

		if (itemStack.getItemMeta() == null)
		{
			return null;
		}
		
		NamespacedKey soliniaIdKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("Solinia3Core"), "soliniaid");
		ItemMeta itemMeta = itemStack.getItemMeta();
		CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
		// old system
		if(tagContainer.hasCustomTag(soliniaIdKey , ItemTagType.STRING)) {
			itemMeta.getCustomTagContainer().setCustomTag(soliniaIdKey, ItemTagType.INTEGER, Integer.parseInt(tagContainer.getCustomTag(soliniaIdKey, ItemTagType.STRING)));
			itemStack.setItemMeta(itemMeta);
		}
		if(tagContainer.hasCustomTag(soliniaIdKey , ItemTagType.INTEGER)) {
		    return tagContainer.getCustomTag(soliniaIdKey, ItemTagType.INTEGER);
		}
		
		return null;
	}
	
	public static Long getSoliniaLastUpdated(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;

		if (itemStack.getItemMeta() == null)
			return null;
		
		NamespacedKey soliniaLastUpdatedKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("Solinia3Core"), "sollastupdated");
		ItemMeta itemMeta = itemStack.getItemMeta();
		CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
		
		if(tagContainer.hasCustomTag(soliniaLastUpdatedKey , ItemTagType.LONG)) {
		    return tagContainer.getCustomTag(soliniaLastUpdatedKey, ItemTagType.LONG);
		}
		
		return null;
	}
	
	public static String ConvertItemStackToJsonRegular(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_13_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }
	
	public static Integer getClassicAugmentationItemId(ItemStack itemStack)
	{
		if (!Utils.IsSoliniaItem(itemStack))
			return null;
		
		if (itemStack.getItemMeta().getLore() != null)
		for(String loreLine : itemStack.getItemMeta().getLore())
		{
			if (!loreLine.startsWith("Attached Augmentation: "))
				continue;
			
			String[] temporaryData = loreLine.split(" ");
			
			return Integer.parseInt(temporaryData[2]);
		}
		
		return null;
	}
	
	public static Integer getNBTAugmentationItemId(ItemStack itemStack)
	{
		if (!Utils.IsSoliniaItem(itemStack))
			return null;
		
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		
		String soliniaaug1id = compound.getString("soliniaaug1id");
		
		if (soliniaaug1id == null || soliniaaug1id.equals(""))
			return null;
		
		return Integer.parseInt(soliniaaug1id);
	}
	
	public static String getSkullTexture(ItemStack itemStack)
	{
		String textureValue = "";
		if (Utils.isSkullItem(itemStack))
	    {
			net.minecraft.server.v1_13_R2.ItemStack rawItemStack = CraftItemStack.asNMSCopy(itemStack);
	        if (rawItemStack.hasTag()) {
	            NBTTagCompound tag = rawItemStack.getTag();
	            if (tag.hasKeyOfType("SkullOwner", 10)) {
	                GameProfile profile = GameProfileSerializer.deserialize(tag.getCompound("SkullOwner"));
	                if (profile != null) {
	                    Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
	                    if (property != null)
	                    	textureValue = property.getValue();
	                }
	            }
	        }
	    }
		
		return textureValue;
	}
	
	public static Integer getMerchantItemWorth(ItemStack itemStack)
	{
		if (!Utils.IsSoliniaItem(itemStack))
			return null;
		
		for(String loreLine : itemStack.getItemMeta().getLore())
		{
			if (!loreLine.startsWith("Worth: "))
				continue;
			
			String[] temporaryData = loreLine.split(" ");
			
			return Integer.parseInt(temporaryData[1]);
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
		if (itemStack.getType().equals(Material.WOODEN_SWORD) || itemStack.getType().equals(Material.STONE_SWORD)
				|| itemStack.getType().equals(Material.IRON_SWORD) || itemStack.getType().equals(Material.GOLDEN_SWORD)
				|| itemStack.getType().equals(Material.DIAMOND_SWORD) || itemStack.getType().equals(Material.WOODEN_AXE)
				|| itemStack.getType().equals(Material.STONE_AXE) || itemStack.getType().equals(Material.IRON_AXE)
				|| itemStack.getType().equals(Material.GOLDEN_AXE) || itemStack.getType().equals(Material.DIAMOND_AXE)
				|| itemStack.getType().equals(Material.WOODEN_HOE) || itemStack.getType().equals(Material.STONE_HOE)
				|| itemStack.getType().equals(Material.IRON_HOE) || itemStack.getType().equals(Material.GOLDEN_HOE) || itemStack.getType().equals(Material.DIAMOND_HOE)
				|| itemStack.getType().equals(Material.WOODEN_PICKAXE) || itemStack.getType().equals(Material.STONE_PICKAXE)
				|| itemStack.getType().equals(Material.IRON_PICKAXE) || itemStack.getType().equals(Material.GOLDEN_PICKAXE) || itemStack.getType().equals(Material.DIAMOND_PICKAXE)
				|| itemStack.getType().equals(Material.WOODEN_SHOVEL) || itemStack.getType().equals(Material.STONE_SHOVEL)
				|| itemStack.getType().equals(Material.IRON_SHOVEL) || itemStack.getType().equals(Material.GOLDEN_SHOVEL)
				|| itemStack.getType().equals(Material.DIAMOND_SHOVEL))
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

	private static ItemMeta applyAugmentationTextToItemStack(ItemStack targetItemStack,
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

	public static ItemStack applyAugmentation(ISoliniaItem soliniaItem, ItemStack itemStack, Integer augmentationItemId) {
		itemStack.setItemMeta(ItemStackUtils.applyAugmentationTextToItemStack(itemStack,augmentationItemId));
		
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.set("soliniaaug1id", new NBTTagString(Integer.toString(augmentationItemId)));
		nmsStack.setTag(compound);
		itemStack = CraftItemStack.asBukkitCopy(nmsStack);
		return itemStack;
	}
	
	public static ItemStack[] itemStackArrayFromYamlString(String yamlString)
	{
		YamlConfiguration config = new YamlConfiguration();
		try {
            config.loadFromString(yamlString);
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
		
		
		ArrayList<ItemStack> content = (ArrayList<ItemStack>) config.getList("serialized-item-stack-array");
		if (content == null)
			return new ItemStack[0];
		
		ItemStack[] items = new ItemStack[content.size()];
		for (int i = 0; i < content.size(); i++) {
		    ItemStack item = content.get(i);
		    if (item != null) {
		        items[i] = item;
		    } else {
		        items[i] = null;
		    }
		}
		
        return items;
	}
	
	public static String itemStackArrayToYamlString(ItemStack[] itemStackArray)
	{
		YamlConfiguration config = new YamlConfiguration();
        config.set("serialized-item-stack-array", itemStackArray);
        return config.saveToString();
	}
	
	public static String itemStackToYamlString(ItemStack itemStack)
	{
		YamlConfiguration config = new YamlConfiguration();
        config.set("serialized-item-stack", itemStack);
        return config.saveToString();
	}

	public static ItemStack itemStackFromYamlString(String yamlString)
	{
		YamlConfiguration config = new YamlConfiguration();
		try {
            config.loadFromString(yamlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("serialized-item-stack", null);
	}
	
	public static boolean isItemStackUptoDate(ItemStack item, ISoliniaItem solitem) {
		if (!Utils.IsSoliniaItem(item))
			return true;
		
		Timestamp itemStackTimestamp = Utils.GetSolLastUpdated(item);
		if (itemStackTimestamp == null)
		{
			Utils.DebugLog("ItemStackUtils","isItemStackUptoDate",String.valueOf(solitem.getId()),"ItemStack was null so returning false");
			return false;
		}
		
		Timestamp latesttimestamp = solitem.getLastUpdatedTime();
		if (latesttimestamp != null) {
			if (itemStackTimestamp.before(latesttimestamp))
			{
				String solUp = "";
				String stackUp = "";
				if (latesttimestamp != null)
				{
					solUp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(latesttimestamp);
				}
				stackUp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(itemStackTimestamp);
				
				Utils.DebugLog("ItemStackUtils","isItemStackUptoDate",String.valueOf(solitem.getId()),"ItemStack [" + stackUp  + "] was before [" + solUp + "]");
				return false;
			}
		}
		
		return true;
	}
}
