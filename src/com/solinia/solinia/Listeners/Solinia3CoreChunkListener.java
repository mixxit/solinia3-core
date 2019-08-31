package com.solinia.solinia.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.solinia.solinia.Solinia3CorePlugin;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class Solinia3CoreChunkListener implements Listener {

	private Solinia3CorePlugin plugin;

	public Solinia3CoreChunkListener(Solinia3CorePlugin solinia3CorePlugin) {
		// TODO Auto-generated constructor stub
		plugin = solinia3CorePlugin;
	}
	
	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		try {
			StateManager.getInstance().getEntityManager().removeAllPetsInChunk(event.getChunk());
		} catch (CoreStateInitException e) {

		}
		
	}
	
	@EventHandler
	public void onChunkUnloadEvent(ChunkLoadEvent event) {
		try {
			StateManager.getInstance().getEntityManager().removeAllAbandonedPetsInChunk(event.getChunk());
		} catch (CoreStateInitException e) {

		}
		
	}

}
