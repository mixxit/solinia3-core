package com.solinia.solinia.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.AugmentationSlotType;
import com.solinia.solinia.Models.ItemType;
import com.solinia.solinia.Models.SkillReward;
import com.solinia.solinia.Models.SkillType;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.AttributeModifier;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.GameProfileSerializer;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class ItemStackUtils {

	public static boolean IsDisplayItem(ItemStack itemStack) {
		// Also check nbttag
		if (itemStack == null)
			return false;
		
		boolean isDisplayItem = itemStack.getItemMeta().getDisplayName().startsWith("Display Item: ");
		if (isDisplayItem)
			return isDisplayItem;

		// Classic method
		net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

		String isMerchant = compound.getString("merchant");
		return Boolean.parseBoolean(isMerchant);
	}

	
	public static AugmentationSlotType getItemStackAugSlotType(String basename, boolean isAugmentation) {
		if (isAugmentation)
			return AugmentationSlotType.NONE;

		switch (basename.toUpperCase()) {
		case "TRIDENT":
			return AugmentationSlotType.WEAPON;
		case "BOW":
			return AugmentationSlotType.WEAPON;
		case "CROSSBOW":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_SWORD":
			return AugmentationSlotType.WEAPON;
		case "WOOD_SWORD":
			return AugmentationSlotType.WEAPON;
		case "STONE_SWORD":
			return AugmentationSlotType.WEAPON;
		case "IRON_SWORD":
			return AugmentationSlotType.WEAPON;
		case "GOLD_SWORD":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_SWORD":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_SWORD":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_AXE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_AXE":
			return AugmentationSlotType.WEAPON;
		case "STONE_AXE":
			return AugmentationSlotType.WEAPON;
		case "IRON_AXE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_AXE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_AXE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_AXE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_SPADE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_SPADE":
			return AugmentationSlotType.WEAPON;
		case "STONE_SPADE":
			return AugmentationSlotType.WEAPON;
		case "IRON_SPADE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_SPADE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_SPADE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_SPADE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_HOE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_HOE":
			return AugmentationSlotType.WEAPON;
		case "STONE_HOE":
			return AugmentationSlotType.WEAPON;
		case "IRON_HOE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_HOE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_HOE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_HOE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "STONE_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "IRON_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "LEATHER_HELMET":
			return AugmentationSlotType.HELMET;
		case "LEATHER_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "LEATHER_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "LEATHER_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "CHAINMAIL_HELMET":
			return AugmentationSlotType.HELMET;
		case "CHAINMAIL_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "CHAINMAIL_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "CHAINMAIL_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "IRON_HELMET":
			return AugmentationSlotType.HELMET;
		case "IRON_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "IRON_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "IRON_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "DIAMOND_HELMET":
			return AugmentationSlotType.HELMET;
		case "DIAMOND_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "DIAMOND_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "DIAMOND_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "GOLD_HELMET":
			return AugmentationSlotType.HELMET;
		case "GOLD_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "GOLD_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "GOLD_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "GOLDEN_HELMET":
			return AugmentationSlotType.HELMET;
		case "GOLDEN_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "GOLDEN_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "GOLDEN_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "SHIELD":
			return AugmentationSlotType.SHIELD;
		default:
			return AugmentationSlotType.NONE;
		}
	}


	public static ItemStack getTargetingItemStack() {
		ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
		itemStack.setItemMeta(ItemStackAdapter.buildSkull((SkullMeta) itemStack.getItemMeta(),
				UUID.fromString("9c3bb224-bc6e-4da8-8b15-a35c97bc3b16"),
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlNDI1YzViYTlmM2MyOTYyYjM4MTc4Y2JjMjMxNzJhNmM2MjE1YTExYWNjYjkyNzc0YTQ3MTZlOTZjYWRhIn19fQ==",
				null));
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("This is a targetting tool");
		lore.add("Right click on an entity with this");
		lore.add("Left click to target self");
		lore.add("To clear, right click on nothing");

		itemMeta.setLore(lore);
		itemStack.setDurability((short) 3);
		itemMeta.setDisplayName("Targetting Tool");
		itemStack.setItemMeta(itemMeta);
		itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 997);
		return itemStack;
	}
	public static String getDefaultSkillForMaterial(Material material) {
		String materialstring = material.name().toUpperCase();
		
		switch (materialstring) {
		case "WOODEN_SWORD":
			return "SLASHING";
		case "STONE_SWORD":
			return "SLASHING";
		case "IRON_SWORD":
			return "SLASHING";
		case "GOLDEN_SWORD":
			return "SLASHING";
		case "DIAMOND_SWORD":
			return "SLASHING";
		case "WOODEN_AXE":
			return "SLASHING";
		case "STONE_AXE":
			return "SLASHING";
		case "IRON_AXE":
			return "SLASHING";
		case "GOLDEN_AXE":
			return "SLASHING";
		case "DIAMOND_AXE":
			return "SLASHING";
		case "AIR":
			return "CRUSHING";
		case "STICK":
			return "CRUSHING";
		case "WOODEN_SHOVEL":
			return "CRUSHING";
		case "STONE_SHOVEL":
			return "CRUSHING";
		case "IRON_SHOVEL":
			return "CRUSHING";
		case "GOLDEN_SHOVEL":
			return "CRUSHING";
		case "DIAMOND_SHOVEL":
			return "CRUSHING";
		case "WOODEN_HOE":
			return "CRUSHING";
		case "STONE_HOE":
			return "CRUSHING";
		case "IRON_HOE":
			return "CRUSHING";
		case "GOLDEN_HOE":
			return "CRUSHING";
		case "DIAMOND_HOE":
			return "CRUSHING";
		case "WOODEN_PICKAXE":
			return "CRUSHING";
		case "STONE_PICKAXE":
			return "CRUSHING";
		case "IRON_PICKAXE":
			return "CRUSHING";
		case "GOLDEN_PICKAXE":
			return "CRUSHING";
		case "DIAMOND_PICKAXE":
			return "CRUSHING";
		case "BOW":
			return "ARCHERY";
		case "CROSSBOW":
			return "ARCHERY";
		default:
			return "CRUSHING";
		}
	}


	public static Enchantment getEnchantmentFromEnchantmentName(String name) throws Exception {
		switch (name) {
		case "ARROW_DAMAGE":
			return Enchantment.ARROW_DAMAGE;
		case "ARROW_FIRE":
			return Enchantment.ARROW_FIRE;
		case "ARROW_INFINITE":
			return Enchantment.ARROW_INFINITE;
		case "ARROW_KNOCKBACK":
			return Enchantment.ARROW_KNOCKBACK;
		case "DAMAGE_ALL":
			return Enchantment.DAMAGE_ALL;
		case "DAMAGE_ARTHROPODS":
			return Enchantment.DAMAGE_ARTHROPODS;
		case "DAMAGE_UNDEAD":
			return Enchantment.DAMAGE_UNDEAD;
		case "DEPTH_STRIDER":
			return Enchantment.DEPTH_STRIDER;
		case "DIG_SPEED":
			return Enchantment.DIG_SPEED;
		case "DURABILITY":
			return Enchantment.DURABILITY;
		case "FIRE_ASPECT":
			return Enchantment.FIRE_ASPECT;
		case "FROST_WALKER":
			return Enchantment.FROST_WALKER;
		case "KNOCKBACK":
			return Enchantment.KNOCKBACK;
		case "LOOT_BONUS_BLOCKS":
			return Enchantment.LOOT_BONUS_BLOCKS;
		case "LOOT_BONUS_MOBS":
			return Enchantment.LOOT_BONUS_MOBS;
		case "LUCK":
			return Enchantment.LUCK;
		case "LURE":
			return Enchantment.LURE;
		case "MENDING":
			return Enchantment.MENDING;
		case "PROTECTION_ENVIRONMENTAL":
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		case "PROTECTION_EXPLOSIONS":
			return Enchantment.PROTECTION_EXPLOSIONS;
		case "PROTECTION_FALL":
			return Enchantment.PROTECTION_FALL;
		case "PROTECTION_FIRE":
			return Enchantment.PROTECTION_FIRE;
		case "PROTECTION_PROJECTILE":
			return Enchantment.PROTECTION_PROJECTILE;
		case "SILK_TOUCH":
			return Enchantment.SILK_TOUCH;
		case "THORNS":
			return Enchantment.THORNS;
		case "WATER_WORKER":
			return Enchantment.WATER_WORKER;
		default:
			throw new Exception("Unsupported enchantment type for SoliniaItem");
		}
	}
	
	public static List<Material> getAllowedVanillaItemStacks()
	{
		List<Material> allowedVanillaItems = new ArrayList<Material>();
		allowedVanillaItems.add(Material.COAL_ORE);
		allowedVanillaItems.add(Material.GOLD_ORE);
		allowedVanillaItems.add(Material.GOLD_INGOT);
		allowedVanillaItems.add(Material.GOLD_BLOCK);
		allowedVanillaItems.add(Material.IRON_INGOT);
		allowedVanillaItems.add(Material.IRON_ORE);
		allowedVanillaItems.add(Material.IRON_BLOCK);
		allowedVanillaItems.add(Material.DIAMOND_ORE);
		allowedVanillaItems.add(Material.DIAMOND);
		allowedVanillaItems.add(Material.DIAMOND_BLOCK);
		allowedVanillaItems.add(Material.LAPIS_ORE);
		allowedVanillaItems.add(Material.LAPIS_BLOCK);
		allowedVanillaItems.add(Material.REDSTONE_ORE);
		allowedVanillaItems.add(Material.REDSTONE);
		allowedVanillaItems.add(Material.REDSTONE_BLOCK);
		return allowedVanillaItems;
	}
	
	public static int getWeaponDamageFromItemStack(ItemStack itemStack, EnumItemSlot itemSlot) {
        double attackDamage = 1.0;
        UUID uuid = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        net.minecraft.server.v1_14_R1.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_14_R1.Item item = craftItemStack.getItem();
        if(item instanceof net.minecraft.server.v1_14_R1.ItemSword || item instanceof net.minecraft.server.v1_14_R1.ItemTool || item instanceof net.minecraft.server.v1_14_R1.ItemHoe) {
            Multimap<String, AttributeModifier> map = item.a(itemSlot);
            Collection<AttributeModifier> attributes = map.get(GenericAttributes.ATTACK_DAMAGE.getName());
            if(!attributes.isEmpty()) {
                for(AttributeModifier am: attributes) {
                    if(am.getUniqueId().toString().equalsIgnoreCase(uuid.toString()) && am.getOperation() == AttributeModifier.Operation.ADDITION) attackDamage += am.getAmount();
                }
                double y = 1;
                for(AttributeModifier am: attributes) {
                    if(am.getUniqueId().toString().equalsIgnoreCase(uuid.toString()) && am.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) y += am.getAmount();
                }
                attackDamage *= y;
                for(AttributeModifier am: attributes) {
                    if(am.getUniqueId().toString().equalsIgnoreCase(uuid.toString()) && am.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) attackDamage *= (1 + am.getAmount());
                }
            }
        }
        
        Long rounded = Math.round(attackDamage);
        
        if (rounded > Integer.MAX_VALUE)
        	rounded = (long)Integer.MAX_VALUE;
        
        int damage = Integer.valueOf(rounded.intValue());
        
        return damage;
    }
	
	public static SkillReward getMeleeSkillForItemStack(ItemStack itemStack) {
		SkillReward reward = null;
		ItemType type = ItemType.None;
		try
		{
			ISoliniaItem item = SoliniaItemAdapter.Adapt(itemStack);
			if (item != null)
			{
				type = item.getItemType();				
			}
		} catch (CoreStateInitException e)
		{
			
		} catch (SoliniaItemException e) {
			
		}

		int xp = 0;
		SkillType skillType = SkillType.HandtoHand;

		switch (type) {
			case OneHandSlashing:
				xp = 1;
				skillType = SkillType.Slashing;
				break;
			case TwoHandSlashing:
				xp = 1;
				skillType = SkillType.TwoHandSlashing;
				break;
			case OneHandBlunt:
				xp = 1;
				skillType = SkillType.Crushing;
				break;
			case TwoHandBlunt:
				xp = 1;
				skillType = SkillType.TwoHandBlunt;
				break;
			case OneHandPiercing:
				xp = 1;
				skillType = SkillType.Piercing;
				break;
			case TwoHandPiercing:
				xp = 1;
				skillType = SkillType.TwoHandPiercing;
				break;
			case BowArchery:
				xp = 1;
				skillType = SkillType.Archery;
				break;
			default:
				xp = 1;
				skillType = SkillType.HandtoHand;
			break;
		
		}

		if (xp > 0 && !skillType.equals(SkillType.None)) {
			reward = new SkillReward(skillType, xp);
		}

		return reward;
	}
	
	public static Integer getSoliniaItemId(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
			return null;
		
		NamespacedKey soliniaIdKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "soliniaid");
		if(itemMeta.getCustomTagContainer().hasCustomTag(soliniaIdKey , ItemTagType.INTEGER)) {
		    return itemMeta.getCustomTagContainer().getCustomTag(soliniaIdKey, ItemTagType.INTEGER);
		}
		// NPCS store it as a string
		if(itemMeta.getCustomTagContainer().hasCustomTag(soliniaIdKey , ItemTagType.STRING)) {
		    return Integer.parseInt(itemMeta.getCustomTagContainer().getCustomTag(soliniaIdKey, ItemTagType.STRING));
		}
		
		return null;
	}
	
	public static Long getSoliniaLastUpdated(ItemStack itemStack)
	{
		if (itemStack == null)
			return null;

		if (itemStack.getItemMeta() == null)
			return null;
		
		NamespacedKey soliniaLastUpdatedKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "sollastupdated");
		ItemMeta itemMeta = itemStack.getItemMeta();
		CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
		// old system
		if(tagContainer.hasCustomTag(soliniaLastUpdatedKey , ItemTagType.STRING)) {
			itemMeta.getCustomTagContainer().setCustomTag(soliniaLastUpdatedKey, ItemTagType.LONG, Long.parseLong(tagContainer.getCustomTag(soliniaLastUpdatedKey, ItemTagType.STRING)));
			itemStack.setItemMeta(itemMeta);
		}		
		
		if(tagContainer.hasCustomTag(soliniaLastUpdatedKey , ItemTagType.LONG)) {
		    return tagContainer.getCustomTag(soliniaLastUpdatedKey, ItemTagType.LONG);
		}
		
		return null;
	}
	
	public static String ConvertItemStackToJsonRegular(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_14_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        compound = nmsItemStack.save(compound);

        return compound.toString();
    }
	
	public static boolean IsSoliniaItem(ItemStack itemStack) {
		if (itemStack == null)
			return false;

		// New method
		if (ItemStackUtils.getSoliniaItemId(itemStack) != null) {
			return true;
		}
		
		return false;
	}
	
	public static Integer getAugmentationItemId(ItemStack itemStack)
	{
		if (!ItemStackUtils.IsSoliniaItem(itemStack))
			return null;
		
		NamespacedKey soliniaAugIdKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "soliniaaug1id");
		ItemMeta itemMeta = itemStack.getItemMeta();
		CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
		
		if(tagContainer.hasCustomTag(soliniaAugIdKey , ItemTagType.INTEGER)) {
			return itemMeta.getCustomTagContainer().getCustomTag(soliniaAugIdKey, ItemTagType.INTEGER);
		}
		
		return null;
	}
	
	public static boolean isSkullItem(ItemStack itemStack) {
		if (itemStack.getType().name().equals("SKULL_ITEM") || itemStack.getType().name().equals("PLAYER_HEAD")
				|| itemStack.getType().name().equals("LEGACY_SKULL_ITEM"))
			return true;

		return false;
	}
	
	public static String getSkullTexture(ItemStack itemStack)
	{
		String textureValue = "";
		if (ItemStackUtils.isSkullItem(itemStack))
	    {
			net.minecraft.server.v1_14_R1.ItemStack rawItemStack = CraftItemStack.asNMSCopy(itemStack);
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
	
	public static Double getMerchantItemWorth(ItemStack itemStack)
	{
		if (!ItemStackUtils.IsSoliniaItem(itemStack))
			return null;
		
		for(String loreLine : itemStack.getItemMeta().getLore())
		{
			if (!loreLine.startsWith("Worth: "))
				continue;
			
			String[] temporaryData = loreLine.split(" ");
			
			return Double.parseDouble(temporaryData[1]);
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
		
		ItemMeta itemMeta = itemStack.getItemMeta();
		NamespacedKey soliniaAugIdKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "soliniaaug1id");
		itemMeta.getCustomTagContainer().setCustomTag(soliniaAugIdKey, ItemTagType.INTEGER, augmentationItemId);
		itemStack.setItemMeta(itemMeta);
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
	
	public static Timestamp GetSolLastUpdated(ItemStack itemStack) {

		Long solupdatedtime = ItemStackUtils.getSoliniaLastUpdated(itemStack);
		if (solupdatedtime == null)
			return null;

		try {
			Timestamp timestamp = new java.sql.Timestamp(solupdatedtime);
			return timestamp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isItemStackUptoDate(ItemStack item, ISoliniaItem solitem) {
		if (!ItemStackUtils.IsSoliniaItem(item))
			return true;
		
		Timestamp itemStackTimestamp = ItemStackUtils.GetSolLastUpdated(item);
		if (itemStackTimestamp == null)
		{
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
				
				return false;
			}
		}
		
		return true;
	}

	public static boolean isRangedWeapon(ItemStack item) {
		if (item.getType().name().toUpperCase().equals("BOW"))
			return true;
		if (item.getType().name().toUpperCase().equals("CROSSBOW"))
			return true;
		
		return false;
	}
}
