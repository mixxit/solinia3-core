package com.solinia.solinia.Utils;

import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class Utils {

	public static int GetLevelFromExperience(Double experience) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double level = experience / levelfactor / racemodifier / classmodifier;
		level = java.lang.Math.pow(level, 0.25) + 1;
		return (int) java.lang.Math.floor(level);
	}

	public static double GetStatMaxHP(ISoliniaPlayer player) {
		ISoliniaClass solprofession = player.getClassObj();
		
		String profession = "UNSKILLED";
		if (solprofession != null)
			profession = solprofession.getName().toUpperCase();
		
		double level = GetLevelFromExperience(player.getExperience());

		double levelmultiplier = 1;
		
		if (profession != null)
		{
			if (profession.equals("UNSKILLED"))
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
	
}
