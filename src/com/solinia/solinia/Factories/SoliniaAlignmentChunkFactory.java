package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaChunkCreationException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Models.SoliniaAlignmentChunk;

public class SoliniaAlignmentChunkFactory {
	public static SoliniaAlignmentChunk Create(ISoliniaAlignment alignment, String worldName, int chunkX, int chunkZ) throws CoreStateInitException, SoliniaChunkCreationException {
		SoliniaAlignmentChunk entry = new SoliniaAlignmentChunk();
		entry.setChunkX(chunkX);
		entry.setChunkZ(chunkZ);
		entry.setAlignmentId(alignment.getId());
		entry.setSoliniaWorldName(worldName.toUpperCase());
		alignment.setChunk(worldName.toUpperCase() + "_" + chunkX + "_" + chunkZ, entry);
		return entry;
	}
}
