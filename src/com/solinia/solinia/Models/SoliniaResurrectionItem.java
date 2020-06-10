package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.TextUtils;

public class SoliniaResurrectionItem {
	String playeruuidb64; // actualy character id now
	int experience;
	String playername;
	java.sql.Timestamp timestamp;
	
	public SoliniaResurrectionItem(ISoliniaPlayer solplayer, int experience, Timestamp timestamp)
	{
		this.playeruuidb64 = Integer.toString(solplayer.getId()); // actualy character id now
		this.playername = solplayer.getFullName();
		this.experience = experience;
		this.timestamp = timestamp;
	}
	
	public ItemStack AsItemStack()
	{
		ItemStack stack = new ItemStack(Material.NAME_TAG);
		ItemMeta i = stack.getItemMeta();
		i.setUnbreakable(true);
		i.setDisplayName("Signaculum");
		List<String> loretxt = new ArrayList<String>();
		loretxt.add("This signaculum belongs");
		loretxt.add("to "+ ChatColor.YELLOW+playername+ChatColor.RESET+". It may be");
		loretxt.add("possible to restore them to life.");
		loretxt.add(experience+"|"+timestamp);
		loretxt.add(playeruuidb64); // actualy character id now
		System.out.println("Signaculum has been set to " +playeruuidb64 ); // actualy character id now
	    i.setLore(loretxt);
	    stack.setItemMeta(i);
	    stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
	    return stack;
	}
	
	private String uuidToBase64(String str) {
		return TextUtils.ToBase64UTF8(str);
	}
}