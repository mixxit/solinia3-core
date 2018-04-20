package com.solinia.solinia.Adapters;

import org.bukkit.Chunk;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Models.SoliniaChunk;

public interface SoliniaChunkAdapter {
	public static SoliniaChunk Adapt(Chunk chunk) throws CoreStateInitException
	{
		return SoliniaWorldAdapter.Adapt(chunk.getWorld().getName().toUpperCase()).getChunk(chunk);
	}
}
