package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerValidatedModEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Utils.PlayerUtils;
import com.solinia.solinia.Utils.Utils;

public class PlayerValidatorModListener implements Listener {
	Solinia3CorePlugin plugin;

	public PlayerValidatorModListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
		
	@EventHandler
	public void onPlayerValidatedModEvent(PlayerValidatedModEvent event) {
		try {
			if (event.getPlayer() == null)
				return;
			
			ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(event.getPlayer());
			if (solPlayer == null)
				return;
			
			if (solPlayer.getClassObj() == null)
			{
			    try {
			    	PlayerUtils.sendCharCreation(event.getPlayer());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e) {

		}
		
	}
	
}
