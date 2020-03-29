package com.solinia.solinia.Utils;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for (ISoliniaPlayer player : StateManager.getInstance().getConfigurationManager().getCharacters())
			{
				System.out.println("Updated active character");
			}
			
			
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
}
