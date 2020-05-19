package com.solinia.solinia.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonParser;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaZone;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.Tuple;

public class PlayerUtils {

	public static double calculateExpLoss(ISoliniaPlayer player) {
		double loss = 0;
		loss = (double) (player.getMentorLevel() * (player.getMentorLevel() / 18.0) * 12000);
		return (double) loss;
	}
	public static void BroadcastPlayers(String message) {
		BroadcastPlayers(message, null);
	}
	
	public static void BroadcastPlayers(String message, ItemStack itemStack) {
		message = ChatColor.YELLOW + "[Announcement] " + message + " "+ ChatColor.RESET;
		TextComponent tc = new TextComponent(message);

		if (itemStack != null && itemStack.getItemMeta() != null)
		{
			TextComponent itemLinkComponent = new TextComponent();
			String title = " <" + itemStack.getItemMeta().getDisplayName() + ">";
			itemLinkComponent.setText(title);
			itemLinkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemStackUtils.ConvertItemStackToJsonRegular(itemStack)).create()));
			tc.addExtra(itemLinkComponent);
		}

		for (World world : Bukkit.getWorlds()) {
			for (Player player : world.getPlayers()) {
				player.spigot().sendMessage(tc);
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
		String uuid = null;
		try
		{
			URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();
		} catch (Exception e)
		{
			System.out.println("Could not find UUID for player name: " + playerName);
		}
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
	

	public static Double getExperienceRewardAverageForLevel(int mobsLevel, int playersLevel) {
		try
		{
			Double experience = (Math.pow(mobsLevel, 2) * 10) * StateManager.getInstance().getConfigurationManager().getMaxLevel() - 1;
			experience = experience / 2;
			if (experience < 1) {
				experience = 1d;
			}
	
			if (playersLevel < 10) {
				return experience * 6d;
			}
	
			if (playersLevel < 20) {
				return experience * 5d;
			}
	
			if (playersLevel < 30) {
				return experience * 5d;
			}
	
			if (playersLevel < 40) {
				return experience * 4d;
			}
	
			if (playersLevel < 50) {
				return experience * 3d;
			}
			return experience;
		} catch (CoreStateInitException e)
		{
			return 0D;
		}
	}

	public static Double getMaxAAXP() {
		// TODO Auto-generated method stub
		return getExperienceRequirementForLevel(51) - getExperienceRequirementForLevel(50);
	}
	
	public static Double getMaxClaimXP() {
		// TODO Auto-generated method stub
		return getMaxAAXP()/10;
	}
	
	public static Double getMaxFellowshipXP() {
		// TODO Auto-generated method stub
		return getMaxAAXP()/4;
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

				if (soliniaitem.getAllowedClassNamesUpper().size() > 0)
				{
					if (solplayer.getClassObj() == null) {
						Utils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedClassNamesUpper().contains(solplayer.getClassObj().getName())) {
						Utils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
				}
				
				if (soliniaitem.getAllowedRaceNamesUpper().size() > 0)
				{
					if (solplayer.getRace() == null) {
						Utils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedRaceNamesUpper().contains(solplayer.getRace().getName())) {
						Utils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
				}

				if (soliniaitem.getMinLevel() > solplayer.getActualLevel()) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your level cannot wear this armour");
					return;
				}

			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public static void addToPlayersInventory(Player player, ItemStack item) {
		try
		{
			ISoliniaItem solItem = SoliniaItemAdapter.Adapt(item);
			if (solItem == null)
				return;
			
			if (player.getInventory().firstEmpty() == -1)
			{
				try
				{
					SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
					ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
					final int newid = StateManager.getInstance().getConfigurationManager().getNextAccountClaimId();
					newclaim.setId(newid);
					newclaim.setMcname(player.getName());
					newclaim.setItemid(solItem.getId());
					newclaim.setClaimed(false);
					StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
					player.sendMessage(ChatColor.GRAY + "Your inventory was full so we could not place the item into it ("+newid+")" + ChatColor.RESET);
					player.sendMessage(ChatColor.GRAY + "It has been added to your /claims instead" + ChatColor.RESET);
				} catch (CoreStateInitException e)
				{
					
				}
			} else {
				player.getInventory().addItem(item);
				player.updateInventory();
				player.sendMessage(ChatColor.GRAY + "Item added to your inventory" + ChatColor.RESET);
				//((Player)sender).getLocation().getWorld().dropItemNaturally(((Player)sender).getLocation(), item);
			}
		} catch (CoreStateInitException e)
		{
			player.sendMessage("Could not grant item in your inventory as the plugin was inactive");
			return;
		} catch (SoliniaItemException e1) {
			player.sendMessage("Could not grant item in your inventory as the solinia item does not exist");
			return;
		}

		
	}
	public static Tuple<String,TextComponent> GetCharacterText(ISoliniaPlayer solplayer, String optionalhidden, String optionalplayername, String optionalworldname, String optionalzone, boolean showPersonality) {
		int lvl = (int) Math.floor(solplayer.getActualLevel());
		int mentorLevel = (int)Math.floor(solplayer.getMentorLevel());
		
		String racename = "UNKNOWN";
		String classname = "UNKNOWN";
		String godname = "UNKNOWN /setgod <name>";
		String zone = "UNKNOWN";
		if (optionalzone != null && !zone.equals(""))
			zone = optionalzone;
		String hidden = "";
		if (optionalhidden != null && !optionalhidden.equals(""))
			hidden = optionalhidden;
		
		if (solplayer.getRace() != null)
			racename = solplayer.getRace().getShortName();
		if (solplayer.getClassObj() != null)
			classname = solplayer.getClassObj().getShortName();
		
		String message = hidden+"["+optionalplayername+"]"+ChatColor.YELLOW + solplayer.getFullName().toUpperCase() + ChatColor.RESET + " ["+ optionalworldname +"] - LVL " + ChatColor.AQUA + mentorLevel + "/"+lvl + ChatColor.RESET + " " + racename + " " + ChatColor.AQUA + classname + ChatColor.RESET + " Zone: " + ChatColor.AQUA + zone + ChatColor.RESET;
		
		TextComponent tc = new TextComponent(TextComponent.fromLegacyText(message));
		
		String worship = "I worship: " + godname + System.lineSeparator();
		
		String ideal = "Ideal: I have no ideal" + System.lineSeparator();
		String trait1 = "Trait: I have no primary trait" + System.lineSeparator();
		String trait2 = "Trait: I have no secondary trait" + System.lineSeparator();
		String bond = "Bond: I have no bond" + System.lineSeparator();
		String flaw = "Flaw: I have no flaw" + System.lineSeparator();
		String oath = "Oath: I have no oath" + System.lineSeparator();
		
		if (solplayer.getGodId() > 0)
			worship = "I worship: " + solplayer.getGod().getName() + System.lineSeparator();
		String backstory = "My back story is a mystery" + System.lineSeparator();
		if (solplayer.getBackStory() != null && !solplayer.getBackStory().equals(""))
		backstory = "Backstory:" + solplayer.getBackStory() + System.lineSeparator();
		if (solplayer.getPersonality().getIdealId() > 0)
		ideal = "Ideal:" + solplayer.getPersonality().getIdeal().description + System.lineSeparator();
		if (solplayer.getPersonality().getFirstTraitId() > 0)
		trait1 = "Trait:" + solplayer.getPersonality().getFirstTrait().description + System.lineSeparator();
		if (solplayer.getPersonality().getSecondTraitId() > 0)
		trait2 = "Trait:" + solplayer.getPersonality().getSecondTrait().description + System.lineSeparator();
		if (solplayer.getPersonality().getBondId() > 0)
		bond = "Bond:" + solplayer.getPersonality().getBond().description + System.lineSeparator();
		if (solplayer.getPersonality().getFlawId() > 0)
		flaw = "Flaw:" + solplayer.getPersonality().getFlaw().description + System.lineSeparator();
		String custom = "";
		for(String customTrait : solplayer.getPersonality().getCustomPersonalityTraits())
		{
			custom += "Custom:" + customTrait + System.lineSeparator();
		}
		if (solplayer.getClassObj() != null && solplayer.getClassObj().getOaths().size() > 0 && solplayer.getOathId() != 0)
		{
			oath = "Oath: " + solplayer.getOath().oathname + System.lineSeparator();
			for(String tenet : solplayer.getOath().tenets)
			{
				oath += " " + tenet;
			}
		}
		
		String inspiration = "Inspiration Points: " + solplayer.getInspiration() + System.lineSeparator();
		
		String details = 
				ChatColor.GOLD + solplayer.getFullName().toUpperCase() + " Level " + lvl + " " + racename + " " + classname + ChatColor.RESET + System.lineSeparator() + 
				backstory +
				inspiration;
		
		if (showPersonality)
		{
			details += worship + 
			ideal +
			trait1 +
			trait2 + 
			bond +
			flaw + 
			custom +
			oath;
		} else {
			details += "To see more information:" + System.lineSeparator() + "/personality player " + solplayer.getId() + " [or click]";
		}
		
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/personality player " + solplayer.getId()));
		tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
		return new Tuple<String,TextComponent>(message,tc);
	}

}
