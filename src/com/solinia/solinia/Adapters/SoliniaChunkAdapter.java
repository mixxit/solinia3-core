package com.solinia.solinia.Adapters;

import org.bukkit.Chunk;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Utils.Utils;

public interface SoliniaChunkAdapter {
	public static SoliniaChunk Adapt(Chunk chunk) throws SoliniaItemException, CoreStateInitException
	{
		return SoliniaWorldAdapter.Adapt(chunk.getWorld().getName().toUpperCase()).getChunk(chunk);
	}
}
