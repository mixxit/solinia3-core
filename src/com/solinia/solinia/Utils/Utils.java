package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillReward;

import net.md_5.bungee.api.ChatColor;

public class Utils {

	public static int getLevelFromExperience(Double experience) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double level = experience / levelfactor / racemodifier / classmodifier;
		level = java.lang.Math.pow(level, 0.25) + 1;
		return (int) java.lang.Math.floor(level);
	}
	
	public static boolean RandomChance(int minmum)
	{
		Random r = new Random();
		int randomInt = r.nextInt(100) + 1;
		if (randomInt > minmum) {
			return true;
		}
		
		return false;
	}
	
	public static int RandomBetween(int minnumber, int maxnumber)
	{
		Random r = new Random();
		return r.nextInt((maxnumber - minnumber) + 1) + minnumber;
	}
	
	public static SkillReward getSkillForMaterial(String materialstring)
	{
		SkillReward reward = null;
		
		int xp = 0;
		String skill = "";
		
		switch(materialstring)
    	{
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
		
		if (xp > 0 && !skill.equals(""))
		{
			reward = new SkillReward(skill,xp);
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
		
		if (profession != null)
		{
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
	
	public static int getMaxLevel()
	{
		return 31;
	}

	public static Double getExperienceRewardAverageForLevel(int level) {
		Double experience = (Math.pow(level, 2) * 10) * getMaxLevel() - 1;
		experience = experience / 2;
		if (experience < 1)
		{
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

		if (soliniaClass.getName().equals("CLERIC") || soliniaClass.getName().equals("DRUID") || soliniaClass.getName().equals("SHAMAN"))
			percentagemodifier = 120;

		return percentagemodifier;
	}

	public static int getSkillCap(ISoliniaPlayer player, String skillname) {

		// If the skill being queried happens to be a race name, the cap for language is always 100
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

		// TODO - Move all these skill cap bonuses to the race configuration classes
		
		if (skillname.equals("SLASHING")) {
			if (profession != null)
			if ((profession.getName().toUpperCase().equals("RANGER") || profession.getName().toUpperCase().equals("PALADIN")
					|| profession.getName().toUpperCase().equals("WARRIOR") || profession.getName().toUpperCase().equals("SHADOWKNIGHT")
					|| profession.getName().toUpperCase().equals("HUNTER") || profession.getName().toUpperCase().equals("KNIGHT"))) {
				int cap = (int) ((5 * player.getLevel()) + 5);
				return cap;
			}
		}

		if (skillname.equals("CRUSHING")) {
			if (profession != null)
			if ((profession.getName().toUpperCase().equals("RANGER") || profession.getName().toUpperCase().equals("PALADIN")
					|| profession.getName().toUpperCase().equals("WARRIOR") || profession.getName().toUpperCase().equals("SHADOWKNIGHT")
					|| profession.getName().toUpperCase().equals("MONK") || profession.getName().toUpperCase().equals("HUNTER")
					|| profession.getName().toUpperCase().equals("KNIGHT"))) {
				int cap = (int) ((5 * player.getLevel()) + 5);
				return cap;
			}
		}

		if (skillname.equals("ARCHERY")) {
			if (profession != null)
			if ((profession.getName().toUpperCase().equals("RANGER") || profession.getName().toUpperCase().equals("HUNTER"))) {
				int cap = (int) ((5 * player.getLevel()) + 5);
				return cap;
			}
		}

		if (skillname.equals("MEDITATION")) {
			if (profession != null)
			if ((profession.getName().toUpperCase().equals("DRUID") || profession.getName().toUpperCase().equals("WIZARD")
					|| profession.getName().toUpperCase().equals("MAGICIAN") || profession.getName().toUpperCase().equals("NECROMANCER")
					|| profession.getName().toUpperCase().equals("ENCHANTER") || profession.getName().toUpperCase().equals("MONK")
					|| profession.getName().toUpperCase().equals("ARCANIST") || profession.getName().toUpperCase().equals("EXARCH"))) {
				int cap = (int) ((5 * player.getLevel()) + 5);
				return cap;
			}
		}

		int cap = (int) ((2 * player.getLevel()) + 2);
		return cap;
	}

	public static Enchantment getEnchantmentFromEnchantmentName(String name) throws Exception
	{
		switch (name)
		{
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
	
	public static <T> T getRandomItemFromList(List<T> list)
	{
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	public static void checkArmourEquip(ISoliniaPlayer solplayer, PlayerInteractEvent event) {
		ItemStack itemstack = event.getItem();
    	if (itemstack == null)
    		return;
		if (!(CraftItemStack.asNMSCopy(itemstack).getItem() instanceof net.minecraft.server.v1_12_R1.ItemArmor))
		{
			return;
		}
		
    	if (itemstack.getEnchantmentLevel(Enchantment.OXYGEN) > 999 && !itemstack.getType().equals(Material.ENCHANTED_BOOK))
	    {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
				
				if (soliniaitem.getAllowedClassNames().size() == 0)
	    			return;
				
				if (solplayer.getClassObj() == null)
				{
					event.setCancelled(true);
	    			event.getPlayer().updateInventory();
	    			event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
	    			return;
				}
					    		
	    		if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName()))
	    		{
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

		try
		{
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
		} catch (Exception e)
		{
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

	/*
	// returns map of class with level
	public static Map<String, Integer> getLegacyClassesFromSpellData(ISoliniaSpell spell) {
		Map<String, Integer> returnClasses = new HashMap<String, Integer>();
		if (spell.getClasses1() != 254 && spell.getClasses1() != 255)
			returnClasses.put("WARRIOR", spell.getClasses1());

		if (spell.getClasses2() != 254 && spell.getClasses2() != 255)
			returnClasses.put("CLERIC", spell.getClasses2());

		if (spell.getClasses3() != 254 && spell.getClasses3() != 255)
			returnClasses.put("PALADIN", spell.getClasses3());

		if (spell.getClasses4() != 254 && spell.getClasses4() != 255)
			returnClasses.put("RANGER", spell.getClasses4());

		if (spell.getClasses5() != 254 && spell.getClasses5() != 255)
			returnClasses.put("SHADOWKNIGHT", spell.getClasses5());

		if (spell.getClasses6() != 254 && spell.getClasses6() != 255)
			returnClasses.put("DRUID", spell.getClasses6());

		if (spell.getClasses7() != 254 && spell.getClasses7() != 255)
			returnClasses.put("MONK", spell.getClasses7());

		if (spell.getClasses8() != 254 && spell.getClasses8() != 255)
			returnClasses.put("BARD", spell.getClasses8());

		if (spell.getClasses9() != 254 && spell.getClasses9() != 255)
			returnClasses.put("ROGUE", spell.getClasses9());

		if (spell.getClasses10() != 254 && spell.getClasses10() != 255)
			returnClasses.put("SHAMAN", spell.getClasses10());

		if (spell.getClasses11() != 254 && spell.getClasses11() != 255)
			returnClasses.put("NECROMANCER", spell.getClasses11());

		if (spell.getClasses12() != 254 && spell.getClasses12() != 255)
			returnClasses.put("WIZARD", spell.getClasses12());

		if (spell.getClasses13() != 254 && spell.getClasses13() != 255)
			returnClasses.put("MAGICIAN", spell.getClasses13());

		if (spell.getClasses14() != 254 && spell.getClasses14() != 255)
			returnClasses.put("ENCHANTER", spell.getClasses14());

		if (spell.getClasses15() != 254 && spell.getClasses15() != 255)
			returnClasses.put("BEASTLORD", spell.getClasses15());

		if (spell.getClasses16() != 254 && spell.getClasses16() != 255)
			returnClasses.put("BERSERKER", spell.getClasses16());

		
		return returnClasses;
	}
	*/
}
