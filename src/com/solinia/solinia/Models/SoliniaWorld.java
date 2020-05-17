package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidWorldSettingException;
import com.solinia.solinia.Exceptions.SoliniaChunkCreationException;
import com.solinia.solinia.Factories.SoliniaChunkFactory;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;

public class SoliniaWorld {
	private Integer id;
	private String name;
	private ConcurrentHashMap<String, SoliniaChunk> chunks = new ConcurrentHashMap<String, SoliniaChunk>(); 
	private int forestryMinSkill = 0;
	private int forestryLootTableId = 0;
	private int fishingMinSkill = 0;
	private int fishingLootTableId = 0;
	private int miningMinSkill = 0;
	private int miningLootTableId = 0;
	private int foragingMinSkill = 0;
	private int foragingLootTableId = 0;
	private int playerStartLootTableId = 0;
	private int globalLootTableId = 0;
	private int inspirationLootTableId = 0;
	private int whisperchatrange = 5;
	private int localchatrange = 32;
	private int shoutchatrange = 64;
	
	private ConcurrentHashMap<String, ArrayList<String>> playerIpNameMappings = new ConcurrentHashMap<String, ArrayList<String>>(); 
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConcurrentHashMap<String, SoliniaChunk> getChunks() {
		return chunks;
	}

	public void setChunks(ConcurrentHashMap<String, SoliniaChunk> chunks) {
		this.chunks = chunks;
	}
	
	public SoliniaChunk getChunk(Chunk bukkitChunk) {
		
		return getChunk(bukkitChunk.getX(), bukkitChunk.getZ());
	}
	
	public void setChunk(String chunkUnderscoredPosition, SoliniaChunk chunk) {
		chunks.put(chunkUnderscoredPosition, chunk);
	}
	
