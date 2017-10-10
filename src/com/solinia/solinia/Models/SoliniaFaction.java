package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaFaction implements ISoliniaFaction {
	private int id;
	private String name;
	private int base;
	
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
	public int getBase() {
		return base;
	}
	
	@Override
	public void setBase(int base) {
		this.base = base;
	}
	
	@Override
	public void sendFactionSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "SpawnGroup Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- base: " + ChatColor.GOLD + getBase() + ChatColor.RESET);

	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidFactionSettingException, NumberFormatException, CoreStateInitException, java.io.IOException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidFactionSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidFactionSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "base":
			if (Integer.parseInt(value) < -1500 || Integer.parseInt(value) > 1500)
				throw new InvalidFactionSettingException("Bounds are -1500 to 1500");
			setBase(Integer.parseInt(value));
			break;
		default:
			throw new InvalidFactionSettingException(
					"Invalid SpawnGroup setting. Valid Options are: name, base");
		}
	}
}
