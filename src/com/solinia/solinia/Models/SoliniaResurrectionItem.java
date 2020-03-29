package com.solinia.solinia.Models;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class SoliniaResurrectionItem {
	String playeruuidb64;
	int experience;
	String playername;
	java.sql.Timestamp timestamp;
	
	public SoliniaResurrectionItem(ISoliniaPlayer solplayer, int experience, Timestamp timestamp)
	{
		this.playeruuidb64 = uuidToBase64(solplayer.getUUID().toString());
		this.playername = solplayer.getFullName();
		this.experience = experience;
		this.timestamp = timestamp;
	}
	
	public ItemStack AsItemStack()
	{
		ItemStack stack = new ItemStack(Material.NAME_TAG);
		ItemMeta i = stack.getItemMeta();
		i.spigot().setUnbreakable(true);
		i.setDisplayName("Signaculum");
		List<String> loretxt = new ArrayList<String>();
		loretxt.add("This signaculum belongs");
		loretxt.add("to "+ ChatColor.YELLOW+playername+ChatColor.RESET+". It may be");
		loretxt.add("possible to restore them to life.");
		loretxt.add(experience+"|"+timestamp);
		loretxt.add(playeruuidb64);
	    i.setLore(loretxt);
	    stack.setItemMeta(i);
	    stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
	    return stack;
	}
	
	private String uuidToBase64(String str) {
	    UUID uuid = UUID.fromString(str);
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return Base64.encodeBase64URLSafeString(bb.array());
	}
}