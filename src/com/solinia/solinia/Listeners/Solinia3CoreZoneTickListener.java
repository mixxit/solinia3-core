package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerZoneTickEvent;
import com.solinia.solinia.Events.ZoneTickEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class Solinia3CoreZoneTickListener implements Listener {
	Solinia3CorePlugin plugin;

	public Solinia3CoreZoneTickListener(Solinia3CorePlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnZoneTick(ZoneTickEvent event) {
		if (event.isCancelled())
			return;
		
		try
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer == null)
					continue;
				
				if (!solPlayer.isInZone(event.getZone()))
					continue;
				
				PlayerZoneTickEvent soliniaevent = new PlayerZoneTickEvent(solPlayer,event.getZone());
				Bukkit.getPluginManager().callEvent(soliniaevent);
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}

}
