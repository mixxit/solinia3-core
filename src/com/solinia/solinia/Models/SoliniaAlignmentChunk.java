package com.solinia.solinia.Models;

import org.bukkit.World;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaAlignmentChunk {
	private int alignmentId = 0;
	private boolean tradePost = false;
	private int chunkX;
	private int chunkZ;
	private String soliniaWorldName;

	public int getChunkX() {
		return chunkX;
	}
	public void setChunkX(int chunkX) {
		this.chunkX = chunkX;
	}
	public int getChunkZ() {
		return chunkZ;
	}
	public void setChunkZ(int chunkZ) {
		this.chunkZ = chunkZ;
	}
	
	public String getSoliniaWorldName() {
		return soliniaWorldName;
	}
	public void setSoliniaWorldName(String soliniaWorldName) {
		this.soliniaWorldName = soliniaWorldName;
	}
	
	public int getAlignmentId() {
		return alignmentId;
	}
	public void setAlignmentId(int alignmentId) {
		this.alignmentId = alignmentId;
	}
	public int getCoinGenerated() {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean isTradePost() {
		return tradePost;
	}
	public void setTradePost(boolean tradePost) {
		this.tradePost = tradePost;
	}
	
	public SoliniaChunk getSoliniaChunk()
	{
		try {
			for (SoliniaWorld world : StateManager.getInstance().getConfigurationManager().getWorlds())
			{
				if (!world.getName().toUpperCase().equals(getSoliniaWorldName().toUpperCase()))
					continue;
				
				return world.getChunk(getChunkX(), getChunkZ());
			}
		} catch (CoreStateInitException e) {
			
		}
		
		return null;
	}
}
