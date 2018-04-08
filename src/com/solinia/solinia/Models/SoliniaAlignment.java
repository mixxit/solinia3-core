package com.solinia.solinia.Models;

import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaAlignmentChunkFactory;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

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
		return 0;
	}
	
	@Override
	public int getTotalZones() {
		return chunks.size();
	}

	@Override
	public int getTotalMaterialZones() {
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
	public void sendAlignmentStats(CommandSender sender) {
		sender.sendMessage(getName().toUpperCase() + " Empire Territory Overview:");
		sender.sendMessage("-----------------------------");
		
		sender.sendMessage("Coffers: $" + getCoffers());
		sender.sendMessage("Upkeep Cost: $" + getUpkeepCost());
		sender.sendMessage("Total Zones: " + getTotalZones());
		sender.sendMessage("Total Material Zones: " + getTotalMaterialZones());
		sender.sendMessage("Total Trade Posts: $" + getTotalTradePosts());
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
