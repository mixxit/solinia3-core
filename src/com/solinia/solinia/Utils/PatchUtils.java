package com.solinia.solinia.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.PlayerState;

public class PatchUtils {
	// Used for one off patching, added in /solinia patch command for console sender
	public static void Patcher() {
		try
		{
			for (Fellowship entry : StateManager.getInstance().getConfigurationManager().getFellowships())
			{
				
			}
			
			
		} catch (CoreStateInitException e)
		{
			
		}
		
	}
}
