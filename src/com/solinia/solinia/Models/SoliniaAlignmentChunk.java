package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Managers.UniversalTemporarySoliniaLootTable;

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
	
	public List<UniversalMerchantEntry> getUniversalMerchantEntries(SoliniaAlignmentChunk alignmentChunk) {
		List<UniversalMerchantEntry> entries = new ArrayList<UniversalMerchantEntry>();
		List<UniversalTemporarySoliniaLootTable> lootTables = new ArrayList<UniversalTemporarySoliniaLootTable>();
		
		World bukkitworld = null;
		for(World tmpworld : Bukkit.getWorlds())
		{
			if(tmpworld.getName().toUpperCase().equals(alignmentChunk.getSoliniaWorldName().toUpperCase()))
				bukkitworld = tmpworld;
		}
		
		if (bukkitworld == null)
			return entries;
		
		try
		{
			ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment(getAlignmentId());
			for (SoliniaZone zone : alignment.getMaterialZones())
			{
				Location locationfrom = new Location(bukkitworld, alignmentChunk.getChunkX(), 0, alignmentChunk.getChunkZ());
				Location tmplocationto = new Location(bukkitworld, zone.getX(), 0, zone.getZ());
				Chunk chunk = bukkitworld.getChunkAt(tmplocationto);
				Location locationto = new Location(bukkitworld, chunk.getX(), 0, chunk.getZ());

				int distance = (int) locationfrom.distance(locationto);
				
				if (distance < 1)
					distance = 1;
				
				if (zone.getFishingLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getFishingLootTableId()),distance));
				}
				
				if (zone.getForagingLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getForagingLootTableId()),distance));
				}
				
				if (zone.getMiningLootTableId() > 0)
				{
					System.out.println("Adding mining zone distance: " + distance);
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getMiningLootTableId()),distance));
				}
				
				if (zone.getForestryLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getForestryLootTableId()),distance));
				}
			}
			
			// Always show global world drops
			for (SoliniaWorld world : StateManager.getInstance().getConfigurationManager().getWorlds())
			{
				int distance = 1;
				
				if (world.getFishingLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(world.getFishingLootTableId()),distance));
				}
				
				if (world.getForagingLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(world.getForagingLootTableId()),distance));
				}
				
				if (world.getMiningLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(world.getMiningLootTableId()),distance));
				}
				
				if (world.getForestryLootTableId() > 0)
				{
					lootTables.add(new UniversalTemporarySoliniaLootTable(StateManager.getInstance().getConfigurationManager().getLootTable(world.getForestryLootTableId()),distance));
				}
			}
			
			HashMap<Integer, Long> existingItemIds = new HashMap<Integer, Long>();
		
			for (UniversalTemporarySoliniaLootTable templootTable : lootTables)
			{
				for(ISoliniaLootTableEntry entry : templootTable.soliniaLootTable.getEntries())
				{
					ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager().getLootDrop(entry.getLootdropid());
					for(ISoliniaLootDropEntry lootdropentry : lootdrop.getEntries())
					{
						if (existingItemIds.containsKey(lootdropentry.getItemid()))
						{
							if (existingItemIds.get(lootdropentry.getItemid()) < templootTable.distance)
							{
								continue;
							}
							
							existingItemIds.remove(lootdropentry.getItemid());
						}
						
						ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(lootdropentry.getItemid());
						if (item == null)
							continue;
						
						if (item.isNeverDrop())
							continue;
						
						UniversalMerchantEntry ume = new UniversalMerchantEntry();
						ume.setItemid(lootdropentry.getItemid());
						ume.setTemporaryquantitylimit(64);
						
						ume.setCostMultiplier(templootTable.distance);
						entries.add(ume);
						existingItemIds.put(lootdropentry.getItemid(), templootTable.distance);
					}
				}
			}
			
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		}
		
		return entries;
	}
}
