package com.solinia.solinia.Models;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Managers.StateManager;
import net.md_5.bungee.api.ChatColor;

public class SoliniaSpawnGroup implements ISoliniaSpawnGroup {
	private int id;
	private String name;
	private String world;
	private double x;
	private double y;
	private double z;
	private int npcid;
	private double yaw;
	private double pitch;
	private int respawntime = 900;
	private boolean disabled = false;
	
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
		this.name = name;
	}
	@Override
	public String getWorld() {
		return world;
	}
	@Override
	public void setWorld(String world) {
		this.world = world;
	}
	@Override
	public double getX() {
		return x;
	}
	@Override
	public void setX(double x) {
		this.x = x;
	}
	@Override
	public double getY() {
		return y;
	}
	@Override
	public void setY(double y) {
		this.y = y;
	}
	@Override
	public double getZ() {
		return z;
	}
	@Override
	public void setZ(double z) {
		this.z = z;
	}
	
	@Override
	public int getNpcid() {
		return npcid;
	}

	@Override
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}
	@Override
	public void setLocation(Location location) {
		// TODO Auto-generated method stub
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.world = location.getWorld().getName();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}
	
	@Override
	public double getYaw() {
		return yaw;
	}
	@Override
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	@Override
	public double getPitch() {
		return pitch;
	}
	@Override
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	
	@Override
	public int getRespawntime() {
		return respawntime;
	}
	@Override
	public void setRespawntime(int respawntime) {
		this.respawntime = respawntime;
	}
	
	@Override
	public void sendSpawnGroupSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "SpawnGroup Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- disabled: " + ChatColor.GOLD + isDisabled() + ChatColor.RESET);
		sender.sendMessage("- respawntime: " + ChatColor.GOLD + getRespawntime() + ChatColor.RESET);
		sender.sendMessage("- npcid: " + ChatColor.GOLD + getNpcid() + ChatColor.RESET);
		sender.sendMessage("- x: " + ChatColor.GOLD + getX() + ChatColor.RESET);
		sender.sendMessage("- y: " + ChatColor.GOLD + getY() + ChatColor.RESET);
		sender.sendMessage("- z: " + ChatColor.GOLD + getZ() + ChatColor.RESET);

	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidSpawnGroupSettingException, NumberFormatException, CoreStateInitException, java.io.IOException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidSpawnGroupSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidSpawnGroupSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "respawntime":
			if (Integer.parseInt(value) < 30)
				throw new InvalidSpawnGroupSettingException("Minimum respawn time is 30 seconds");
			setRespawntime(Integer.parseInt(value));
			break;
		case "x":
			setX(Integer.parseInt(value));
			break;
		case "y":
			setY(Integer.parseInt(value));
			break;
		case "z":
			setZ(Integer.parseInt(value));
			break;
		case "disabled":
			setDisabled(Boolean.parseBoolean(value));
			break;
		case "npcid":
			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(Integer.parseInt(value));
			if (npc == null)
				throw new InvalidSpawnGroupSettingException("Invalid NPCID");
			setNpcid(Integer.parseInt(value));
			break;
		default:
			throw new InvalidSpawnGroupSettingException(
					"Invalid SpawnGroup setting. Valid Options are: name, x, y, z, respawntime,npcid");
		}
	}
	
	@Override
	public boolean isDisabled() {
		return disabled;
	}
	
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
