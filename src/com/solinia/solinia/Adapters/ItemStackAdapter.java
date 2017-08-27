package com.solinia.solinia.Adapters;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class ItemStackAdapter {
	public static ItemStack Adapt(ISoliniaItem soliniaItem) {
		ItemStack stack = new ItemStack(Material.valueOf(soliniaItem.getBasename().toUpperCase()));

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
			classtxt = "Wearable By: ";
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

	private static ItemMeta buildSkull(SkullMeta i, UUID skinuuid, String texturebase64, Object object) {
		// TODO Auto-generated method stub
		return null;
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
