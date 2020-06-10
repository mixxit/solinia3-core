package com.solinia.solinia.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Commands.CommandToday;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.CharacterCreation;
import com.solinia.solinia.Models.PacketOpenCharacterCreation;
import com.solinia.solinia.Models.RaceChoice;
import com.solinia.solinia.Models.Solinia3UIChannelNames;
import com.solinia.solinia.Models.Solinia3UIPacketDiscriminators;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaZone;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.Tuple;

public class PlayerUtils {
	public static String uuidFromBase64(String str) {
		byte[] bytes = TextUtils.FromBase64UTF8(str).getBytes();
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		UUID uuid = new UUID(bb.getLong(), bb.getLong());
		return uuid.toString();
	}
	
	public static int getMaxUnspentAAPoints() {
		// TODO Auto-generated method stub
		return 1000;
	}
	
	public static void sendCharCreation(Player sender) {
		try {
			if (StateManager.getInstance().getPlayerManager().hasValidMod(sender))
			{
		    	PacketOpenCharacterCreation packet = new PacketOpenCharacterCreation();
		    	packet.fromData(StateManager.getInstance().getConfigurationManager().getCharacterCreationChoices());
			ForgeUtils.QueueSendForgeMessage(((Player)sender),Solinia3UIChannelNames.Outgoing,Solinia3UIPacketDiscriminators.CHARCREATION,packet.toPacketData(), 0);
			} else {
				sendCharCreationNoMod(sender);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendCharCreationNoMod(Player sender) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle("Creation");
		bookMeta.setAuthor("Server");
		
		try
		{
			CharacterCreation choices = StateManager.getInstance().getConfigurationManager().getCharacterCreationChoices();
			
			String pageText = "";
			BaseComponent[] introPage = new ComponentBuilder(
							ChatColor.RED + "Race/Class Selection\n" + ChatColor.RESET +
							"---------------\n" +
							"\n" +
							"Please cycle through\n" +
							"our choice of races\n" +
							"and classes\n" +
							"\n" +
							"When you are ready \n" +
							"click "+ChatColor.BLUE+"SELECT"+ChatColor.RESET+" on the page\n"
					
					).create();
			bookMeta.spigot().addPage(introPage);
			for(String choiceKey : choices.raceChoices.keySet())
			{
				RaceChoice choice = choices.raceChoices.get(choiceKey);
				int passiveId = StateManager.getInstance().getConfigurationManager().getRace(choice.RaceId).getPassiveAbilityId();
				ISoliniaSpell passiveSpell = StateManager.getInstance().getConfigurationManager().getSpell(passiveId);
				String header = ChatColor.RED + "" +choice.RaceName + " " + choice.ClassName + ChatColor.RESET+"\n";
				String hover = "["+ChatColor.RED+"Hover for More Info"+ChatColor.RESET+"]\n";
				String text = "";
				text += "\n";
				text += "STR "+ChatColor.BLUE+choice.STR+ChatColor.RESET+" STA "+ChatColor.BLUE+choice.STA+ChatColor.RESET+" AGI " + ChatColor.BLUE+choice.AGI+ChatColor.RESET + "\n";
				text += "DEX "+ChatColor.BLUE+choice.DEX+ChatColor.RESET+" INT "+ChatColor.BLUE+choice.INT+ChatColor.RESET+" WIS " + ChatColor.BLUE+choice.WIS+ChatColor.RESET + "\n";
				text += "CHA "+ChatColor.BLUE+choice.CHA+ChatColor.RESET+"\n";
				text += "\n";
				text += "Racial Benefit: " + ChatColor.RED+passiveSpell.getName()+ChatColor.RESET +  "\n";
				text += "\n";

				
				
				String details = 
						ChatColor.GOLD + choice.RaceName + ChatColor.RESET + System.lineSeparator() 
						+ "Recommended Alignment: " + ChatColor.GOLD + choice.Alignment + ChatColor.RESET + System.lineSeparator() 
						+ choice.RaceDescription + System.lineSeparator()
				+ ChatColor.GOLD + choice.ClassName + ChatColor.RESET + System.lineSeparator() + 
				choice.ClassDescription;
				
				String click = "\n["+ChatColor.BLUE+"SELECT"+ChatColor.RESET+"]\n";
				
				BaseComponent[] racePage = new ComponentBuilder(header)
						.append(new ComponentBuilder(hover).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create())).create())
						.append(new ComponentBuilder(text).create())
						.append(new ComponentBuilder(click).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/createcharactershort "+choice.RaceId + " "+ choice.ClassId)).create())
						.create();

				//						
				//)

				
				bookMeta.spigot().addPage(racePage);
			}
			
			/*			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://spigotmc.org"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go to the spigot website!").create()))
			        .create();*/

			
			book.setItemMeta(bookMeta);
			sender.openBook(book);
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		
	}

	
	public static boolean AddAccountClaim(String mcaccountname, int itemId) {
		try {
			SoliniaAccountClaim newclaim = new SoliniaAccountClaim();
			newclaim.setId(StateManager.getInstance().getConfigurationManager().getNextAccountClaimId());
			newclaim.setMcname(mcaccountname);
			newclaim.setItemid(itemId);
			newclaim.setClaimed(false);

			StateManager.getInstance().getConfigurationManager().addAccountClaim(newclaim);
			return true;
		} catch (CoreStateInitException e) {

		}
		return false;
	}

	public static boolean canChangeCharacter(Player player) {
		Timestamp lastChange = null;

		try {
			lastChange = StateManager.getInstance().getPlayerManager().getPlayerLastChangeChar(player.getUniqueId());

		} catch (CoreStateInitException e) {
			return false;
		}

		if (lastChange == null)
			return true;

		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		Timestamp mintimestamp = Timestamp.valueOf(lastChange.toLocalDateTime().plus(10, ChronoUnit.MINUTES));

		if (nowtimestamp.before(mintimestamp))
			return false;

		return true;

	}
	
	public static boolean canUnstuck(Player player) {
		Timestamp lastChange = null;

		try {
			lastChange = StateManager.getInstance().getPlayerManager().getPlayerLastUnstuck(player.getUniqueId());

			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
			if (solPlayer == null)
			{
				player.sendMessage("You cannot use unstuck as you are not a valid player");
				return false;
			}
			
			for(SoliniaZone zone : solPlayer.getZones())
				if (zone.isNoUnstuck())
				{
					player.sendMessage("You cannot use unstuck in this zone");
					return false;
				}
				
		} catch (CoreStateInitException e) {
			player.sendMessage("You cannot use unstuck at his time");
			return false;
		}

		if (lastChange == null)
			return true;

		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		Timestamp mintimestamp = Timestamp.valueOf(lastChange.toLocalDateTime().plus(3, ChronoUnit.HOURS));

		if (nowtimestamp.before(mintimestamp))
		{
			player.sendMessage("You can only unstuck your character every 3 hours");
			return false;
		}

		return true;

	}

	
	public static String getTextureFromName(String name) {
		String texture = "";
		try {
			String uuid = PlayerUtils.getUUIDFromPlayerName(name);
			if (uuid == null)
				return null;

			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
					.getAsJsonArray().get(0).getAsJsonObject();
			texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
		} catch (IOException e) {
			System.err.println("Could not get skin data from session servers!");
			e.printStackTrace();
			return null;
		}

		return texture;
	}
	
	public static Tuple<Integer,Integer> GetGroupExpMinAndMaxLevel(List<Integer> levelRanges)
	{
		Integer dhighestlevel = 0;
		Collections.sort(levelRanges);

		// get the highest person in the group
		dhighestlevel = levelRanges.get(levelRanges.size() - 1);

		int ihighlvl = (int) Math.floor(dhighestlevel);
		int ilowlvl = EntityUtils.getMinLevelFromLevel(ihighlvl);

		if (ilowlvl < 1) {
			ilowlvl = 1;
		}
		
		return new Tuple<Integer,Integer>(ilowlvl,ihighlvl);
	}
	
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
			if (tmpitemid >= 255)
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
			if (playersLevel < 60) {
				return experience * 2d;
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

		if (!(CraftItemStack.asNMSCopy(itemstack).getItem() instanceof net.minecraft.server.v1_15_R1.ItemArmor)) {
			return;
		}

		if (ItemStackUtils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

				if (soliniaitem.getAllowedClassNamesUpper().size() > 0)
				{
					if (solplayer.getClassObj() == null) {
						EntityUtils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedClassNamesUpper().contains(solplayer.getClassObj().getName())) {
						EntityUtils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
						return;
					}
				}
				
				if (soliniaitem.getAllowedRaceNamesUpper().size() > 0)
				{
					if (solplayer.getRace() == null) {
						EntityUtils.CancelEvent(event);
						;
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
	
					if (!soliniaitem.getAllowedRaceNamesUpper().contains(solplayer.getRace().getName())) {
						EntityUtils.CancelEvent(event);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(ChatColor.GRAY + "Your race cannot wear this armour");
						return;
					}
				}

				if (soliniaitem.getMinLevel() > solplayer.getActualLevel()) {
					EntityUtils.CancelEvent(event);
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
		String ageinfo = "Age: ???";
		if (solplayer.getBirthday() != null)
		{
			int age = 0;
			
			String text = "2020-01-01 00:00:00.00";
			LocalDateTime fromDate = Timestamp.valueOf(text).toLocalDateTime();
			LocalDateTime toDate = solplayer.getBirthday().toLocalDateTime();
			
			int characterbirthyear = CommandToday.getUTYear(fromDate, toDate);
			int currentutyear = CommandToday.getCurrentUTYear();
			
			ageinfo = "Age: " + (currentutyear-characterbirthyear);
		}
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
				ageinfo + " " + backstory +
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
