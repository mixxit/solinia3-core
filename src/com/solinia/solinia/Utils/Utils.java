package com.solinia.solinia.Utils;

import java.util.List;

import org.bukkit.enchantments.Enchantment;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SkillReward;

public class Utils {

	public static int getLevelFromExperience(Double experience) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double level = experience / levelfactor / racemodifier / classmodifier;
		level = java.lang.Math.pow(level, 0.25) + 1;
		return (int) java.lang.Math.floor(level);
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
	
	
}
