package com.solinia.solinia.Factories;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaPlayer;
import com.solinia.solinia.Utils.PlayerUtils;

public class SoliniaPlayerFactory {

	public static ISoliniaPlayer CreatePlayer(Player player, boolean main) throws CoreStateInitException {
		// A player is different to a players entity
		ISoliniaPlayer soliniaPlayer = new SoliniaPlayer();
		soliniaPlayer.setUUID(player.getUniqueId());
		soliniaPlayer.setMain(main);

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
		
		// give newbie items
		int loottableId = soliniaPlayer.getWorld().getPlayerStartLootTableId();
		if (loottableId > 0)
		{
			dropNewLootItems(player, loottableId);
		}
		
		if (main == true)
		{
			PlayerUtils.BroadcastPlayers("A new player has entered the world: " + player.getName() + " [" + soliniaPlayer.getFullNameWithTitle() + "]");
		}
		
		return soliniaPlayer;
	}

	private static void dropNewLootItems(Player player, int loottableId) {
		try
		{
		ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
				.getLootTable(loottableId);
		for (ISoliniaLootTableEntry le : StateManager.getInstance().getConfigurationManager()
				.getLootTable(loottable.getId()).getEntries()) {
			ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager()
					.getLootDrop(le.getLootdropid());
			for(ISoliniaLootDropEntry lde : ld.getEntries())
			{
				ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
				
				PlayerUtils.addToPlayersInventory(player, i.asItemStack());
			}
			
			}
		
		} catch (CoreStateInitException e)
		{
			
		}
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
