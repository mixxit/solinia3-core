package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaChunkCreationException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Models.SoliniaWorld;

public class SoliniaChunkFactory {
	public static SoliniaChunk Create(String worldName, int x, int z) throws CoreStateInitException, SoliniaChunkCreationException {
		SoliniaWorld world = StateManager.getInstance().getConfigurationManager().getWorld(worldName.toUpperCase());
		if (world == null)
			throw new SoliniaChunkCreationException("World does not exist");
		
		SoliniaChunk entry = new SoliniaChunk();
		entry.setChunkX(x);
		entry.setChunkZ(z);
		entry.setSoliniaWorldName(worldName);
		world.setChunk(x + "_" + z, entry);
		return entry;
	}
}
