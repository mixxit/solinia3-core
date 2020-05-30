package com.solinia.solinia.Adapters;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.AugmentationSlotType;
import com.solinia.solinia.Models.EquipmentSlot;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Utils.ColorUtil;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.SkillUtils;
import com.solinia.solinia.Utils.SpellUtils;
import com.solinia.solinia.Utils.TextUtils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagInt;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagString;

public class ItemStackAdapter {
	public static ItemStack Adapt(ISoliniaItem soliniaItem, long costmultiplier, boolean merchanttag) {
		
		ItemStack stack = new ItemStack(Material.valueOf(soliniaItem.getBasename().toUpperCase()), 1, soliniaItem.getColor());
		
		Timestamp lastItemTimestamp = soliniaItem.getLastUpdatedTime();
		
		// New Item ID storage system
		NamespacedKey soliniaIdKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "soliniaid");
		NamespacedKey merchantKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "merchant");
		NamespacedKey soliniaLastUpdatedKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "sollastupdated");
		NamespacedKey soliniaAppearanceIdKey = new NamespacedKey(StateManager.getInstance().getPlugin(), "appearanceid");
		
		ItemMeta itemMeta = stack.getItemMeta();
		itemMeta.getCustomTagContainer().setCustomTag(merchantKey, ItemTagType.STRING, Boolean.toString(merchanttag));
		itemMeta.getCustomTagContainer().setCustomTag(soliniaIdKey, ItemTagType.INTEGER, soliniaItem.getId());
		if (lastItemTimestamp != null)
			itemMeta.getCustomTagContainer().setCustomTag(soliniaLastUpdatedKey, ItemTagType.LONG, lastItemTimestamp.getTime());
		if (soliniaItem.getAppearanceId() > 0)
			itemMeta.getCustomTagContainer().setCustomTag(soliniaAppearanceIdKey, ItemTagType.INTEGER, soliniaItem.getAppearanceId());
		
		stack.setItemMeta(itemMeta);
		
		net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		nmsStack.setTag(compound);
		stack = CraftItemStack.asBukkitCopy(nmsStack);

		if (soliniaItem.getItemWeaponDamage(false, stack) > 0) {
			if (soliniaItem.isMeleeWeapon()) {
				nmsStack = CraftItemStack.asNMSCopy(stack);

				compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				NBTTagList modifiers = new NBTTagList();
				NBTTagCompound damagecompound = new NBTTagCompound();
				damagecompound.set("AttributeName", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Name", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Amount", new NBTTagInt(soliniaItem.getItemWeaponDamage(false, null)));
				damagecompound.set("Operation", new NBTTagInt(0));
				damagecompound.set("UUIDLeast", new NBTTagInt(894654));
				damagecompound.set("UUIDMost", new NBTTagInt(2872));
				damagecompound.set("Slot", new NBTTagString("mainhand"));

				modifiers.add(damagecompound);
				compound.set("AttributeModifiers", modifiers);
				nmsStack.setTag(compound);
				stack = CraftItemStack.asBukkitCopy(nmsStack);
			}
		}

		ItemMeta i = stack.getItemMeta();
		if (soliniaItem.getBasename().equals("POTION") || soliniaItem.getBasename().equals("SPLASH_POTION")
				|| soliniaItem.getBasename().equals("LINGERING_POTION")) {
			i = (PotionMeta) stack.getItemMeta();
			PotionData data = new PotionData(PotionType.INSTANT_HEAL);
			((PotionMeta) i).setBasePotionData(data);
		}

		if (soliniaItem.getTexturebase64() != null && !soliniaItem.getTexturebase64().equals("")
				&& soliniaItem.isSkullItem()) {
			UUID skinuuid = getUUIDFromString(soliniaItem.getTexturebase64());
			i = buildSkull((SkullMeta) i, skinuuid, soliniaItem.getTexturebase64(), null);
		}

		i.setUnbreakable(true);
		i.setDisplayName(soliniaItem.getDisplayname());
		
		if (soliniaItem.getBasename().equals("WRITTEN_BOOK"))
		{
			((BookMeta) i).setAuthor("Unknown Author");
			((BookMeta) i).setTitle(soliniaItem.getDisplayname());
			((BookMeta) i).setPages(new ArrayList<String>());
			
			if (!soliniaItem.getBookAuthor().equals(""))			
				((BookMeta) i).setAuthor(soliniaItem.getBookAuthor());
			if (!soliniaItem.getDisplayname().equals(""))			
				((BookMeta) i).setTitle(soliniaItem.getDisplayname());
			if (soliniaItem.getBookPages().size() > 0)			
				((BookMeta) i).setPages(soliniaItem.getBookPages());
		}
		
		List<String> loretxt = new ArrayList<String>();

		if (soliniaItem.getLore() != null) {
			String[] lorestr = soliniaItem.getLore().split("(?<=\\G.{34})");

			loretxt.addAll(Arrays.asList(lorestr));
		}
		
		if (soliniaItem.getItemWeaponDamage(false, null) > 0) {
			loretxt.add("Damage: " + ChatColor.GREEN + soliniaItem.getItemWeaponDamage(false, null) + ChatColor.RESET);
			if (soliniaItem.getWeaponDelay() > 0) {
				loretxt.add("WeaponDelay: " + ChatColor.GREEN + soliniaItem.getWeaponDelay() + ChatColor.RESET);
			}
		}

		loretxt.add("ItemType: " + ChatColor.GREEN + soliniaItem.getItemType().name().toUpperCase() + ChatColor.RESET);
		
		if (soliniaItem.isThrowing() == true && !soliniaItem.isSpellscroll())
	    {
			loretxt.add("This item can be thrown!");
	    }

		if (soliniaItem.isArtifact() == true) {
			loretxt.add(ChatColor.GREEN + "This item is a unique artifact!" + ChatColor.RESET);
		}
		
		if (soliniaItem.getQuestId() > 0) {
			try
			{
				ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(soliniaItem.getQuestId());
				if (quest != null)
					loretxt.add(ChatColor.YELLOW + "Quest: " + quest.getName());
			
			} catch (CoreStateInitException e)
			{
				
			}
		}

		if (soliniaItem.isCrafting() == true) {
			loretxt.add(ChatColor.GOLD + "This looks like it could be crafted" + ChatColor.RESET);
			loretxt.add(ChatColor.GOLD + "into something useful" + ChatColor.RESET);
		}

		if (soliniaItem.isExperienceBonus() == true) {
			loretxt.add(ChatColor.GOLD + "Grant XP Experience!" + ChatColor.RESET);
		}
		
		if (soliniaItem.isBandage())
		{
			loretxt.add(ChatColor.AQUA + "This item can be used with the bindwound ability" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Fingers))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : FINGERS" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Waist))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : WAIST" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Neck))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : NECK" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Shoulders))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : SHOULDERS" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Ears))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : EARS" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Forearms))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : FOREARMS" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Arms))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : ARMS" + ChatColor.RESET);
		}
		
		if (soliniaItem.getEquipmentSlot().equals(EquipmentSlot.Hands))
		{
			loretxt.add(ChatColor.AQUA + "/EQUIP : HANDS" + ChatColor.RESET);
		}
		
		if (soliniaItem.isAugmentation() == true) {
				loretxt.add(ChatColor.AQUA + "This looks like it could augment " + ChatColor.RESET);
				loretxt.add(ChatColor.AQUA + "weapon or armour" + ChatColor.RESET);
				loretxt.add(ChatColor.AQUA + "Augments Item Slots: " + soliniaItem.getAugmentationFitsSlotType().name() + ChatColor.RESET);
		}
		
		if (soliniaItem.getItemWeaponDamage(false, null) > 0) {
			if (soliniaItem.getBasename().equals("BOW") || soliniaItem.getBasename().equals("CROSSBOW")) {
				loretxt.add("Modifies Arrow Dmg: " + ChatColor.GREEN + soliniaItem.getItemWeaponDamage(false, null) + ChatColor.RESET);
			}
		}

		if (soliniaItem.getBaneUndead() > 0) {
			loretxt.add("Bane UNDEAD: " + ChatColor.GREEN + soliniaItem.getBaneUndead() + ChatColor.RESET);
		}

		if (soliniaItem.getAC() > 0) {
			loretxt.add("Armour Class: " + ChatColor.GREEN + soliniaItem.getAC() + ChatColor.RESET);
		}
		
		if (
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_HELMET") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_CHESTPLATE") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_LEGGINGS") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_BOOTS")
				)
		{
			try
			{
				if (soliniaItem.getLeatherRgbDecimal() > 0)
				{
					Color colorTmp = Color.fromRGB(soliniaItem.getLeatherRgbDecimal());
					loretxt.add("Leather Color (Closest): " + ColorUtil.fromRGB(colorTmp.getRed(),colorTmp.getGreen(), colorTmp.getBlue()) + "Dye Color" + ChatColor.RESET);
				}
		        
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		String classtxt = "";
		if (soliniaItem.getAllowedClassNamesUpper().size() > 0) {
			classtxt = "Classes: ";
			for (String classname : soliniaItem.getAllowedClassNamesUpper()) {
				classtxt += ChatColor.YELLOW + classname + ChatColor.RESET + " ";
			}
		}
		
		if (!classtxt.equals("")) {
			loretxt.add(classtxt);
		}
		
		String racetxt = "";
		if (soliniaItem.getAllowedRaceNamesUpper().size() > 0) {
			racetxt = "Races: ";
			for (String racename : soliniaItem.getAllowedRaceNamesUpper()) {
				racetxt += ChatColor.YELLOW + racename + ChatColor.RESET + " ";
			}
		}
		
		if (!racetxt.equals("")) {
			loretxt.add(racetxt);
		}

		
		if (soliniaItem.getMinLevel() > 0) {
			loretxt.add("Minimum Level: " + ChatColor.YELLOW + soliniaItem.getMinLevel() + ChatColor.RESET);
		}

		String stattxt = "";
		if (soliniaItem.getStrength() > 0) {
			stattxt = "STR: " + ChatColor.GREEN + soliniaItem.getStrength() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getStamina() > 0) {
			stattxt += "STA: " + ChatColor.GREEN + soliniaItem.getStamina() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getAgility() > 0) {
			stattxt += "AGI: " + ChatColor.GREEN + soliniaItem.getAgility() + ChatColor.RESET + " ";
		}

		if (!stattxt.equals("")) {
			loretxt.add(stattxt);
		}

		stattxt = "";

		if (soliniaItem.getDexterity() > 0) {
			stattxt = "DEX: " + ChatColor.GREEN + soliniaItem.getDexterity() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getIntelligence() > 0) {
			stattxt += "INT: " + ChatColor.GREEN + soliniaItem.getIntelligence() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getWisdom() > 0) {
			stattxt += "WIS: " + ChatColor.GREEN + soliniaItem.getWisdom() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getCharisma() > 0) {
			stattxt += "CHA: " + ChatColor.GREEN + soliniaItem.getCharisma() + ChatColor.RESET + " ";
		}

		if (!stattxt.equals("")) {
			loretxt.add(stattxt);
		}

		String resisttxt = "";

		if (soliniaItem.getFireResist() > 0) {
			resisttxt += "FR: " + ChatColor.AQUA + soliniaItem.getFireResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getColdResist() > 0) {
			resisttxt += "CR: " + ChatColor.AQUA + soliniaItem.getColdResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getMagicResist() > 0) {
			resisttxt += "MR: " + ChatColor.AQUA + soliniaItem.getMagicResist() + ChatColor.RESET + " ";
		}
		if (soliniaItem.getDiseaseResist() > 0) {
			resisttxt += "DR: " + ChatColor.AQUA + soliniaItem.getDiseaseResist() + ChatColor.RESET + " ";
		}

		if (soliniaItem.getPoisonResist() > 0) {
			resisttxt += "PR: " + ChatColor.AQUA + soliniaItem.getPoisonResist() + ChatColor.RESET + " ";
		}

		if (!resisttxt.equals("")) {
			loretxt.add(resisttxt);
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
		
		if (soliniaItem.getAttackspeed() > 0)
		{
			loretxt.add(ChatColor.WHITE + "Modifies Melee Attack Speed % by: " + ChatColor.YELLOW + soliniaItem.getAttackspeed() + ChatColor.RESET);
		}
		
		if (!(soliniaItem.getSkillModType().equals(SkillType.None)))
		{
			loretxt.add(ChatColor.WHITE + "Modifies skill checks for: " + ChatColor.YELLOW + soliniaItem.getSkillModType().toString() + "  +(" + soliniaItem.getSkillModValue() + ")" + ChatColor.RESET);
		}

		if (!(soliniaItem.getSkillModType2().equals(SkillType.None)))
		{
			loretxt.add(ChatColor.WHITE + "Modifies skill checks for: " + ChatColor.YELLOW + soliniaItem.getSkillModType2().toString() + "  +(" + soliniaItem.getSkillModValue2() + ")" + ChatColor.RESET);
		}

		if (!(soliniaItem.getSkillModType3().equals(SkillType.None)))
		{
			loretxt.add(ChatColor.WHITE + "Modifies skill checks for: " + ChatColor.YELLOW + soliniaItem.getSkillModType3().toString() + "  +(" + soliniaItem.getSkillModValue3() + ")" + ChatColor.RESET);
		}

		if (!(soliniaItem.getSkillModType4().equals(SkillType.None)))
		{
			loretxt.add(ChatColor.WHITE + "Modifies skill checks for: " + ChatColor.YELLOW + soliniaItem.getSkillModType4().toString() + "  +(" + soliniaItem.getSkillModValue4() + ")" + ChatColor.RESET);
		}

		
		if (!(soliniaItem.getAcceptsAugmentationSlotType().equals(AugmentationSlotType.NONE)))
		{
			loretxt.add(ChatColor.WHITE + "Augmentation Slot Types: " + ChatColor.YELLOW + soliniaItem.getAcceptsAugmentationSlotType().name() + ChatColor.RESET);
		}

		if (soliniaItem.getAttack() > 0) {
			resisttxt += "Attack: " + ChatColor.AQUA + soliniaItem.getAttack() + ChatColor.RESET + " ";
		}
		
		if (!regentxt.equals("")) {
			loretxt.add(regentxt);
		}
		
		String hpmanatxt = "";
		
		if (soliniaItem.getHp() > 0 || soliniaItem.getMana() > 0) {
			if (soliniaItem.getHp() > 0) {
				hpmanatxt = ChatColor.WHITE + "HP: " + ChatColor.YELLOW + soliniaItem.getHp()
						+ ChatColor.RESET;
			}

			if (soliniaItem.getMana() > 0) {
				if (!hpmanatxt.equals(""))
					hpmanatxt += " ";
				hpmanatxt += ChatColor.WHITE + "Mana: " + ChatColor.YELLOW + soliniaItem.getMana()
						+ ChatColor.RESET;
			}
		}
		
		if (!hpmanatxt.equals("")) {
			loretxt.add(hpmanatxt);
		}		
		
		if (soliniaItem.isSpellscroll())
		{
			loretxt.add("This item can be added to your /spellbook");
		}
		
		if (soliniaItem.getAbilityid() > 0 && soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateSpellLoreText(soliniaItem));
	    }
		
		if (soliniaItem.getFocusEffectId() > 0)
		{
			loretxt.addAll(generateFocusEffectLoreText(soliniaItem));
		}

		if (soliniaItem.getAbilityid() > 0 && !soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateItemSpellLoreText(soliniaItem));
	    }
		
		if (soliniaItem.getWeaponabilityid() > 0 && !soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateWeaponAbilityLoreText(soliniaItem));
	    }
		
		if (!soliniaItem.getLanguagePrimer().equals(""))
		{
			loretxt.add("Language Primer: " + soliniaItem.getLanguagePrimer());
		}
		
		if (soliniaItem.isDistiller())
		{
			loretxt.add("This is an Augmentation Distiller");
		}
		
		loretxt.add("Discovered By: " + soliniaItem.getDiscoverer());
		
		if ((soliniaItem.getWorth() * costmultiplier) > 0)
		{
			loretxt.add("Worth: " + (soliniaItem.getWorth() * costmultiplier));
		}

		if ((soliniaItem.getWorth() * costmultiplier) > 0)
		{
			loretxt.add("Inspiration Worth: " + (soliniaItem.getInspirationWorth()));
		}
		
		if (soliniaItem.isTemporary())
		{
			loretxt.add("Temporary: " + StateManager.getInstance().getInstanceGuid());
		}
		if (soliniaItem.isConsumable())
		{
			loretxt.add("This item is consumed on use");
		}

		i.setLore(loretxt);
		
		stack.setItemMeta(i);
		
		if (
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_HELMET") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_CHESTPLATE") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_LEGGINGS") ||
				soliniaItem.getBasename().toUpperCase().equals("LEATHER_BOOTS")
				)
		{
			try
			{
				LeatherArmorMeta lch = (LeatherArmorMeta)stack.getItemMeta();
				if (soliniaItem.getLeatherRgbDecimal() < 1)
			        lch.setColor(null);
				else
					lch.setColor(Color.fromRGB(soliniaItem.getLeatherRgbDecimal()));
		        stack.setItemMeta(lch);
		        
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}		
		// depcreated in favour of nbt string soliniaid
		//stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1000 + soliniaItem.getId());

		if (soliniaItem.getEnchantment1() != null) {
			if (soliniaItem.getEnchantment1val() > 0) {
				try {
					Enchantment enchantment = ItemStackUtils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment1());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment1val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment2() != null) {
			if (soliniaItem.getEnchantment2val() > 0) {
				try {
					Enchantment enchantment = ItemStackUtils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment2());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment2val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment3() != null) {
			if (soliniaItem.getEnchantment3val() > 0) {
				try {
					Enchantment enchantment = ItemStackUtils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment3());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment3val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment4() != null) {
			if (soliniaItem.getEnchantment4val() > 0) {
				try {
					Enchantment enchantment = ItemStackUtils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment4());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment4val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getTexturebase64() != null && !soliniaItem.getTexturebase64().equals("")
				&& soliniaItem.isSkullItem()) {
			stack.setDurability((short) 3);
		}

		return stack;
	}

	private static List<String> generateItemSpellLoreText(ISoliniaItem soliniaItem) {
		List<String> loreTxt = new ArrayList<String>();
		try {
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getAbilityid());
			if (spell != null)
			{
				loreTxt.add(ChatColor.WHITE + "Right Click Effect: " + ChatColor.YELLOW + spell.getName() + " " + ChatColor.RESET);
			} else {
				System.out.println("!! WARNING !! Item: " + soliniaItem.getId() + " has an invalid spell Id [" + soliniaItem.getAbilityid() + "]");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loreTxt;
	}
	
	private static List<String> generateFocusEffectLoreText(ISoliniaItem soliniaItem)
	{
		List<String> loreTxt = new ArrayList<String>();
		
		try {
			ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getFocusEffectId());
			
			loreTxt.add(ChatColor.WHITE + "Focus Effect: " + ChatColor.YELLOW+ spell.getName() + " " + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loreTxt;
	}

	private static Collection<String> generateWeaponAbilityLoreText(ISoliniaItem soliniaItem) {
		List<String> loreTxt = new ArrayList<String>();
		ISoliniaSpell spell;
		
		try {
			spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getWeaponabilityid());
			loreTxt.add(ChatColor.WHITE + "Chance to Proc on Hit: " + ChatColor.YELLOW+spell.getName() + ChatColor.RESET);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loreTxt;
	}

	private static List<String> generateSpellLoreText(ISoliniaItem soliniaItem) {
		List<String> loreTxt = new ArrayList<String>();
		ISoliniaSpell spell;
		try {
			spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getAbilityid());
			if (spell.getEffectid1() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid1()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid1()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue1() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid1()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue1() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid1()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid2() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid2()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid2()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue2() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid2()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue2() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid2()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid3() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid3()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid3()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue3() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid3()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue3() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid3()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid4() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid4()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid4()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue4() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid4()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue4() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid4()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid5() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid5()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid5()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue5() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid5()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue5() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid5()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid6() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid6()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid6()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue6() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid6()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue6() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid6()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid7() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid7()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid7()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue7() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid7()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue7() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid7()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid8() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid8()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid8()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue8() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid8()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue8() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid8()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid9() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid9()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid9()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue9() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid9()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue9() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid9()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid10() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid10()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid10()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue10() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid10()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue10() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid10()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid11() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid11()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid11()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue11() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid11()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue11() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid11()).name() + ChatColor.RESET);
			}
			if (spell.getEffectid12() != 254 && !SpellUtils.getSpellEffectType(spell.getEffectid12()).name().contains("LIMIT_") && !(SpellUtils.getSpellEffectType(spell.getEffectid12()).equals(SpellEffectType.CHA) && spell.getEffectBaseValue12() == 0) && !SpellUtils.getSpellEffectType(spell.getEffectid12()).name().contains("StackingCommand_"))
			{
				String pos = "+";
				if (spell.getEffectBaseValue12() < 0)
					pos = "-";
				loreTxt.add(ChatColor.WHITE + "Effect: " + ChatColor.YELLOW+pos+SpellUtils.getSpellEffectType(spell.getEffectid12()).name() + ChatColor.RESET);
			}
			
			if (!spell.isBardSong())
			{
				if (spell.getComponents1() > 0)
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents1());
					if (item != null && item.isReagent())
					{
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW+item.getDisplayname()+" x " + spell.getComponentCounts1() + ChatColor.RESET);
					} else {
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents1());
					}
				}
	
				if (spell.getComponents2() > 0)
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents2());
					if (item != null && item.isReagent())
					{
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW+item.getDisplayname()+" x " + spell.getComponentCounts2() + ChatColor.RESET);
					} else {
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents2());
					}
				}
	
				if (spell.getComponents3() > 0)
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents3());
					if (item != null && item.isReagent())
					{
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW+item.getDisplayname()+" x " + spell.getComponentCounts3() + ChatColor.RESET);
					} else {
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents3());
					}
				}
	
				if (spell.getComponents4() > 0)
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(spell.getComponents4());
					if (item != null && item.isReagent())
					{
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW+item.getDisplayname()+" x " + spell.getComponentCounts4() + ChatColor.RESET);
					} else {
						loreTxt.add(ChatColor.WHITE + "Requires Component: " + ChatColor.YELLOW + "ERROR-ALERT-ADMIN-SPELL"+ spell.getId() + "-ID" + spell.getComponents4());
					}
				}
			}

			loreTxt.add(ChatColor.WHITE + "Mana/Power: " + ChatColor.YELLOW+spell.getMana() + ChatColor.RESET);
			loreTxt.add(ChatColor.WHITE + "Spell Skill: " + ChatColor.YELLOW+SkillUtils.getSkillType(spell.getSkill()).name().toUpperCase() + ChatColor.RESET);
			loreTxt.add(ChatColor.WHITE + "Range: " + ChatColor.YELLOW+spell.getRange() + ChatColor.RESET);
			loreTxt.add(ChatColor.WHITE + "Casting Time: " + ChatColor.YELLOW+(spell.getCastTime()/1000) + " seconds" + ChatColor.RESET);
			
			if (spell.isAASpell())
			{
				loreTxt.add(ChatColor.WHITE + "This spell is an AA spell" + ChatColor.RESET);
			}
			
			if (spell.isBuffSpell() && spell.getBuffduration() > 0)
			{
				loreTxt.add(ChatColor.WHITE + "Buff Duration: " + ChatColor.YELLOW + (spell.getBuffduration() * 6) + " seconds" + ChatColor.RESET);
			}
			loreTxt.add(ChatColor.WHITE + "Target Type: " + ChatColor.YELLOW + SpellUtils.getSpellTargetType(spell.getTargettype()).name() + ChatColor.RESET);
			
			List<String> classInfo = generateClassesText(spell);
			
			if (classInfo.size() > 0)
				loreTxt.addAll(classInfo);
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return loreTxt;
	}

	private static List<String> generateClassesText(ISoliniaSpell spell) {
		String classesBuilder = "";
		List<SoliniaSpellClass> allowedSpellClasses = spell.getAllowedClasses();
		try
		{
			for (SoliniaSpellClass spellclass : allowedSpellClasses)
			{
				if (StateManager.getInstance().getConfigurationManager().getClassObj(spellclass.getClassname().toUpperCase()) == null)
					continue;
				
				classesBuilder += ChatColor.WHITE + spellclass.getClassname() + ":(" + ChatColor.YELLOW + spellclass.getMinlevel() + ChatColor.WHITE + ")" + ChatColor.RESET + " ";
			}
		} catch (CoreStateInitException e) {
		
		}
		
		// we make this 64 to allow for 'special' colour characters in the string
		return TextUtils.breakStringOfWordsIntoLines(classesBuilder,64);
	}

	public static ItemMeta buildSkull(SkullMeta meta, UUID skinuuid, String texturebase64, String player) {
		if (player != null) {
			meta.setOwner(player);
		} else if (texturebase64 != null) {
			GameProfile profile = new GameProfile(skinuuid, null);

			profile.getProperties().put("textures", new Property("textures", texturebase64));
			Field profileField = null;
			try {
				profileField = meta.getClass().getDeclaredField("profile");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			profileField.setAccessible(true);
			try {
				profileField.set(meta, profile);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return meta;
	}

	public static UUID getUUIDFromString(String s) {
		String md5 = getMD5(s);

		String uuid = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-"
				+ md5.substring(16, 20) + "-" + md5.substring(20);

		return UUID.fromString(uuid);
	}

	public static String getMD5(String input) {
		// TODO Auto-generated method stub
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
