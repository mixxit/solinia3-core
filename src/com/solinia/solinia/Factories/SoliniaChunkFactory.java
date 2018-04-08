package com.solinia.solinia.Factories;

import org.bukkit.Chunk;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaChunkCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Models.SoliniaWorld;
import com.sun.javafx.geom.Vec2d;

public class SoliniaChunkFactory {
	public static SoliniaChunk Create(Chunk bukkitChunk) throws CoreStateInitException, SoliniaChunkCreationException {
		SoliniaWorld world = StateManager.getInstance().getConfigurationManager().getWorld(bukkitChunk.getWorld().getName().toUpperCase());
		if (world == null)
			throw new SoliniaChunkCreationException("World does not exist");
		
		SoliniaChunk entry = new SoliniaChunk();
		entry.setChunkX(bukkitChunk.getX());
		entry.setChunkZ(bukkitChunk.getZ());
		entry.setSoliniaWorldName(bukkitChunk.getWorld().getName().toUpperCase());
		world.setChunk(bukkitChunk.getX() + "_" + bukkitChunk.getZ(), entry);
		return entry;
	}
}
