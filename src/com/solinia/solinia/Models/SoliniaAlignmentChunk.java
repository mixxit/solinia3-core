package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
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
	
	public List<UniversalMerchantEntry> getUniversalMerchantEntries() {
		List<UniversalMerchantEntry> entries = new ArrayList<UniversalMerchantEntry>();
		
		List<ISoliniaLootTable> lootTables = new ArrayList<ISoliniaLootTable>();
		
		try
		{
			System.out.println("Generating Universal Merchant Entries for Alignment: " + getAlignmentId());
			ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment(getAlignmentId());
			for (SoliniaAlignmentChunk chunk : alignment.getMaterialChunks())
			{
				for(SoliniaZone zone : chunk.getSoliniaChunk().getZones())
				{
					if (zone.getFishingLootTableId() > 0)
					{
						lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getFishingLootTableId()));
					}
					
					if (zone.getForagingLootTableId() > 0)
					{
						lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getForagingLootTableId()));
					}
					
					if (zone.getMiningLootTableId() > 0)
					{
						lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getMiningLootTableId()));
					}
					
					if (zone.getForestryLootTableId() > 0)
					{
						lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(zone.getForestryLootTableId()));
					}
				}
			}
			
			// Always show global world drops
			for (SoliniaWorld world : StateManager.getInstance().getConfigurationManager().getWorlds())
			{
				if (world.getFishingLootTableId() > 0)
				{
					lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(world.getFishingLootTableId()));
				}
				
				if (world.getForagingLootTableId() > 0)
				{
					lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(world.getForagingLootTableId()));
				}
				
				if (world.getMiningLootTableId() > 0)
				{
					lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(world.getMiningLootTableId()));
				}
				
				if (world.getForestryLootTableId() > 0)
				{
					lootTables.add(StateManager.getInstance().getConfigurationManager().getLootTable(world.getForestryLootTableId()));
				}
			}
		
			for (ISoliniaLootTable lootTable : lootTables)
			{
				for(ISoliniaLootTableEntry entry : lootTable.getEntries())
				{
					ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager().getLootDrop(entry.getLootdropid());
					for(ISoliniaLootDropEntry lootdropentry : lootdrop.getEntries())
					{
						UniversalMerchantEntry ume = new UniversalMerchantEntry();
						ume.setItemid(lootdropentry.getItemid());
						ume.setTemporaryquantitylimit(64);
						entries.add(ume);
					}
				}
			}
			
		} catch (CoreStateInitException e)
		{
			
		}
		
		return entries;
	}
}
