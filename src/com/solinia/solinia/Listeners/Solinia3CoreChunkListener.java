package com.solinia.solinia.Listeners;

import org.bukkit.Bukkit;
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
		
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
					Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
						public void run() {
							try {
							final int chunkX = event.getChunk().getX();
							final int chunkZ = event.getChunk().getZ();
							final String world = event.getChunk().getWorld().getName();
							
							StateManager.getInstance().getEntityManager().removeAllAbandonedPetsInChunk(world,chunkX,chunkZ);
							StateManager.getInstance().getEntityManager().removeAllPetsInChunk(world,chunkX,chunkZ);
							} catch (CoreStateInitException e) {

							}
						}
					});
	}
	
	@EventHandler
	public void onChunkLoadEvent(ChunkLoadEvent event) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
				Bukkit.getPluginManager().getPlugin("Solinia3Core"), new Runnable() {
					public void run() {
						try {
						final int chunkX = event.getChunk().getX();
						final int chunkZ = event.getChunk().getZ();
						final String world = event.getChunk().getWorld().getName();
						
						StateManager.getInstance().getEntityManager().removeAllAbandonedPetsInChunk(world,chunkX,chunkZ);
						StateManager.getInstance().getEntityManager().removeAllPetsInChunk(world,chunkX,chunkZ);
						} catch (CoreStateInitException e) {

						}
					}
				});
		
	}

}
