package com.solinia.solinia.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.comphenix.example.Vector3D;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillReward;
import com.solinia.solinia.Models.SoliniaAAPrereq;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SpellEffectIndex;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Models.WorldWidePerk;

import net.md_5.bungee.api.ChatColor;

public class Utils {

	public static List<WorldWidePerk> getActiveWorldWidePerks() {
		List<WorldWidePerk> perks = new ArrayList<WorldWidePerk>();

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		Timestamp currenttimestamp = new Timestamp(now.getTime());

		for (WorldWidePerk entity : StateManager.getInstance().getWorldWidePerks()) {
			// System.out.println("Comparing Perk [" + entity.getId() + "/" +
			// entity.getContributor() + "] time: " +
			// entity.getEndtimeAsTimestamp().toLocaleString() + " against now " +
			// now.toLocaleString());
			if (entity.getEndtimeAsTimestamp().after(currenttimestamp)) {
				perks.add(entity);
			}
		}
		return perks;
	}

	public static int GetWorldPerkDropCountModifier() {
		int dropcount = 1;

		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			if (perk.getPerkname().equals("DROP100")) {
				dropcount += 1;
			}
		}

		return dropcount;
	}

	public static double getWorldPerkXPModifier() {
		double xppercent = 100;
		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			if (perk.getPerkname().equals("XPBONUS50")) {
				xppercent += 50;
			}

			if (perk.getPerkname().equals("XPBONUS100")) {
				xppercent += 100;
			}

			if (perk.getPerkname().equals("XPBONUS150")) {
				xppercent += 150;
			}

			if (perk.getPerkname().equals("XPBONUS200")) {
				xppercent += 200;
			}
		}

		return xppercent;
	}

	public static void broadcastPerks() {
		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage("* You are currently receiving " + perk.getPerkname() + " from contributor "
						+ perk.getContributor());
			}
		}
	}

	public static String getTextureFromName(String name) {
		String texture = "";
		try {
			URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

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

	public static int getPlayerTotalCountOfItemId(Player player, int itemid) {
		int total = 0;

		System.out.println("Getting total Count of Item Id: " + itemid + " for player: " + player.getDisplayName());
		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType().equals(Material.AIR))
				continue;

			int tmpitemid = itemstack.getEnchantmentLevel(Enchantment.OXYGEN);
			if (tmpitemid < 1000)
				continue;

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

		System.out.println(
				"Total Count of Item Id: " + itemid + " for player: " + player.getDisplayName() + " was " + total);
		return total;
	}

	public static int removeItemsFromInventory(Player player, int itemid, int count) {
		int removed = 0;
		int remaining = count;
		System.out.println("Removing " + count + " of Item Id: " + itemid + " for player: " + player.getDisplayName());
		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType().equals(Material.AIR))
				continue;

			int tmpitemid = itemstack.getEnchantmentLevel(Enchantment.OXYGEN);
			if (tmpitemid < 1000)
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

		System.out.println("Removed " + removed + " of Item Id: " + itemid + " for player: " + player.getDisplayName());

		player.updateInventory();
		return removed;
	}

	public static SpellTargetType getSpellTargetType(int spellTargetId) {
		switch (spellTargetId) {
		case 1:
			return SpellTargetType.TargetOptional;
		case 2:
			return SpellTargetType.AEClientV1;
		case 3:
			return SpellTargetType.GroupTeleport;
		case 4:
			return SpellTargetType.AECaster;
		case 5:
			return SpellTargetType.Target;
		case 6:
			return SpellTargetType.Self;
		case 8:
			return SpellTargetType.AETarget;
		case 9:
			return SpellTargetType.Animal;
		case 10:
			return SpellTargetType.Undead;
		case 11:
			return SpellTargetType.Summoned;
		case 13:
			return SpellTargetType.Tap;
		case 14:
			return SpellTargetType.Pet;
		case 15:
			return SpellTargetType.Corpse;
		case 16:
			return SpellTargetType.Plant;
		case 17:
			return SpellTargetType.Giant;
		case 18:
			return SpellTargetType.Dragon;
		case 20:
			return SpellTargetType.TargetAETap;
		case 24:
			return SpellTargetType.UndeadAE;
		case 25:
			return SpellTargetType.SummonedAE;
		case 32:
			return SpellTargetType.AETargetHateList;
		case 33:
			return SpellTargetType.HateList;
		case 36:
			return SpellTargetType.AreaClientOnly;
		case 37:
			return SpellTargetType.AreaNPCOnly;
		case 38:
			return SpellTargetType.SummonedPet;
		case 39:
			return SpellTargetType.GroupNoPets;
		case 40:
			return SpellTargetType.AEBard;
		case 41:
			return SpellTargetType.Group;
		case 42:
			return SpellTargetType.Directional;
		case 43:
			return SpellTargetType.GroupClientAndPet;
		case 44:
			return SpellTargetType.Beam;
		case 45:
			return SpellTargetType.Ring;
		case 46:
			return SpellTargetType.TargetsTarget;
		case 47:
			return SpellTargetType.PetMaster;
		case 50:
			return SpellTargetType.TargetAENoPlayersPets;
		default:
			return SpellTargetType.Error;
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

	public static boolean RandomChance(int minmum) {
		Random r = new Random();
		int randomInt = r.nextInt(100) + 1;
		if (randomInt > minmum) {
			return true;
		}

		return false;
	}

	public static int RandomBetween(int minnumber, int maxnumber) {
		Random r = new Random();
		return r.nextInt((maxnumber - minnumber) + 1) + minnumber;
	}

	public static SkillReward getSkillForMaterial(String materialstring) {
		SkillReward reward = null;

		int xp = 0;
		String skill = "";

		switch (materialstring) {
		case "WOOD_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "STONE_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "IRON_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "GOLD_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "DIAMOND_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "WOOD_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "STONE_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "IRON_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "GOLD_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "DIAMOND_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "AIR":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STICK":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "WOOD_SPADE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STONE_SPADE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "IRON_SPADE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "GOLD_SPADE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "DIAMOND_SPADE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "BOW":
			xp = 1;
			skill = "ARCHERY";
			break;
		default:
			break;

		}

		if (xp > 0 && !skill.equals("")) {
			reward = new SkillReward(skill, xp);
		}

		return reward;
	}

	public static double getStatMaxHP(ISoliniaPlayer player) {
		ISoliniaClass solprofession = player.getClassObj();

		String profession = "UNSKILLED";
		if (solprofession != null)
			profession = solprofession.getName().toUpperCase();

		double level = getLevelFromExperience(player.getExperience());

		double levelmultiplier = 1;

		if (profession != null) {
			if (profession.equals("UNSKILLED") || profession.equals("UNKNOWN"))
				levelmultiplier = 3;
			if (profession.equals("MONK") || profession.equals("ROGUE") || profession.equals("BARD"))
				levelmultiplier = 18;
			if (profession.equals("CLERIC") || profession.equals("DRUID") || profession.equals("SHAMAN")
					|| profession.equals("EXARCH"))
				levelmultiplier = 15;
			if (profession.equals("MAGICIAN") || profession.equals("NECROMANCER") || profession.equals("ENCHANTER")
					|| profession.equals("WIZARD") || profession.equals("ARCANIST"))
				levelmultiplier = 12;
			if (profession.equals("RANGER") || profession.equals("HUNTER"))
				levelmultiplier = 20;
			if ((profession.equals("SHADOWKNIGHT") || profession.equals("PALADIN") || profession.equals("KNIGHT"))
					&& level <= 34)
				levelmultiplier = 21;
			if ((profession.equals("SHADOWKNIGHT") || profession.equals("PALADIN") || profession.equals("KNIGHT"))
					&& level >= 35)
				levelmultiplier = 22;
			if ((profession.equals("SHADOWKNIGHT") || profession.equals("PALADIN") || profession.equals("KNIGHT"))
					&& level >= 45)
				levelmultiplier = 23;
			if (profession.equals("WARRIOR") && level <= 19)
				levelmultiplier = 22;
			if (profession.equals("WARRIOR") && level >= 20)
				levelmultiplier = 23;
			if (profession.equals("WARRIOR") && level >= 30)
				levelmultiplier = 25;
			if (profession.equals("WARRIOR") && level >= 40)
				levelmultiplier = 27;
		}

		double hp = level * levelmultiplier;
		double hpmain = (player.getStamina() / 12) * level;

		double calculatedhp = hp + hpmain;
		return (int) Math.floor(calculatedhp);
	}

	public static int getMaxLevel() {
		return 31;
	}

	public static Double getExperienceRewardAverageForLevel(int level) {
		Double experience = (Math.pow(level, 2) * 10) * getMaxLevel() - 1;
		experience = experience / 2;
		if (experience < 1) {
			experience = 1d;
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

	public static int getSkillCap(ISoliniaPlayer player, String skillname) {

		// If the skill being queried happens to be a race name, the cap for
		// language is always 100
		try {
			List<ISoliniaRace> races = StateManager.getInstance().getConfigurationManager().getRaces();
			for (ISoliniaRace race : races) {
				if (race.getName().toUpperCase().equals(skillname.toUpperCase())) {
					return 100;
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ISoliniaClass profession = player.getClassObj();

		// TODO - Move all these skill cap bonuses to the race configuration
		// classes

		if (skillname.equals("SLASHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * player.getLevel()) + 5);
					return cap;
				}
		}

		if (skillname.equals("CRUSHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * player.getLevel()) + 5);
					return cap;
				}
		}

		if (skillname.equals("ARCHERY")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("HUNTER"))) {
					int cap = (int) ((5 * player.getLevel()) + 5);
					return cap;
				}
		}

		if (skillname.equals("MEDITATION")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("DRUID")
						|| profession.getName().toUpperCase().equals("WIZARD")
						|| profession.getName().toUpperCase().equals("MAGICIAN")
						|| profession.getName().toUpperCase().equals("NECROMANCER")
						|| profession.getName().toUpperCase().equals("ENCHANTER")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("ARCANIST")
						|| profession.getName().toUpperCase().equals("EXARCH"))) {
					int cap = (int) ((5 * player.getLevel()) + 5);
					return cap;
				}
		}

		int cap = (int) ((2 * player.getLevel()) + 2);
		return cap;
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

	public static <T> T getRandomItemFromList(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	public static void checkArmourEquip(ISoliniaPlayer solplayer, PlayerInteractEvent event) {
		ItemStack itemstack = event.getItem();
		if (itemstack == null)
			return;
		if (!(CraftItemStack.asNMSCopy(itemstack).getItem() instanceof net.minecraft.server.v1_12_R1.ItemArmor)) {
			return;
		}

		if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999
				&& !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

				if (soliniaitem.getAllowedClassNames().size() == 0)
					return;

				if (solplayer.getClassObj() == null) {
					event.setCancelled(true);
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

				if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName())) {
					event.setCancelled(true);
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

	public static String FormatAsName(String name) {
		// TODO Auto-generated method stub
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	public static int getTotalItemStat(ISoliniaPlayer solplayer, String stat) {
		int total = 0;

		try {
			List<ItemStack> itemstacks = new ArrayList<ItemStack>();
			for (ItemStack itemstack : solplayer.getBukkitPlayer().getInventory().getArmorContents()) {
				if (itemstack == null)
					continue;

				itemstacks.add(itemstack);
			}

			if (solplayer.getBukkitPlayer().getInventory().getItemInOffHand() != null)
				itemstacks.add(solplayer.getBukkitPlayer().getInventory().getItemInOffHand());

			for (ItemStack itemstack : itemstacks) {
				if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999
						&& !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {

					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
					switch (stat) {
					case "STRENGTH":
						if (item.getStrength() > 0) {
							total += item.getStrength();
						}
						break;
					case "STAMINA":
						if (item.getStamina() > 0) {
							total += item.getStamina();
						}
						break;
					case "AGILITY":
						if (item.getAgility() > 0) {
							total += item.getAgility();
						}
						break;
					case "DEXTERITY":
						if (item.getDexterity() > 0) {
							total += item.getDexterity();
						}
						break;
					case "INTELLIGENCE":
						if (item.getIntelligence() > 0) {
							total += item.getIntelligence();
						}
						break;
					case "WISDOM":
						if (item.getWisdom() > 0) {
							total += item.getWisdom();
						}
						break;
					case "CHARISMA":
						if (item.getCharisma() > 0) {
							total += item.getCharisma();
						}
						break;
					default:
						break;
					}

				}
			}
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return total;
		}
	}

	public static String getCasterClass(String classname) {
		switch (classname) {
		case "CLERIC":
		case "PALADIN":
		case "RANGER":
		case "DRUID":
		case "SHAMAN":
		case "HUNTER":
		case "EXARCH":
		case "KNIGHT":
			return "W";
		case "ARCANIST":
		case "SHADOWKNIGHT":
		case "BARD":
		case "NECROMANCER":
		case "WIZARD":
		case "MAGICIAN":
		case "ENCHANTER":
			return "I";
		default:
			return "N";
		}
	}

	public static List<LivingEntity> getLivingEntitiesInCone(Player player) {
		List<LivingEntity> le = new ArrayList<LivingEntity>();
		for (Entity e : player.getNearbyEntities(200, 200, 200)) {
			if (e instanceof LivingEntity) {
				if (isEntityInLineOfSight(player, e)) {
					le.add((LivingEntity) e);
				}
			}
		}
		return le;
	}

	public static LivingEntity getTargettedLivingEntity(LivingEntity observer, int reach) {
		Location observerPos = observer.getEyeLocation();
		Vector3D observerDir = new Vector3D(observerPos.getDirection());

		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(reach));

		LivingEntity hit = null;

		// Get nearby entities
		for (Entity targetEntity : observer.getNearbyEntities(reach, reach, reach)) {
			if (!(targetEntity instanceof LivingEntity))
				continue;

			LivingEntity target = (LivingEntity) targetEntity;
			// Bounding box of the given player
			Vector3D targetPos = new Vector3D(target.getLocation());
			Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
			Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

			if (target != observer && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
				if (hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation()
						.distanceSquared(observerPos)) {

					hit = target;
				}
			}
		}

		return hit;
	}

	private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
		final double epsilon = 0.0001f;

		Vector3D d = p2.subtract(p1).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();

		if (Math.abs(c.x) > e.x + ad.x)
			return false;
		if (Math.abs(c.y) > e.y + ad.y)
			return false;
		if (Math.abs(c.z) > e.z + ad.z)
			return false;

		if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
			return false;
		if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
			return false;
		if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
			return false;

		return true;
	}

	public static boolean isEntityInLineOfSight(LivingEntity entityfrom, Entity entityto) {
		if (entityto instanceof LivingEntity) {
			entityto = (LivingEntity) entityto;
			double x = entityfrom.getLocation().toVector().distance(entityto.getLocation().toVector());
			Vector direction = entityfrom.getLocation().getDirection().multiply(x);
			Vector answer = direction.add(entityfrom.getLocation().toVector());
			if (answer.distance(entityto.getLocation().toVector()) < 1.37) {
				if (entityfrom.hasLineOfSight(entityto)) {
					return true;
				}
			}
		}

		return false;
	}

	public static SpellEffectType getSpellEffectType(Integer typeId) {
		switch (typeId) {
		case 0:
			return SpellEffectType.CurrentHP;
		case 1:
			return SpellEffectType.ArmorClass;
		case 2:
			return SpellEffectType.ATK;
		case 3:
			return SpellEffectType.MovementSpeed;
		case 4:
			return SpellEffectType.STR;
		case 5:
			return SpellEffectType.DEX;
		case 6:
			return SpellEffectType.AGI;
		case 7:
			return SpellEffectType.STA;
		case 8:
			return SpellEffectType.INT;
		case 9:
			return SpellEffectType.WIS;
		case 10:
			return SpellEffectType.CHA;
		case 11:
			return SpellEffectType.AttackSpeed;
		case 12:
			return SpellEffectType.Invisibility;
		case 13:
			return SpellEffectType.SeeInvis;
		case 14:
			return SpellEffectType.WaterBreathing;
		case 15:
			return SpellEffectType.CurrentMana;
		case 16:
			return SpellEffectType.NPCFrenzy;
		case 17:
			return SpellEffectType.NPCAwareness;
		case 18:
			return SpellEffectType.Lull;
		case 19:
			return SpellEffectType.AddFaction;
		case 20:
			return SpellEffectType.Blind;
		case 21:
			return SpellEffectType.Stun;
		case 22:
			return SpellEffectType.Charm;
		case 23:
			return SpellEffectType.Fear;
		case 24:
			return SpellEffectType.Stamina;
		case 25:
			return SpellEffectType.BindAffinity;
		case 26:
			return SpellEffectType.Gate;
		case 27:
			return SpellEffectType.CancelMagic;
		case 28:
			return SpellEffectType.InvisVsUndead;
		case 29:
			return SpellEffectType.InvisVsAnimals;
		case 30:
			return SpellEffectType.ChangeFrenzyRad;
		case 31:
			return SpellEffectType.Mez;
		case 32:
			return SpellEffectType.SummonItem;
		case 33:
			return SpellEffectType.SummonPet;
		case 34:
			return SpellEffectType.Confuse;
		case 35:
			return SpellEffectType.DiseaseCounter;
		case 36:
			return SpellEffectType.PoisonCounter;
		case 37:
			return SpellEffectType.DetectHostile;
		case 38:
			return SpellEffectType.DetectMagic;
		case 39:
			return SpellEffectType.DetectPoison;
		case 40:
			return SpellEffectType.DivineAura;
		case 41:
			return SpellEffectType.Destroy;
		case 42:
			return SpellEffectType.ShadowStep;
		case 43:
			return SpellEffectType.Berserk;
		case 44:
			return SpellEffectType.Lycanthropy;
		case 45:
			return SpellEffectType.Vampirism;
		case 46:
			return SpellEffectType.ResistFire;
		case 47:
			return SpellEffectType.ResistCold;
		case 48:
			return SpellEffectType.ResistPoison;
		case 49:
			return SpellEffectType.ResistDisease;
		case 50:
			return SpellEffectType.ResistMagic;
		case 51:
			return SpellEffectType.DetectTraps;
		case 52:
			return SpellEffectType.SenseDead;
		case 53:
			return SpellEffectType.SenseSummoned;
		case 54:
			return SpellEffectType.SenseAnimals;
		case 55:
			return SpellEffectType.Rune;
		case 56:
			return SpellEffectType.TrueNorth;
		case 57:
			return SpellEffectType.Levitate;
		case 58:
			return SpellEffectType.Illusion;
		case 59:
			return SpellEffectType.DamageShield;
		case 60:
			return SpellEffectType.TransferItem;
		case 61:
			return SpellEffectType.Identify;
		case 62:
			return SpellEffectType.ItemID;
		case 63:
			return SpellEffectType.WipeHateList;
		case 64:
			return SpellEffectType.SpinTarget;
		case 65:
			return SpellEffectType.InfraVision;
		case 66:
			return SpellEffectType.UltraVision;
		case 67:
			return SpellEffectType.EyeOfZomm;
		case 68:
			return SpellEffectType.ReclaimPet;
		case 69:
			return SpellEffectType.TotalHP;
		case 70:
			return SpellEffectType.CorpseBomb;
		case 71:
			return SpellEffectType.NecPet;
		case 72:
			return SpellEffectType.PreserveCorpse;
		case 73:
			return SpellEffectType.BindSight;
		case 74:
			return SpellEffectType.FeignDeath;
		case 75:
			return SpellEffectType.VoiceGraft;
		case 76:
			return SpellEffectType.Sentinel;
		case 77:
			return SpellEffectType.LocateCorpse;
		case 78:
			return SpellEffectType.AbsorbMagicAtt;
		case 79:
			return SpellEffectType.CurrentHPOnce;
		case 80:
			return SpellEffectType.EnchantLight;
		case 81:
			return SpellEffectType.Revive;
		case 82:
			return SpellEffectType.SummonPC;
		case 83:
			return SpellEffectType.Teleport;
		case 84:
			return SpellEffectType.TossUp;
		case 85:
			return SpellEffectType.WeaponProc;
		case 86:
			return SpellEffectType.Harmony;
		case 87:
			return SpellEffectType.MagnifyVision;
		case 88:
			return SpellEffectType.Succor;
		case 89:
			return SpellEffectType.ModelSize;
		case 90:
			return SpellEffectType.Cloak;
		case 91:
			return SpellEffectType.SummonCorpse;
		case 92:
			return SpellEffectType.InstantHate;
		case 93:
			return SpellEffectType.StopRain;
		case 94:
			return SpellEffectType.NegateIfCombat;
		case 95:
			return SpellEffectType.Sacrifice;
		case 96:
			return SpellEffectType.Silence;
		case 97:
			return SpellEffectType.ManaPool;
		case 98:
			return SpellEffectType.AttackSpeed2;
		case 99:
			return SpellEffectType.Root;
		case 100:
			return SpellEffectType.HealOverTime;
		case 101:
			return SpellEffectType.CompleteHeal;
		case 102:
			return SpellEffectType.Fearless;
		case 103:
			return SpellEffectType.CallPet;
		case 104:
			return SpellEffectType.Translocate;
		case 105:
			return SpellEffectType.AntiGate;
		case 106:
			return SpellEffectType.SummonBSTPet;
		case 107:
			return SpellEffectType.AlterNPCLevel;
		case 108:
			return SpellEffectType.Familiar;
		case 109:
			return SpellEffectType.SummonItemIntoBag;
		case 110:
			return SpellEffectType.IncreaseArchery;
		case 111:
			return SpellEffectType.ResistAll;
		case 112:
			return SpellEffectType.CastingLevel;
		case 113:
			return SpellEffectType.SummonHorse;
		case 114:
			return SpellEffectType.ChangeAggro;
		case 115:
			return SpellEffectType.Hunger;
		case 116:
			return SpellEffectType.CurseCounter;
		case 117:
			return SpellEffectType.MagicWeapon;
		case 118:
			return SpellEffectType.Amplification;
		case 119:
			return SpellEffectType.AttackSpeed3;
		case 120:
			return SpellEffectType.HealRate;
		case 121:
			return SpellEffectType.ReverseDS;
		case 122:
			return SpellEffectType.ReduceSkill;
		case 123:
			return SpellEffectType.Screech;
		case 124:
			return SpellEffectType.ImprovedDamage;
		case 125:
			return SpellEffectType.ImprovedHeal;
		case 126:
			return SpellEffectType.SpellResistReduction;
		case 127:
			return SpellEffectType.IncreaseSpellHaste;
		case 128:
			return SpellEffectType.IncreaseSpellDuration;
		case 129:
			return SpellEffectType.IncreaseRange;
		case 130:
			return SpellEffectType.SpellHateMod;
		case 131:
			return SpellEffectType.ReduceReagentCost;
		case 132:
			return SpellEffectType.ReduceManaCost;
		case 133:
			return SpellEffectType.FcStunTimeMod;
		case 134:
			return SpellEffectType.LimitMaxLevel;
		case 135:
			return SpellEffectType.LimitResist;
		case 136:
			return SpellEffectType.LimitTarget;
		case 137:
			return SpellEffectType.LimitEffect;
		case 138:
			return SpellEffectType.LimitSpellType;
		case 139:
			return SpellEffectType.LimitSpell;
		case 140:
			return SpellEffectType.LimitMinDur;
		case 141:
			return SpellEffectType.LimitInstant;
		case 142:
			return SpellEffectType.LimitMinLevel;
		case 143:
			return SpellEffectType.LimitCastTimeMin;
		case 144:
			return SpellEffectType.LimitCastTimeMax;
		case 145:
			return SpellEffectType.Teleport2;
		case 146:
			return SpellEffectType.ElectricityResist;
		case 147:
			return SpellEffectType.PercentalHeal;
		case 148:
			return SpellEffectType.StackingCommand_Block;
		case 149:
			return SpellEffectType.StackingCommand_Overwrite;
		case 150:
			return SpellEffectType.DeathSave;
		case 151:
			return SpellEffectType.SuspendPet;
		case 152:
			return SpellEffectType.TemporaryPets;
		case 153:
			return SpellEffectType.BalanceHP;
		case 154:
			return SpellEffectType.DispelDetrimental;
		case 155:
			return SpellEffectType.SpellCritDmgIncrease;
		case 156:
			return SpellEffectType.IllusionCopy;
		case 157:
			return SpellEffectType.SpellDamageShield;
		case 158:
			return SpellEffectType.Reflect;
		case 159:
			return SpellEffectType.AllStats;
		case 160:
			return SpellEffectType.MakeDrunk;
		case 161:
			return SpellEffectType.MitigateSpellDamage;
		case 162:
			return SpellEffectType.MitigateMeleeDamage;
		case 163:
			return SpellEffectType.NegateAttacks;
		case 164:
			return SpellEffectType.AppraiseLDonChest;
		case 165:
			return SpellEffectType.DisarmLDoNTrap;
		case 166:
			return SpellEffectType.UnlockLDoNChest;
		case 167:
			return SpellEffectType.PetPowerIncrease;
		case 168:
			return SpellEffectType.MeleeMitigation;
		case 169:
			return SpellEffectType.CriticalHitChance;
		case 170:
			return SpellEffectType.SpellCritChance;
		case 171:
			return SpellEffectType.CrippBlowChance;
		case 172:
			return SpellEffectType.AvoidMeleeChance;
		case 173:
			return SpellEffectType.RiposteChance;
		case 174:
			return SpellEffectType.DodgeChance;
		case 175:
			return SpellEffectType.ParryChance;
		case 176:
			return SpellEffectType.DualWieldChance;
		case 177:
			return SpellEffectType.DoubleAttackChance;
		case 178:
			return SpellEffectType.MeleeLifetap;
		case 179:
			return SpellEffectType.AllInstrumentMod;
		case 180:
			return SpellEffectType.ResistSpellChance;
		case 181:
			return SpellEffectType.ResistFearChance;
		case 182:
			return SpellEffectType.HundredHands;
		case 183:
			return SpellEffectType.MeleeSkillCheck;
		case 184:
			return SpellEffectType.HitChance;
		case 185:
			return SpellEffectType.DamageModifier;
		case 186:
			return SpellEffectType.MinDamageModifier;
		case 187:
			return SpellEffectType.BalanceMana;
		case 188:
			return SpellEffectType.IncreaseBlockChance;
		case 189:
			return SpellEffectType.CurrentEndurance;
		case 190:
			return SpellEffectType.EndurancePool;
		case 191:
			return SpellEffectType.Amnesia;
		case 192:
			return SpellEffectType.Hate;
		case 193:
			return SpellEffectType.SkillAttack;
		case 194:
			return SpellEffectType.FadingMemories;
		case 195:
			return SpellEffectType.StunResist;
		case 196:
			return SpellEffectType.StrikeThrough;
		case 197:
			return SpellEffectType.SkillDamageTaken;
		case 198:
			return SpellEffectType.CurrentEnduranceOnce;
		case 199:
			return SpellEffectType.Taunt;
		case 200:
			return SpellEffectType.ProcChance;
		case 201:
			return SpellEffectType.RangedProc;
		case 202:
			return SpellEffectType.IllusionOther;
		case 203:
			return SpellEffectType.MassGroupBuff;
		case 204:
			return SpellEffectType.GroupFearImmunity;
		case 205:
			return SpellEffectType.Rampage;
		case 206:
			return SpellEffectType.AETaunt;
		case 207:
			return SpellEffectType.FleshToBone;
		case 208:
			return SpellEffectType.PurgePoison;
		case 209:
			return SpellEffectType.DispelBeneficial;
		case 210:
			return SpellEffectType.PetShield;
		case 211:
			return SpellEffectType.AEMelee;
		case 212:
			return SpellEffectType.FrenziedDevastation;
		case 213:
			return SpellEffectType.PetMaxHP;
		case 214:
			return SpellEffectType.MaxHPChange;
		case 215:
			return SpellEffectType.PetAvoidance;
		case 216:
			return SpellEffectType.Accuracy;
		case 217:
			return SpellEffectType.HeadShot;
		case 218:
			return SpellEffectType.PetCriticalHit;
		case 219:
			return SpellEffectType.SlayUndead;
		case 220:
			return SpellEffectType.SkillDamageAmount;
		case 221:
			return SpellEffectType.Packrat;
		case 222:
			return SpellEffectType.BlockBehind;
		case 223:
			return SpellEffectType.DoubleRiposte;
		case 224:
			return SpellEffectType.GiveDoubleRiposte;
		case 225:
			return SpellEffectType.GiveDoubleAttack;
		case 226:
			return SpellEffectType.TwoHandBash;
		case 227:
			return SpellEffectType.ReduceSkillTimer;
		case 228:
			return SpellEffectType.ReduceFallDamage;
		case 229:
			return SpellEffectType.PersistantCasting;
		case 230:
			return SpellEffectType.ExtendedShielding;
		case 231:
			return SpellEffectType.StunBashChance;
		case 232:
			return SpellEffectType.DivineSave;
		case 233:
			return SpellEffectType.Metabolism;
		case 234:
			return SpellEffectType.ReduceApplyPoisonTime;
		case 235:
			return SpellEffectType.ChannelChanceSpells;
		case 236:
			return SpellEffectType.FreePet;
		case 237:
			return SpellEffectType.GivePetGroupTarget;
		case 238:
			return SpellEffectType.IllusionPersistence;
		case 239:
			return SpellEffectType.FeignedCastOnChance;
		case 240:
			return SpellEffectType.StringUnbreakable;
		case 241:
			return SpellEffectType.ImprovedReclaimEnergy;
		case 242:
			return SpellEffectType.IncreaseChanceMemwipe;
		case 243:
			return SpellEffectType.CharmBreakChance;
		case 244:
			return SpellEffectType.RootBreakChance;
		case 245:
			return SpellEffectType.TrapCircumvention;
		case 246:
			return SpellEffectType.SetBreathLevel;
		case 247:
			return SpellEffectType.RaiseSkillCap;
		case 248:
			return SpellEffectType.SecondaryForte;
		case 249:
			return SpellEffectType.SecondaryDmgInc;
		case 250:
			return SpellEffectType.SpellProcChance;
		case 251:
			return SpellEffectType.ConsumeProjectile;
		case 252:
			return SpellEffectType.FrontalBackstabChance;
		case 253:
			return SpellEffectType.FrontalBackstabMinDmg;
		case 254:
			return SpellEffectType.Blank;
		case 255:
			return SpellEffectType.ShieldDuration;
		case 256:
			return SpellEffectType.ShroudofStealth;
		case 257:
			return SpellEffectType.PetDiscipline;
		case 258:
			return SpellEffectType.TripleBackstab;
		case 259:
			return SpellEffectType.CombatStability;
		case 260:
			return SpellEffectType.AddSingingMod;
		case 261:
			return SpellEffectType.SongModCap;
		case 262:
			return SpellEffectType.RaiseStatCap;
		case 263:
			return SpellEffectType.TradeSkillMastery;
		case 264:
			return SpellEffectType.HastenedAASkill;
		case 265:
			return SpellEffectType.MasteryofPast;
		case 266:
			return SpellEffectType.ExtraAttackChance;
		case 267:
			return SpellEffectType.AddPetCommand;
		case 268:
			return SpellEffectType.ReduceTradeskillFail;
		case 269:
			return SpellEffectType.MaxBindWound;
		case 270:
			return SpellEffectType.BardSongRange;
		case 271:
			return SpellEffectType.BaseMovementSpeed;
		case 272:
			return SpellEffectType.CastingLevel2;
		case 273:
			return SpellEffectType.CriticalDoTChance;
		case 274:
			return SpellEffectType.CriticalHealChance;
		case 275:
			return SpellEffectType.CriticalMend;
		case 276:
			return SpellEffectType.Ambidexterity;
		case 277:
			return SpellEffectType.UnfailingDivinity;
		case 278:
			return SpellEffectType.FinishingBlow;
		case 279:
			return SpellEffectType.Flurry;
		case 280:
			return SpellEffectType.PetFlurry;
		case 281:
			return SpellEffectType.FeignedMinion;
		case 282:
			return SpellEffectType.ImprovedBindWound;
		case 283:
			return SpellEffectType.DoubleSpecialAttack;
		case 284:
			return SpellEffectType.LoHSetHeal;
		case 285:
			return SpellEffectType.NimbleEvasion;
		case 286:
			return SpellEffectType.FcDamageAmt;
		case 287:
			return SpellEffectType.SpellDurationIncByTic;
		case 288:
			return SpellEffectType.SkillAttackProc;
		case 289:
			return SpellEffectType.CastOnFadeEffect;
		case 290:
			return SpellEffectType.IncreaseRunSpeedCap;
		case 291:
			return SpellEffectType.Purify;
		case 292:
			return SpellEffectType.StrikeThrough2;
		case 293:
			return SpellEffectType.FrontalStunResist;
		case 294:
			return SpellEffectType.CriticalSpellChance;
		case 295:
			return SpellEffectType.ReduceTimerSpecial;
		case 296:
			return SpellEffectType.FcSpellVulnerability;
		case 297:
			return SpellEffectType.FcDamageAmtIncoming;
		case 298:
			return SpellEffectType.ChangeHeight;
		case 299:
			return SpellEffectType.WakeTheDead;
		case 300:
			return SpellEffectType.Doppelganger;
		case 301:
			return SpellEffectType.ArcheryDamageModifier;
		case 302:
			return SpellEffectType.FcDamagePctCrit;
		case 303:
			return SpellEffectType.FcDamageAmtCrit;
		case 304:
			return SpellEffectType.OffhandRiposteFail;
		case 305:
			return SpellEffectType.MitigateDamageShield;
		case 306:
			return SpellEffectType.ArmyOfTheDead;
		case 307:
			return SpellEffectType.Appraisal;
		case 308:
			return SpellEffectType.SuspendMinion;
		case 309:
			return SpellEffectType.GateCastersBindpoint;
		case 310:
			return SpellEffectType.ReduceReuseTimer;
		case 311:
			return SpellEffectType.LimitCombatSkills;
		case 312:
			return SpellEffectType.Sanctuary;
		case 313:
			return SpellEffectType.ForageAdditionalItems;
		case 314:
			return SpellEffectType.Invisibility2;
		case 315:
			return SpellEffectType.InvisVsUndead2;
		case 316:
			return SpellEffectType.ImprovedInvisAnimals;
		case 317:
			return SpellEffectType.ItemHPRegenCapIncrease;
		case 318:
			return SpellEffectType.ItemManaRegenCapIncrease;
		case 319:
			return SpellEffectType.CriticalHealOverTime;
		case 320:
			return SpellEffectType.ShieldBlock;
		case 321:
			return SpellEffectType.ReduceHate;
		case 322:
			return SpellEffectType.GateToHomeCity;
		case 323:
			return SpellEffectType.DefensiveProc;
		case 324:
			return SpellEffectType.HPToMana;
		case 325:
			return SpellEffectType.NoBreakAESneak;
		case 326:
			return SpellEffectType.SpellSlotIncrease;
		case 327:
			return SpellEffectType.MysticalAttune;
		case 328:
			return SpellEffectType.DelayDeath;
		case 329:
			return SpellEffectType.ManaAbsorbPercentDamage;
		case 330:
			return SpellEffectType.CriticalDamageMob;
		case 331:
			return SpellEffectType.Salvage;
		case 332:
			return SpellEffectType.SummonToCorpse;
		case 333:
			return SpellEffectType.CastOnRuneFadeEffect;
		case 334:
			return SpellEffectType.BardAEDot;
		case 335:
			return SpellEffectType.BlockNextSpellFocus;
		case 336:
			return SpellEffectType.IllusionaryTarget;
		case 337:
			return SpellEffectType.PercentXPIncrease;
		case 338:
			return SpellEffectType.SummonAndResAllCorpses;
		case 339:
			return SpellEffectType.TriggerOnCast;
		case 340:
			return SpellEffectType.SpellTrigger;
		case 341:
			return SpellEffectType.ItemAttackCapIncrease;
		case 342:
			return SpellEffectType.ImmuneFleeing;
		case 343:
			return SpellEffectType.InterruptCasting;
		case 344:
			return SpellEffectType.ChannelChanceItems;
		case 345:
			return SpellEffectType.AssassinateLevel;
		case 346:
			return SpellEffectType.HeadShotLevel;
		case 347:
			return SpellEffectType.DoubleRangedAttack;
		case 348:
			return SpellEffectType.LimitManaMin;
		case 349:
			return SpellEffectType.ShieldEquipDmgMod;
		case 350:
			return SpellEffectType.ManaBurn;
		case 351:
			return SpellEffectType.PersistentEffect;
		case 352:
			return SpellEffectType.IncreaseTrapCount;
		case 353:
			return SpellEffectType.AdditionalAura;
		case 354:
			return SpellEffectType.DeactivateAllTraps;
		case 355:
			return SpellEffectType.LearnTrap;
		case 356:
			return SpellEffectType.ChangeTriggerType;
		case 357:
			return SpellEffectType.FcMute;
		case 358:
			return SpellEffectType.CurrentManaOnce;
		case 359:
			return SpellEffectType.PassiveSenseTrap;
		case 360:
			return SpellEffectType.ProcOnKillShot;
		case 361:
			return SpellEffectType.SpellOnDeath;
		case 362:
			return SpellEffectType.PotionBeltSlots;
		case 363:
			return SpellEffectType.BandolierSlots;
		case 364:
			return SpellEffectType.TripleAttackChance;
		case 365:
			return SpellEffectType.ProcOnSpellKillShot;
		case 366:
			return SpellEffectType.GroupShielding;
		case 367:
			return SpellEffectType.SetBodyType;
		case 368:
			return SpellEffectType.FactionMod;
		case 369:
			return SpellEffectType.CorruptionCounter;
		case 370:
			return SpellEffectType.ResistCorruption;
		case 371:
			return SpellEffectType.AttackSpeed4;
		case 372:
			return SpellEffectType.ForageSkill;
		case 373:
			return SpellEffectType.CastOnFadeEffectAlways;
		case 374:
			return SpellEffectType.ApplyEffect;
		case 375:
			return SpellEffectType.DotCritDmgIncrease;
		case 376:
			return SpellEffectType.Fling;
		case 377:
			return SpellEffectType.CastOnFadeEffectNPC;
		case 378:
			return SpellEffectType.SpellEffectResistChance;
		case 379:
			return SpellEffectType.ShadowStepDirectional;
		case 380:
			return SpellEffectType.Knockdown;
		case 381:
			return SpellEffectType.KnockTowardCaster;
		case 382:
			return SpellEffectType.NegateSpellEffect;
		case 383:
			return SpellEffectType.SympatheticProc;
		case 384:
			return SpellEffectType.Leap;
		case 385:
			return SpellEffectType.LimitSpellGroup;
		case 386:
			return SpellEffectType.CastOnCurer;
		case 387:
			return SpellEffectType.CastOnCure;
		case 388:
			return SpellEffectType.SummonCorpseZone;
		case 389:
			return SpellEffectType.FcTimerRefresh;
		case 390:
			return SpellEffectType.FcTimerLockout;
		case 391:
			return SpellEffectType.LimitManaMax;
		case 392:
			return SpellEffectType.FcHealAmt;
		case 393:
			return SpellEffectType.FcHealPctIncoming;
		case 394:
			return SpellEffectType.FcHealAmtIncoming;
		case 395:
			return SpellEffectType.FcHealPctCritIncoming;
		case 396:
			return SpellEffectType.FcHealAmtCrit;
		case 397:
			return SpellEffectType.PetMeleeMitigation;
		case 398:
			return SpellEffectType.SwarmPetDuration;
		case 399:
			return SpellEffectType.FcTwincast;
		case 400:
			return SpellEffectType.HealGroupFromMana;
		case 401:
			return SpellEffectType.ManaDrainWithDmg;
		case 402:
			return SpellEffectType.EndDrainWithDmg;
		case 403:
			return SpellEffectType.LimitSpellClass;
		case 404:
			return SpellEffectType.LimitSpellSubclass;
		case 405:
			return SpellEffectType.TwoHandBluntBlock;
		case 406:
			return SpellEffectType.CastonNumHitFade;
		case 407:
			return SpellEffectType.CastonFocusEffect;
		case 408:
			return SpellEffectType.LimitHPPercent;
		case 409:
			return SpellEffectType.LimitManaPercent;
		case 410:
			return SpellEffectType.LimitEndPercent;
		case 411:
			return SpellEffectType.LimitClass;
		case 412:
			return SpellEffectType.LimitRace;
		case 413:
			return SpellEffectType.FcBaseEffects;
		case 414:
			return SpellEffectType.LimitCastingSkill;
		case 415:
			return SpellEffectType.FFItemClass;
		case 416:
			return SpellEffectType.ACv2;
		case 417:
			return SpellEffectType.ManaRegen_v2;
		case 418:
			return SpellEffectType.SkillDamageAmount2;
		case 419:
			return SpellEffectType.AddMeleeProc;
		case 420:
			return SpellEffectType.FcLimitUse;
		case 421:
			return SpellEffectType.FcIncreaseNumHits;
		case 422:
			return SpellEffectType.LimitUseMin;
		case 423:
			return SpellEffectType.LimitUseType;
		case 424:
			return SpellEffectType.GravityEffect;
		case 425:
			return SpellEffectType.Display;
		case 426:
			return SpellEffectType.IncreaseExtTargetWindow;
		case 427:
			return SpellEffectType.SkillProc;
		case 428:
			return SpellEffectType.LimitToSkill;
		case 429:
			return SpellEffectType.SkillProcSuccess;
		case 430:
			return SpellEffectType.PostEffect;
		case 431:
			return SpellEffectType.PostEffectData;
		case 432:
			return SpellEffectType.ExpandMaxActiveTrophyBen;
		case 433:
			return SpellEffectType.CriticalDotDecay;
		case 434:
			return SpellEffectType.CriticalHealDecay;
		case 435:
			return SpellEffectType.CriticalRegenDecay;
		case 436:
			return SpellEffectType.BeneficialCountDownHold;
		case 437:
			return SpellEffectType.TeleporttoAnchor;
		case 438:
			return SpellEffectType.TranslocatetoAnchor;
		case 439:
			return SpellEffectType.Assassinate;
		case 440:
			return SpellEffectType.FinishingBlowLvl;
		case 441:
			return SpellEffectType.DistanceRemoval;
		case 442:
			return SpellEffectType.TriggerOnReqTarget;
		case 443:
			return SpellEffectType.TriggerOnReqCaster;
		case 444:
			return SpellEffectType.ImprovedTaunt;
		case 445:
			return SpellEffectType.AddMercSlot;
		case 446:
			return SpellEffectType.AStacker;
		case 447:
			return SpellEffectType.BStacker;
		case 448:
			return SpellEffectType.CStacker;
		case 449:
			return SpellEffectType.DStacker;
		case 450:
			return SpellEffectType.MitigateDotDamage;
		case 451:
			return SpellEffectType.MeleeThresholdGuard;
		case 452:
			return SpellEffectType.SpellThresholdGuard;
		case 453:
			return SpellEffectType.TriggerMeleeThreshold;
		case 454:
			return SpellEffectType.TriggerSpellThreshold;
		case 455:
			return SpellEffectType.AddHatePct;
		case 456:
			return SpellEffectType.AddHateOverTimePct;
		case 457:
			return SpellEffectType.ResourceTap;
		case 458:
			return SpellEffectType.FactionModPct;
		case 459:
			return SpellEffectType.DamageModifier2;
		case 460:
			return SpellEffectType.Ff_Override_NotFocusable;
		case 461:
			return SpellEffectType.ImprovedDamage2;
		case 462:
			return SpellEffectType.FcDamageAmt2;
		case 463:
			return SpellEffectType.Shield_Target;
		case 464:
			return SpellEffectType.PC_Pet_Rampage;
		case 465:
			return SpellEffectType.PC_Pet_AE_Rampage;
		case 466:
			return SpellEffectType.PC_Pet_Flurry_Chance;
		case 467:
			return SpellEffectType.DS_Mitigation_Amount;
		case 468:
			return SpellEffectType.DS_Mitigation_Percentage;
		case 469:
			return SpellEffectType.Chance_Best_in_Spell_Grp;
		case 470:
			return SpellEffectType.SE_Trigger_Best_in_Spell_Grp;
		case 471:
			return SpellEffectType.Double_Melee_Round;
		default:
			return SpellEffectType.ERROR;
		}
	}

	public static int getDurationFromSpell(SoliniaSpell soliniaSpell) {
		if (soliniaSpell.isBuffSpell()) {
			return soliniaSpell.getBuffduration();
		}
		return 0;
	}


	// Used for one off patching, added in /solinia command for console sender
	public static void Patcher() {
		
	}

	public static boolean isLivingEntityNPC(LivingEntity livingentity) {
		String metaid = "";
		for (MetadataValue val : livingentity.getMetadata("mobname")) {
			metaid = val.asString();
		}

		if (metaid.equals(""))
			return false;

		if (!metaid.contains("NPCID_"))
			return false;

		return true;
	}

	public static SpellResistType getSpellResistType(Integer resisttype) {
		switch (resisttype) {
		case 0:
			return SpellResistType.RESIST_NONE;
		case 1:
			return SpellResistType.RESIST_MAGIC;
		case 2:
			return SpellResistType.RESIST_FIRE;
		case 3:
			return SpellResistType.RESIST_COLD;
		case 4:
			return SpellResistType.RESIST_POISON;
		case 5:
			return SpellResistType.RESIST_DISEASE;
		case 6:
			return SpellResistType.RESIST_CHROMATIC;
		case 7:
			return SpellResistType.RESIST_PRISMATIC;
		case 8:
			return SpellResistType.RESIST_PHYSICAL;
		case 9:
			return SpellResistType.RESIST_CORRUPTION;
		default:
			return SpellResistType.RESIST_NONE;
		}
	}

	// Graphical effects
	public static SpellEffectIndex getSpellEffectIndex(int sai) {
		switch (sai) {
		case -1:
			return SpellEffectIndex.Summon_Mount_Unclass;
		case 0:
			return SpellEffectIndex.Direct_Damage;
		case 1:
			return SpellEffectIndex.Heal_Cure;
		case 2:
			return SpellEffectIndex.AC_Buff;
		case 3:
			return SpellEffectIndex.AE_Damage;
		case 4:
			return SpellEffectIndex.Summon;
		case 5:
			return SpellEffectIndex.Sight;
		case 6:
			return SpellEffectIndex.Mana_Regen_Resist_Song;
		case 7:
			return SpellEffectIndex.Stat_Buff;
		case 9:
			return SpellEffectIndex.Vanish;
		case 10:
			return SpellEffectIndex.Appearance;
		case 11:
			return SpellEffectIndex.Enchanter_Pet;
		case 12:
			return SpellEffectIndex.Calm;
		case 13:
			return SpellEffectIndex.Fear;
		case 14:
			return SpellEffectIndex.Dispell_Sight;
		case 15:
			return SpellEffectIndex.Stun;
		case 16:
			return SpellEffectIndex.Haste_Runspeed;
		case 17:
			return SpellEffectIndex.Combat_Slow;
		case 18:
			return SpellEffectIndex.Damage_Shield;
		case 19:
			return SpellEffectIndex.Cannibalize_Weapon_Proc;
		case 20:
			return SpellEffectIndex.Weaken;
		case 21:
			return SpellEffectIndex.Banish;
		case 22:
			return SpellEffectIndex.Blind_Poison;
		case 23:
			return SpellEffectIndex.Cold_DD;
		case 24:
			return SpellEffectIndex.Poison_Disease_DD;
		case 25:
			return SpellEffectIndex.Fire_DD;
		case 27:
			return SpellEffectIndex.Memory_Blur;
		case 28:
			return SpellEffectIndex.Gravity_Fling;
		case 29:
			return SpellEffectIndex.Suffocate;
		case 30:
			return SpellEffectIndex.Lifetap_Over_Time;
		case 31:
			return SpellEffectIndex.Fire_AE;
		case 33:
			return SpellEffectIndex.Cold_AE;
		case 34:
			return SpellEffectIndex.Poison_Disease_AE;
		case 40:
			return SpellEffectIndex.Teleport;
		case 41:
			return SpellEffectIndex.Direct_Damage_Song;
		case 42:
			return SpellEffectIndex.Combat_Buff_Song;
		case 43:
			return SpellEffectIndex.Calm_Song;
		case 45:
			return SpellEffectIndex.Firework;
		case 46:
			return SpellEffectIndex.Firework_AE;
		case 47:
			return SpellEffectIndex.Weather_Rocket;
		case 50:
			return SpellEffectIndex.Convert_Vitals;
		case 60:
			return SpellEffectIndex.NPC_Special_60;
		case 61:
			return SpellEffectIndex.NPC_Special_61;
		case 62:
			return SpellEffectIndex.NPC_Special_62;
		case 63:
			return SpellEffectIndex.NPC_Special_63;
		case 70:
			return SpellEffectIndex.NPC_Special_70;
		case 71:
			return SpellEffectIndex.NPC_Special_71;
		case 80:
			return SpellEffectIndex.NPC_Special_80;
		case 88:
			return SpellEffectIndex.Trap_Lock;
		}

		return null;
	}

	public static SpellEffectType getSpellEffectTypeFromResistType(SpellResistType type) {
		switch(type)
		{
			case RESIST_COLD:
				return SpellEffectType.ResistCold;
			case RESIST_FIRE:
				return SpellEffectType.ResistFire;
			case RESIST_POISON:
				return SpellEffectType.ResistPoison;
			case RESIST_DISEASE:
				return SpellEffectType.ResistDisease;
			case RESIST_MAGIC:
				return SpellEffectType.ResistMagic;
			case RESIST_CORRUPTION:
				return SpellEffectType.ResistCorruption;
			case RESIST_NONE:
				return null;
			default:
				return null;
				
		}
	}
}
