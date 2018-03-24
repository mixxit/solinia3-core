package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaZone {
	private int id;
	private String name;
	private int x;
	private int y;
	private int z;
	private boolean operatorCreated = true;
	private boolean hotzone = false;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public void setOperatorCreated(boolean operatorCreated) {
		this.operatorCreated = operatorCreated;
	}
	
	public boolean isOperatorCreated()
	{
		return this.operatorCreated;
	}
	
	public boolean isHotzone() {
		return hotzone;
	}
	public void setHotzone(boolean hotzone) {
		this.hotzone = hotzone;
	}
	
	public void sendZoneSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Zone Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- x: " + ChatColor.GOLD + getX() + ChatColor.RESET);
		sender.sendMessage("- y: " + ChatColor.GOLD + getY() + ChatColor.RESET);
		sender.sendMessage("- z: " + ChatColor.GOLD + getZ() + ChatColor.RESET);
		sender.sendMessage("- hotzone: " + ChatColor.GOLD + isHotzone() + ChatColor.RESET);
	}

	public void editSetting(String setting, String value)
			throws InvalidZoneSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidZoneSettingException("Name is empty");
			setName(value);
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
		case "hotzone":
			setHotzone(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidZoneSettingException(
					"Invalid zone setting. Valid Options are: name,x,y,z,hotzone");
		}
	}
}