	public void sendWorldSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Settings for Object " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- chunkcount: " + ChatColor.GRAY + chunks.size() + ChatColor.RESET);
		sender.sendMessage("- whisperchatrange: " + ChatColor.GRAY + this.getWhisperchatrange() + ChatColor.RESET);
		sender.sendMessage("- localchatrange: " + ChatColor.GRAY + this.getLocalchatrange() + ChatColor.RESET);
		sender.sendMessage("- shoutchatrange: " + ChatColor.GRAY + this.getShoutchatrange() + ChatColor.RESET);
		
		if (getGlobalLootTableId() != 0) {
			sender.sendMessage("- globalloottableid: " + ChatColor.GOLD + getGlobalLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getGlobalLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- globalloottableid: " + ChatColor.GOLD + getGlobalLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}

		
		sender.sendMessage("- forestryminskill: " + ChatColor.GOLD + getForestryMinSkill() + ChatColor.RESET);
		sender.sendMessage("- fishingminskill: " + ChatColor.GOLD + getFishingMinSkill() + ChatColor.RESET);
		sender.sendMessage("- miningminskill: " + ChatColor.GOLD + getMiningMinSkill() + ChatColor.RESET);
		sender.sendMessage("- foragingminskill: " + ChatColor.GOLD + getForagingMinSkill() + ChatColor.RESET);
		if (getForagingLootTableId() != 0) {
			sender.sendMessage("- foragingloottableid: " + ChatColor.GOLD + getForagingLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getForagingLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- foragingloottableid: " + ChatColor.GOLD + getForagingLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
		
		if (getForestryLootTableId() != 0) {
			sender.sendMessage("- forestryloottableid: " + ChatColor.GOLD + getForestryLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getForestryLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- forestryloottableid: " + ChatColor.GOLD + getForestryLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}

		if (getFishingLootTableId() != 0) {
			sender.sendMessage("- fishingloottableid: " + ChatColor.GOLD + getFishingLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getFishingLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- fishingloottableid: " + ChatColor.GOLD + getFishingLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}

		if (getMiningLootTableId() != 0) {
			sender.sendMessage("- miningloottableid: " + ChatColor.GOLD + getMiningLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getMiningLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- miningloottableid: " + ChatColor.GOLD + getMiningLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
		
		if (getPlayerStartLootTableId() > 0)
		{
		sender.sendMessage("- playerstartloottableid: " + ChatColor.GOLD + getPlayerStartLootTableId() + " ("
				+ StateManager.getInstance().getConfigurationManager().getLootTable(getPlayerStartLootTableId()).getName()
				+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- playerstartloottableid: " + ChatColor.GOLD + getPlayerStartLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
		if (getInspirationLootTableId() > 0)
		{
		sender.sendMessage("- inspirationloottableid: " + ChatColor.GOLD + getInspirationLootTableId() + " ("
				+ StateManager.getInstance().getConfigurationManager().getLootTable(getInspirationLootTableId()).getName()
				+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- playerstartloottableid: " + ChatColor.GOLD + getInspirationLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
	}

	public void editSetting(String setting, String value)
			throws InvalidWorldSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "whisperchatrange":
			setWhisperchatrange(Integer.parseInt(value));
			break;
		case "localchatrange":
			setLocalchatrange(Integer.parseInt(value));
			break;
		case "shoutchatrange":
			setShoutchatrange(Integer.parseInt(value));
			break;
		case "forestryminskill":
			setForestryMinSkill(Integer.parseInt(value));
			break;
		case "miningminskill":
			setMiningMinSkill(Integer.parseInt(value));
			break;
		case "fishingminskill":
			setFishingMinSkill(Integer.parseInt(value));
			break;
		case "foragingminskill":
			setForagingMinSkill(Integer.parseInt(value));
			break;
		case "foragingloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setForagingLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable0 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable0 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setForagingLootTableId(Integer.parseInt(value));
			break;
		case "forestryloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setForestryLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable1 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable1 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setForestryLootTableId(Integer.parseInt(value));
			break;
		case "fishingloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setFishingLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable2 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable2 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setFishingLootTableId(Integer.parseInt(value));
			break;
		case "globalloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setGlobalLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable3 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable3 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setGlobalLootTableId(Integer.parseInt(value));
			break;
		case "miningloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setMiningLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable4 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable4 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setMiningLootTableId(Integer.parseInt(value));
			break;
		case "playerstartloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setPlayerStartLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable5 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable5 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setPlayerStartLootTableId(Integer.parseInt(value));
			break;
		case "inspirationloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setPlayerStartLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable6 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable6 == null)
				throw new InvalidWorldSettingException("Loottable ID does not exist");
			setInspirationLootTableId(Integer.parseInt(value));
			break;
		default:
			throw new InvalidWorldSettingException(
					"Invalid setting. Valid Options are: forestryloottableid,fishingloottableid,miningloottableid,forestryminskill,miningminskill,fishingminskill,playerstartloottableid,inspirationloottableid");
		}
	}
	
	public int getForestryLootTableId() {
		return forestryLootTableId;
	}
	public void setForestryLootTableId(int forestryLootTableId) {
		this.forestryLootTableId = forestryLootTableId;
	}
	public int getMiningLootTableId() {
		return miningLootTableId;
	}
	public void setMiningLootTableId(int miningLootTableId) {
		this.miningLootTableId = miningLootTableId;
	}
	public int getFishingLootTableId() {
		return fishingLootTableId;
	}
	public void setFishingLootTableId(int fishingLootTableId) {
		this.fishingLootTableId = fishingLootTableId;
	}
	public int getFishingMinSkill() {
		return fishingMinSkill;
	}
	public void setFishingMinSkill(int fishingMinSkill) {
		this.fishingMinSkill = fishingMinSkill;
	}
	public int getMiningMinSkill() {
		return miningMinSkill;
	}
	public void setMiningMinSkill(int miningMinSkill) {
		this.miningMinSkill = miningMinSkill;
	}
	public int getForestryMinSkill() {
		return forestryMinSkill;
	}
	public void setForestryMinSkill(int forestryMinSkill) {
		this.forestryMinSkill = forestryMinSkill;
	}
	public int getForagingLootTableId() {
		return foragingLootTableId;
	}
	public void setForagingLootTableId(int foragingLootTableId) {
		this.foragingLootTableId = foragingLootTableId;
	}
	public int getForagingMinSkill() {
		return foragingMinSkill;
	}
	public void setForagingMinSkill(int foragingMinSkill) {
		this.foragingMinSkill = foragingMinSkill;
	}

	public SoliniaChunk getChunk(int chunkX, int chunkZ) {
		SoliniaChunk chunk = chunks.get(chunkX + "_" + chunkZ);
		
		if (chunk != null)
		{
			return chunk;
		} else {
			try {
				return SoliniaChunkFactory.Create(getName().toUpperCase(), chunkX, chunkZ);
			} catch (CoreStateInitException e) {
			} catch (SoliniaChunkCreationException e) {
			}
		}
		
		return null;
	}

	public int getPlayerStartLootTableId() {
		return playerStartLootTableId;
	}

	public void setPlayerStartLootTableId(int playerStartLootTableId) {
		this.playerStartLootTableId = playerStartLootTableId;
	}

	public ConcurrentHashMap<String, ArrayList<String>> getPlayerIpNameMappings() {
		return playerIpNameMappings;
	}

	public void setPlayerIpNameMappings(ConcurrentHashMap<String, ArrayList<String>> playerIpNameMappings) {
		this.playerIpNameMappings = playerIpNameMappings;
	}

	public int getGlobalLootTableId() {
		return globalLootTableId;
	}

	public void setGlobalLootTableId(int globalLootTableId) {
		this.globalLootTableId = globalLootTableId;
	}

	public int getLocalchatrange() {
		return localchatrange;
	}

	public void setLocalchatrange(int localchatrange) {
		this.localchatrange = localchatrange;
	}

	public int getWhisperchatrange() {
		return whisperchatrange;
	}

	public void setWhisperchatrange(int whisperchatrange) {
		this.whisperchatrange = whisperchatrange;
	}

	public int getShoutchatrange() {
		return shoutchatrange;
	}

	public void setShoutchatrange(int shoutchatrange) {
		this.shoutchatrange = shoutchatrange;
	}

	public int getInspirationLootTableId() {
		return inspirationLootTableId;
	}

	public void setInspirationLootTableId(int inspirationLootTableId) {
		this.inspirationLootTableId = inspirationLootTableId;
	}
}
