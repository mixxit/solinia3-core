package com.solinia.solinia.Models;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaChunk {
	private int chunkX;
	private int chunkZ;
	private String soliniaWorldName;
	private int alignmentId = 0;
	
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
	public int getTotalTradeRoutes() {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean isInZoneWithMaterials() {
		World world = getWorld();
		
		if (world == null)
			return false;
		
		return isInZoneWithMaterials(world);
	}
	
	public boolean isInZoneWithMaterials(World world) {
		if (!isInZone(world))
			return false;
		
		SoliniaZone zone = getFirstZone();
		
		if (zone == null)
			return false;
		
		if (zone.getMiningLootTableId() > 0 || zone.getFishingLootTableId() > 0 || zone.getForagingLootTableId() > 0 || zone.getForestryMinSkill() > 0)
			return true;
		
		return false;
	}
	
	public World getWorld()
	{
		for(World world : Bukkit.getWorlds())
		{
			if (!world.getName().toUpperCase().equals(soliniaWorldName.toUpperCase()))
				continue;
			
			return world;
		}
		
		return null;
	}
	
	private Location getFirstBlockLocation()
	{
		World world = getWorld();
		
		if (world == null)
			return null;

		return getFirstBlockLocation(world);
	}
	
	private Location getFirstBlockLocation(World world)
	{
		Chunk chunk = world.getChunkAt(getChunkX(), getChunkZ());
		return chunk.getBlock(0, 0, 0).getLocation();
	}
	
	private boolean isInZone(World world) {
		
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (getFirstBlockLocation(world).distance(
						new Location(world, zone.getX(), zone.getY(), zone.getZ())) < 500)
					return true;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	private SoliniaZone getFirstZone() {
		
		World world = getWorld();
		
		if (world == null)
			return null;
		
		return getFirstZone(world);
	}
	
	private SoliniaZone getFirstZone(World world) {
		
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (getFirstBlockLocation(world).distance(
						new Location(world, zone.getX(), zone.getY(), zone.getZ())) < 500)
					return zone;
			}
		} catch (CoreStateInitException e) {
			return null;
		}
		
		return null;
	}
	
	private boolean isInZone() {
		
		World world = getWorld();
		
		if (world == null)
			return false;
		
		return isInZone(world);
	}
	
}
