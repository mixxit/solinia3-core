package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaRaceCreationException;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaRace;

public class SoliniaRaceFactory {
	public static ISoliniaRace CreateRace(String racename, int strength, int stamina, int agility, int dexterity, int wisdom,
			int intelligence, int charisma, boolean adminonly) throws CoreStateInitException, SoliniaRaceCreationException {
		
		if (StateManager.getInstance().getConfigurationManager().getRace(racename.toUpperCase()) != null)
			throw new SoliniaRaceCreationException("Race already exists");
		
		SoliniaRace race = new SoliniaRace();
		race.setId(StateManager.getInstance().getConfigurationManager().getNextRaceId());
		race.setName(racename.toUpperCase());
		race.setStrength(strength);
		race.setStamina(stamina);
		race.setAgility(agility);
		race.setDexterity(dexterity);
		race.setWisdom(wisdom);
		race.setIntelligence(intelligence);
		race.setCharisma(charisma);
		race.setAdmin(adminonly);
		return race;
	}
}
