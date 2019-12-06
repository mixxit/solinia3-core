package com.solinia.solinia.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonParser;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class PlayerUtils {

	public static double calculateExpLoss(ISoliniaPlayer player) {
		double loss = 0;
		loss = (double) (player.getLevel() * (player.getLevel() / 18.0) * 12000);
		return (double) loss;
	}
	public static void BroadcastPlayers(String message) {
		for (World world : Bukkit.getWorlds()) {
			for (Player player : world.getPlayers()) {
				player.sendMessage(ChatColor.YELLOW + "[Announcement] " + message + ChatColor.RESET);
			}
		}
	}
	public static int getLevelFromExperience(Double experience) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double level = experience / levelfactor / racemodifier / classmodifier;
		level = java.lang.Math.pow(level, 0.25) + 1;
		return (int) java.lang.Math.floor(level);
	}
	public static String getUUIDFromPlayerName(String playerName) throws IOException {
		URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
		String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();
		return uuid;
	}

	public static int getPlayerTotalCountOfItemId(Player player, int itemid) {
		int total = 0;

		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType() == null || itemstack.getType().equals(Material.AIR))
				continue;

			if (!ItemStackUtils.IsSoliniaItem(itemstack))
				continue;

			int tmpitemid = 0;

			try {
				tmpitemid = SoliniaItemAdapter.Adapt(itemstack).getId();
			} catch (SoliniaItemException e) {
				continue;
			} catch (CoreStateInitException e) {
				continue;
			}

			if (tmpitemid == itemid) {
				total = total + itemstack.getAmount();
			}
		}

		return total;
	}

	public static int removeItemsFromInventory(Player player, int itemid, int count) {
		int removed = 0;
		int remaining = count;
		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType() == null || itemstack.getType().equals(Material.AIR))
				continue;

			if (!ItemStackUtils.IsSoliniaItem(itemstack))
				continue;

			// covers cases of special tmp ids
			int tmpitemid = itemstack.getEnchantmentLevel(Enchantment.DURABILITY);
			if (tmpitemid == 999)
				continue;

			try {
				tmpitemid = SoliniaItemAdapter.Adapt(itemstack).getId();
			} catch (SoliniaItemException e) {
				continue;
			} catch (CoreStateInitException e) {
				continue;
			}

			if (remaining < 1)
				break;

			if (tmpitemid != itemid)
				continue;

			if (remaining <= itemstack.getAmount()) {
				removed = removed + remaining;
				itemstack.setAmount(itemstack.getAmount() - remaining);
				remaining = 0;
				break;
			}

			if (remaining > 64) {
				if (itemstack.getAmount() < 64) {
					removed = removed + itemstack.getAmount();
					remaining = remaining - itemstack.getAmount();
					itemstack.setAmount(0);
				} else {
					removed = removed + 64;
					remaining = remaining - 64;
					itemstack.setAmount(itemstack.getAmount() - 64);
				}
			} else {
				removed = removed + itemstack.getAmount();
				remaining = remaining - itemstack.getAmount();
				itemstack.setAmount(0);
			}
		}

		player.updateInventory();
		return removed;
	}
	

	public static Double getExperienceRewardAverageForLevel(int level) {
		Double experience = (Math.pow(level, 2) * 10) * Utils.getMaxLevel() - 1;
		experience = experience / 2;
		if (experience < 1) {
			experience = 1d;
		}

		if (level < 10) {
			return experience * 6d;
		}

		if (level < 20) {
			return experience * 5d;
		}

		if (level < 30) {
			return experience * 4d;
		}

		if (level < 40) {
			return experience * 3d;
		}

		if (level < 50) {
			return experience * 2d;
		}

		return experience;
	}

	public static Double getMaxAAXP() {
		// TODO Auto-generated method stub
		return 578360000d;
	}

	public static double getExperienceRequirementForLevel(int level) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double experiencerequired = (java.lang.Math.pow(level - 1, 4)) * classmodifier * racemodifier * levelfactor;
		return experiencerequired;
	}

	// TODO - Move this to a value setting on the SoliniaClass object
	public static double getClassXPModifier(ISoliniaClass soliniaClass) {
		double percentagemodifier = 100;

		if (soliniaClass == null)
			return percentagemodifier;

		if (soliniaClass.getName().equals("CLERIC") || soliniaClass.getName().equals("DRUID")
				|| soliniaClass.getName().equals("SHAMAN"))
			percentagemodifier = 120;

		return percentagemodifier;
	}
	

	public static void checkArmourEquip(ISoliniaPlayer solplayer, PlayerInteractEvent event) {
		ItemStack itemstack = event.getItem();
		if (itemstack == null)
			return;

		if (!(CraftItemStack.asNMSCopy(itemstack).getItem() instanceof net.minecraft.server.v1_14_R1.ItemArmor)) {
			return;
		}

		if (ItemStackUtils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

				if (soliniaitem.getAllowedClassNames().size() > 0)
				{
					if (solplayer.getClassObj() == null) {
						Utils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName())) {
						Utils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
				}
				
				if (soliniaitem.getAllowedRaceNames().size() > 0)
				{
					if (solplayer.getRace() == null) {
						Utils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedRaceNames().contains(solplayer.getRace().getName())) {
						Utils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
				}

				if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
