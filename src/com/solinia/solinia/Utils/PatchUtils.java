package com.solinia.solinia.Utils;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for (ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters())
			{

				player.setPrimaryUUID(player.getCharacterId());
				player.setSecondaryUUID(player.getUUID());
				
				System.out.println("Set new primary/secondary ID to : " + player.getPrimaryUUID() + ":" + player.getSecondaryUUID());
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
