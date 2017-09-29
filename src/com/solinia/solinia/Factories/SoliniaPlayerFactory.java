package com.solinia.solinia.Factories;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayer;

public class SoliniaPlayerFactory {

	public static ISoliniaPlayer CreatePlayer(Player player) throws CoreStateInitException {
		// A player is different to a players entity
		ISoliniaPlayer soliniaPlayer = new SoliniaPlayer();
		soliniaPlayer.setUUID(player.getUniqueId());

		String forename = getRandomNames(5, 1)[0];
		String lastname = "";
		try {
			while (StateManager.getInstance().getPlayerManager().IsNewNameValid(forename, lastname) == false) {
				forename = getRandomNames(5, 1)[0];
			}
			
			soliniaPlayer.setForename(forename);
			soliniaPlayer.setLastname(lastname);
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StateManager.getInstance().getPlayerManager().addPlayer(soliniaPlayer);
		soliniaPlayer = SoliniaPlayerAdapter.Adapt(player);
		soliniaPlayer.setExperience(0d);
		soliniaPlayer.setAAExperience(0d);
		soliniaPlayer.setMana(0);
		
		return soliniaPlayer;
	}

	public static String[] getRandomNames(final int characterLength, final int generateSize) {
	    HashSet<String> list = new HashSet<String>();
	    for (int i = 0; i < generateSize; ++i) {
	        String name = null;
	        do {
	            name = org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(
	                    org.apache.commons.lang.math.RandomUtils.nextInt(characterLength - 1) + 1);
	        }
	        while(list.contains(name));
	        list.add(name);
	    }
	    return list.toArray(new String[]{});
	}
}
