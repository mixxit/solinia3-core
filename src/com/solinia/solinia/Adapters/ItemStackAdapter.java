package com.solinia.solinia.Adapters;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class ItemStackAdapter {
	public static ItemStack Adapt(ISoliniaItem soliniaItem) {
		
		ItemStack stack = new ItemStack(Material.valueOf(soliniaItem.getBasename().toUpperCase()), 1, soliniaItem.getColor());

		if (soliniaItem.getDamage() > 0) {
			if (soliniaItem.getBasename().equals("WOOD_SWORD") || soliniaItem.getBasename().equals("STONE_SWORD")
					|| soliniaItem.getBasename().equals("IRON_SWORD") || soliniaItem.getBasename().equals("GOLD_SWORD")
					|| soliniaItem.getBasename().equals("DIAMOND_SWORD") || soliniaItem.getBasename().equals("WOOD_AXE")
					|| soliniaItem.getBasename().equals("STONE_AXE") || soliniaItem.getBasename().equals("IRON_AXE")
					|| soliniaItem.getBasename().equals("GOLD_AXE") || soliniaItem.getBasename().equals("DIAMOND_AXE")
					|| soliniaItem.getBasename().equals("WOOD_SPADE") || soliniaItem.getBasename().equals("STONE_SPADE")
					|| soliniaItem.getBasename().equals("IRON_SPADE") || soliniaItem.getBasename().equals("GOLD_SPADE")
					|| soliniaItem.getBasename().equals("DIAMOND_SPADE")) {
				net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

				NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				NBTTagList modifiers = new NBTTagList();
				NBTTagCompound damagecompound = new NBTTagCompound();
				damagecompound.set("AttributeName", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Name", new NBTTagString("generic.attackDamage"));
				damagecompound.set("Amount", new NBTTagInt(soliniaItem.getDamage()));
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
				&& soliniaItem.getBasename().equals("SKULL_ITEM")) {
			UUID skinuuid = getUUIDFromString(soliniaItem.getTexturebase64());
			i = buildSkull((SkullMeta) i, skinuuid, soliniaItem.getTexturebase64(), null);
		}

		i.setUnbreakable(true);
		i.setDisplayName(soliniaItem.getDisplayname());
		List<String> loretxt = new ArrayList<String>();

		if (soliniaItem.getLore() != null) {
			String[] lorestr = soliniaItem.getLore().split("(?<=\\G.{34})");

			loretxt.addAll(Arrays.asList(lorestr));
		}

		if (soliniaItem.getDamage() > 0) {
			if (soliniaItem.getBasename().equals("BOW")) {
				loretxt.add("Modifies Arrow Dmg: " + ChatColor.GREEN + soliniaItem.getDamage() + ChatColor.RESET);
			}
		}

		String classtxt = "";
		if (soliniaItem.getAllowedClassNames().size() > 0) {
			classtxt = "Usable By: ";
			for (String classname : soliniaItem.getAllowedClassNames()) {
				classtxt += ChatColor.YELLOW + classname + ChatColor.RESET + " ";
			}
		}

		if (!classtxt.equals("")) {
			loretxt.add(classtxt);
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

		if (!regentxt.equals("")) {
			loretxt.add(regentxt);
		}
		
		
		if (soliniaItem.getAbilityid() > 0 && soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateSpellLoreText(soliniaItem));
	    }
		
		if (soliniaItem.getAbilityid() > 0 && !soliniaItem.isSpellscroll())
	    {
			loretxt.addAll(generateConsumableAbilityLoreText(soliniaItem));
	    }
		
		if (soliniaItem.getWorth() > 0)
		{
			loretxt.add(ChatColor.WHITE + "Worth: " + ChatColor.YELLOW + soliniaItem.getWorth()
			+ ChatColor.RESET);
		}
		

		i.setLore(loretxt);
		stack.setItemMeta(i);
		stack.addUnsafeEnchantment(Enchantment.OXYGEN, 1000 + soliniaItem.getId());

		if (soliniaItem.getEnchantment1() != null) {
			if (soliniaItem.getEnchantment1val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment1());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment1val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment2() != null) {
			if (soliniaItem.getEnchantment2val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment2());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment2val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment3() != null) {
			if (soliniaItem.getEnchantment3val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment3());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment3val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getEnchantment4() != null) {
			if (soliniaItem.getEnchantment4val() > 0) {
				try {
					Enchantment enchantment = Utils.getEnchantmentFromEnchantmentName(soliniaItem.getEnchantment4());
					stack.addUnsafeEnchantment(enchantment, soliniaItem.getEnchantment4val());
				} catch (Exception e) {
					System.out.println("WARNING: Invalid Enchantment Item on SoliniaItem: " + soliniaItem.getId());
				}
			}
		}

		if (soliniaItem.getTexturebase64() != null && !soliniaItem.getTexturebase64().equals("")
				&& soliniaItem.getBasename().equals("SKULL_ITEM")) {
			stack.setDurability((short) 3);
		}

		return stack;
	}

	private static Collection<String> generateConsumableAbilityLoreText(ISoliniaItem soliniaItem) {
		List<String> loreTxt = new ArrayList<String>();
		ISoliniaSpell spell;
		
		try {
			spell = StateManager.getInstance().getConfigurationManager().getSpell(soliniaItem.getAbilityid());
			if (spell.getEffectid1() != 254 && !Utils.getSpellEffectType(spell.getEffectid1()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid1()).name() + "(" + spell.getEffectBaseValue1() + ")" + ChatColor.RESET);
			if (spell.getEffectid2() != 254 && !Utils.getSpellEffectType(spell.getEffectid2()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid2()).name() + "(" + spell.getEffectBaseValue2() + ")" + ChatColor.RESET);
			if (spell.getEffectid3() != 254 && !Utils.getSpellEffectType(spell.getEffectid3()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid3()).name() + "(" + spell.getEffectBaseValue3() + ")" + ChatColor.RESET);
			if (spell.getEffectid4() != 254 && !Utils.getSpellEffectType(spell.getEffectid4()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid4()).name() + "(" + spell.getEffectBaseValue4() + ")" + ChatColor.RESET);
			if (spell.getEffectid5() != 254 && !Utils.getSpellEffectType(spell.getEffectid5()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid5()).name() + "(" + spell.getEffectBaseValue5() + ")" + ChatColor.RESET);
			if (spell.getEffectid6() != 254 && !Utils.getSpellEffectType(spell.getEffectid6()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid6()).name() + "(" + spell.getEffectBaseValue6() + ")" + ChatColor.RESET);
			if (spell.getEffectid7() != 254 && !Utils.getSpellEffectType(spell.getEffectid7()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid7()).name() + "(" + spell.getEffectBaseValue7() + ")" + ChatColor.RESET);
			if (spell.getEffectid8() != 254 && !Utils.getSpellEffectType(spell.getEffectid8()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid8()).name() + "(" + spell.getEffectBaseValue8() + ")" + ChatColor.RESET);
			if (spell.getEffectid9() != 254 && !Utils.getSpellEffectType(spell.getEffectid9()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid9()).name() + "(" + spell.getEffectBaseValue9() + ")" + ChatColor.RESET);
			if (spell.getEffectid10() != 254 && !Utils.getSpellEffectType(spell.getEffectid10()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid10()).name() + "(" + spell.getEffectBaseValue10() + ")" + ChatColor.RESET);
			if (spell.getEffectid11() != 254 && !Utils.getSpellEffectType(spell.getEffectid11()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid11()).name() + "(" + spell.getEffectBaseValue11() + ")" + ChatColor.RESET);
			if (spell.getEffectid12() != 254 && !Utils.getSpellEffectType(spell.getEffectid12()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Consumable Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid12()).name() + "(" + spell.getEffectBaseValue12() + ")" + ChatColor.RESET);
			
			if (spell.isBuffSpell() && spell.getBuffduration() > 0)
			{
				loreTxt.add(ChatColor.WHITE + "Consumable Buff Duration: " + ChatColor.YELLOW + ((spell.getBuffduration() * 6) / 60) + " minutes" + ChatColor.RESET);
			}
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
			if (spell.getEffectid1() != 254 && !Utils.getSpellEffectType(spell.getEffectid1()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid1()).name() + "(" + spell.getEffectBaseValue1() + ")" + ChatColor.RESET);
			if (spell.getEffectid2() != 254 && !Utils.getSpellEffectType(spell.getEffectid2()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid2()).name() + "(" + spell.getEffectBaseValue2() + ")" + ChatColor.RESET);
			if (spell.getEffectid3() != 254 && !Utils.getSpellEffectType(spell.getEffectid3()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid3()).name() + "(" + spell.getEffectBaseValue3() + ")" + ChatColor.RESET);
			if (spell.getEffectid4() != 254 && !Utils.getSpellEffectType(spell.getEffectid4()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid4()).name() + "(" + spell.getEffectBaseValue4() + ")" + ChatColor.RESET);
			if (spell.getEffectid5() != 254 && !Utils.getSpellEffectType(spell.getEffectid5()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid5()).name() + "(" + spell.getEffectBaseValue5() + ")" + ChatColor.RESET);
			if (spell.getEffectid6() != 254 && !Utils.getSpellEffectType(spell.getEffectid6()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid6()).name() + "(" + spell.getEffectBaseValue6() + ")" + ChatColor.RESET);
			if (spell.getEffectid7() != 254 && !Utils.getSpellEffectType(spell.getEffectid7()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid7()).name() + "(" + spell.getEffectBaseValue7() + ")" + ChatColor.RESET);
			if (spell.getEffectid8() != 254 && !Utils.getSpellEffectType(spell.getEffectid8()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid8()).name() + "(" + spell.getEffectBaseValue8() + ")" + ChatColor.RESET);
			if (spell.getEffectid9() != 254 && !Utils.getSpellEffectType(spell.getEffectid9()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid9()).name() + "(" + spell.getEffectBaseValue9() + ")" + ChatColor.RESET);
			if (spell.getEffectid10() != 254 && !Utils.getSpellEffectType(spell.getEffectid10()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid10()).name() + "(" + spell.getEffectBaseValue10() + ")" + ChatColor.RESET);
			if (spell.getEffectid11() != 254 && !Utils.getSpellEffectType(spell.getEffectid11()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid11()).name() + "(" + spell.getEffectBaseValue11() + ")" + ChatColor.RESET);
			if (spell.getEffectid12() != 254 && !Utils.getSpellEffectType(spell.getEffectid12()).name().contains("LIMIT_"))
				loreTxt.add(ChatColor.WHITE + "Ability: " + ChatColor.YELLOW+Utils.getSpellEffectType(spell.getEffectid12()).name() + "(" + spell.getEffectBaseValue12() + ")" + ChatColor.RESET);
			
			loreTxt.add(ChatColor.WHITE + "Mana/Power: " + ChatColor.YELLOW+spell.getMana() + ChatColor.RESET);
			loreTxt.add(ChatColor.WHITE + "Range: " + ChatColor.YELLOW+spell.getRange() + ChatColor.RESET);
			
			if (spell.isAASpell())
			{
				loreTxt.add(ChatColor.WHITE + "This spell is an AA spell" + ChatColor.RESET);
			}
			
			if (spell.isBuffSpell() && spell.getBuffduration() > 0)
			{
				loreTxt.add(ChatColor.WHITE + "Buff Duration: " + ChatColor.YELLOW + ((spell.getBuffduration() * 6) / 60) + " minutes" + ChatColor.RESET);
			}
			loreTxt.add(ChatColor.WHITE + "Target Type: " + ChatColor.YELLOW + Utils.getSpellTargetType(spell.getTargettype()).name() + ChatColor.RESET);
			String classesBuilder = "";
			List<SoliniaSpellClass> allowedSpellClasses = spell.getAllowedClasses();
			int rowcount = 0;
			for (SoliniaSpellClass spellclass : allowedSpellClasses)
			{
				if (StateManager.getInstance().getConfigurationManager().getClassObj(spellclass.getClassname().toUpperCase()) == null)
					continue;
				
				classesBuilder += ChatColor.WHITE + spellclass.getClassname() + " (" + ChatColor.YELLOW + spellclass.getMinlevel() + ChatColor.WHITE + ") " + ChatColor.RESET;
				rowcount++;
				if (rowcount > 2)
				{
					loreTxt.add(classesBuilder);
					classesBuilder = "";
					
				}
			}
			
			// If we never reached 2 classes on a row, handle the overspill here			
			if (!classesBuilder.equals(""))
				loreTxt.add(classesBuilder);
			
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return loreTxt;
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
