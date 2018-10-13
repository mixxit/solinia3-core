package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidChunkSettingException;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaChunk {
	private int chunkX;
	private int chunkZ;
	private String soliniaWorldName;
	private String lore = "";
	
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
	
	public Location getFirstBlockLocation()
	{
		World world = getWorld();
		
		if (world == null)
			return null;

		return getFirstBlockLocation(world);
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
		
		for (SoliniaZone zone : getZones(world))
		{
			if (zone.getMiningLootTableId() > 0 || zone.getFishingLootTableId() > 0 || zone.getForagingLootTableId() > 0 || zone.getForestryMinSkill() > 0)
				return true;
		}
		
		return false;
	}
	
	public Location getFirstBlockLocation(World world)
	{
		Chunk chunk = world.getChunkAt(getChunkX(), getChunkZ());
		return chunk.getBlock(0, 0, 0).getLocation();
	}
	
	public boolean isInZone(World world) {
		
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (getFirstBlockLocation(world).distance(
						new Location(world, zone.getX(), 0, zone.getZ())) < zone.getSize())
					return true;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	public List<SoliniaZone> getZones() {
		World world = getWorld();
		
		if (world == null)
			return new ArrayList<SoliniaZone>();
		
		return getZones(world);
	}
	
	public List<SoliniaZone> getZones(World world) {
		List<SoliniaZone> zones = new ArrayList<SoliniaZone>();
		try {
			for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones()) {
				if (getFirstBlockLocation(world).distance(
						new Location(world, zone.getX(), 0, zone.getZ())) < zone.getSize())
					zones.add(zone);
			}
		} catch (CoreStateInitException e) {
			
		}
		
		return zones;
	}
	
	public boolean isInZone() {
		
		World world = getWorld();
		
		if (world == null)
			return false;
		
		return isInZone(world);
	}
	
	public void sendChunkSettingsToSender(CommandSender sender) 
	{
		sender.sendMessage(ChatColor.RED + "Chunk Settings for " + ChatColor.GOLD + getChunkX() + ":" + getChunkZ() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- X: " + ChatColor.GOLD + getChunkX() + ChatColor.RESET);
		sender.sendMessage("- Z: " + ChatColor.GOLD + getChunkZ() + ChatColor.RESET);
		sender.sendMessage("- lore: " + ChatColor.GOLD + getLore() + ChatColor.RESET);
		
		if (sender instanceof Player)
		{
			Player player = (Player)sender;
			for(SoliniaZone zone : getZones(player.getWorld()))
			{
				player.sendMessage("In Zone: " + zone.getName());
			}
		}
	}

	public void editSetting(String setting, String value) throws InvalidChunkSettingException
	{
		switch (setting.toLowerCase()) {
		case "lore":
			setLore(value);
			break;
		default:
			throw new InvalidChunkSettingException("Invalid Chunk setting. Valid Options are: lore");
		}
	}
	
	public String getLore() {
		return lore;
	}
	public void setLore(String lore) {
		this.lore = lore;
	}
	
}
