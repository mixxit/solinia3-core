package com.solinia.solinia.Factories;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.gson.Gson;
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
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Utils.PlayerUtils;

public class SoliniaPlayerFactory {

	public static ISoliniaPlayer CreatePlayer(Player player) throws CoreStateInitException {
		// A player is different to a players entity
		ISoliniaPlayer soliniaPlayer = new SoliniaPlayer();
		soliniaPlayer.setUUID(player.getUniqueId());
		soliniaPlayer.setCharacterId(UUID.randomUUID());

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
		
		StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(soliniaPlayer);
		StateManager.getInstance().getPlayerManager().setActiveCharacter(player,soliniaPlayer.getCharacterId());
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

	public static SoliniaPlayer CreatePlayerCopy(SoliniaPlayer solPlayerToCopy, UUID uniqueId) {
		try
		{
			Gson gson= new Gson();
			String tmp = gson.toJson(solPlayerToCopy);
			SoliniaPlayer obj = gson.fromJson(tmp,SoliniaPlayer.class);
			obj.setUUID(uniqueId);
			obj.setCharacterId(UUID.randomUUID());
			obj.setCharacterFellowshipId(0);
			
			String forename = getRandomNames(5, 1)[0];
			String lastname = "";
			try {
				while (StateManager.getInstance().getPlayerManager().IsNewNameValid(forename, lastname) == false) {
					forename = getRandomNames(5, 1)[0];
				}
				
				obj.setForename(forename);
				obj.setLastname(lastname);
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			

			StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(obj);

			System.out.println("New Character Copied: " + obj.getCharacterId() + " - " + obj.getFullName());
			return obj;

		} catch (CoreStateInitException e)
		{
			
		}
		
		return null;
	}
}
