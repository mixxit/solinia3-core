package com.solinia.solinia.Models;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaAlignmentChunkFactory;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaAlignment implements ISoliniaAlignment {

	private int id;
	private String name;
	private UUID emperor;
	private int coffers;
	private ConcurrentHashMap<String, SoliniaAlignmentChunk> chunks = new ConcurrentHashMap<String, SoliniaAlignmentChunk>(); 
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	@Override
	public UUID getEmperor() {
		return emperor;
	}

	@Override
	public void setEmperor(UUID emperor) {
		try {
			String playerName = StateManager.getInstance().getPlayerManager().getPlayerNameByUUID(emperor);
			if (playerName != null && !playerName.equals(""))
			{
				this.emperor = emperor;
				Utils.BroadcastPlayers(playerName + " has been declared Emperor! (" + getName() + ")");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getCoffers() {
		return coffers;
	}

	@Override
	public void setCoffers(int coffers) {
		this.coffers = coffers;
	}

	@Override
	public int getUpkeepCost() {
		return getTotalTradePosts() * 100;
	}
	
	@Override
	public int getTotalChunks() {
		return chunks.size();
	}

	@Override
	public int getTotalMaterialChunks() {
		int count = 0;
		for (Entry<String, SoliniaAlignmentChunk> chunk : getChunks().entrySet())
		{
			
			if (!chunk.getValue().getSoliniaChunk().isInZoneWithMaterials())
				continue;
			
			count++;
		}
		
		return count;
	}
	
	@Override
	public List<SoliniaAlignmentChunk> getMaterialChunks() {
		List<SoliniaAlignmentChunk> alignmentChunks = new ArrayList<SoliniaAlignmentChunk>();
		for (Entry<String, SoliniaAlignmentChunk> chunk : getChunks().entrySet())
		{
			if (chunk.getValue().getSoliniaChunk().isInZoneWithMaterials())
				alignmentChunks.add(chunk.getValue());
		}
		
		return alignmentChunks;
	}
	
	@Override
	public List<SoliniaZone> getMaterialZones() {
		List<SoliniaZone> alignmentZones = new ArrayList<SoliniaZone>();
		List<Integer> zoneIds = new ArrayList<Integer>();
		for(SoliniaAlignmentChunk chunk : getMaterialChunks())
		{
			for (SoliniaZone zone : chunk.getSoliniaChunk().getZones())
			{
				if (!zoneIds.contains(zone.getId()))
				{
					zoneIds.add(zone.getId());
					alignmentZones.add(zone);
				}
			}
		}
		
		return alignmentZones;
	}

	@Override
	public void sendAlignmentStats(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + getName().toUpperCase() + " Empire Territory Overview:" + ChatColor.RESET);
		sender.sendMessage("-----------------------------");
		
		sender.sendMessage("Coffers: " + ChatColor.GOLD + "$" + getCoffers() + ChatColor.RESET);
		sender.sendMessage("Upkeep Cost: " + ChatColor.GOLD + "$" + getUpkeepCost() + ChatColor.RESET);
		sender.sendMessage("Total Territories: " + ChatColor.GOLD + getTotalChunks() + ChatColor.RESET);
		sender.sendMessage("Total Material Zones: " + ChatColor.GOLD + getTotalMaterialChunks() + ChatColor.RESET);
		sender.sendMessage("Total Trade Posts: " + ChatColor.GOLD + getTotalTradePosts() + ChatColor.RESET);
	}

	@Override
	public int getTotalTradePosts() {
		int count = 0;
		for (Entry<String, SoliniaAlignmentChunk> chunk : getChunks().entrySet())
		{
			if (!chunk.getValue().isTradePost())
				continue;
			
			count++;
		}
		
		return count;
	}

	@Override
	public ConcurrentHashMap<String, SoliniaAlignmentChunk> getChunks() {
		return chunks;
	}

	public void setChunks(ConcurrentHashMap<String, SoliniaAlignmentChunk> chunks) {
		this.chunks = chunks;
	}

	@Override
	public SoliniaAlignmentChunk getChunk(SoliniaChunk chunk) {
		// TODO Auto-generated method stub
		return chunks.get(chunk.getSoliniaWorldName() + "_" + chunk.getChunkX() + "_" + chunk.getChunkZ());
	}
	
	@Override
	public void setChunk(String worldChunkPositionCode, SoliniaAlignmentChunk alignmentChunk) {
		chunks.put(worldChunkPositionCode,alignmentChunk);
	}

	@Override
	public void removeChunk(SoliniaChunk chunk) {
		chunks.put(chunk.getSoliniaWorldName().toUpperCase() + "_" + chunk.getChunkX() + "_" + chunk.getChunkZ(), null);
		
	}
}
