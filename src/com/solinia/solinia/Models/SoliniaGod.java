package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Events.SoliniaNPCUpdatedEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaGod implements ISoliniaGod {
	private int id;
	private String name;
	private String description;
	private String alignment = "NEUTRAL";
	
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
	public void sendGodSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "God Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- description: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- alignment: " + ChatColor.GOLD + getAlignment() + ChatColor.RESET);
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidGodSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidGodSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidGodSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "description":
			setDescription(value);
			break;
		case "alignment":
			if (!value.toUpperCase().equals("EVIL") && !value.toUpperCase().equals("NEUTRAL") && !value.toUpperCase().equals("GOOD"))
				throw new InvalidGodSettingException("Invalid Alignment (GOOD,NEUTRAL,EVIL)");
			setAlignment(value.toUpperCase());
			break;
		default:
			throw new InvalidGodSettingException(
					"Invalid setting. Valid Options are: name,description");
		}
	}	
	
	@Override
	public String getAlignment() {
		return alignment;
	}

	@Override
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
}
