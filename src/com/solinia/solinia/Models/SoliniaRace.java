package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaRace implements ISoliniaRace {

	private int id;
	private boolean isadmin = true;
	private String name = "";
	
	private int strength = 1;
	private int stamina = 1;
	private int agility = 1;
	private int dexterity = 1;
	private int wisdom = 1;
	private int intelligence = 1;
	private int charisma = 1;
	
	private String description = "";
	private String alignment = "NEUTRAL";
	private String shortName = "";
	private UUID king;
	private boolean vampire = false;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return this.isadmin;
	}

	@Override
	public void setAdmin(boolean isadmin) {
		// TODO Auto-generated method stub
		this.isadmin = isadmin;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public int getStamina() {
		return stamina;
	}

	@Override
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	@Override
	public int getAgility() {
		return agility;
	}

	@Override
	public void setAgility(int agility) {
		this.agility = agility;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	@Override
	public int getWisdom() {
		return wisdom;
	}

	@Override
	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
	}

	@Override
	public int getIntelligence() {
		return intelligence;
	}

	@Override
	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	@Override
	public int getCharisma() {
		return charisma;
	}

	@Override
	public void setCharisma(int charisma) {
		this.charisma = charisma;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void sendRaceSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Race Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- shortname: " + ChatColor.GOLD + getShortName() + ChatColor.RESET);
		sender.sendMessage("- description: " + ChatColor.GOLD + getDescription() + ChatColor.RESET);
		sender.sendMessage("- alignment: " + ChatColor.GOLD + getAlignment() + ChatColor.RESET);
		sender.sendMessage("- vampire: " + ChatColor.GOLD + isVampire() + ChatColor.RESET);
		sender.sendMessage("- admin: " + ChatColor.GOLD + isAdmin() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidRaceSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "description":
			setDescription(value);
			break;
		case "shortname":
			setShortName(value);
			break;
		case "vampire":
			setVampire(Boolean.parseBoolean(value));
			break;
		case "admin":
			setAdmin(Boolean.parseBoolean(value));
			break;
		case "alignment":
			if (!value.toUpperCase().equals("EVIL") && !value.toUpperCase().equals("NEUTRAL") && !value.toUpperCase().equals("GOOD"))
				throw new InvalidRaceSettingException("Invalid Race Alignment (GOOD,NEUTRAL,EVIL)");
			setAlignment(value.toUpperCase());
			break;
		default:
			throw new InvalidRaceSettingException("Invalid Race setting. Valid Options are: description");
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
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
	public String getShortName() {
		return shortName;
	}

	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public UUID getKing() {
		return king;
	}

	@Override
	public void setKing(UUID king) {
		try {
			String playerName = StateManager.getInstance().getPlayerManager().getPlayerNameByUUID(king);
			if (playerName != null && !playerName.equals(""))
			{
				this.king = king;
				Utils.BroadcastPlayers(playerName + " has been declared Ruler of the " + Utils.FormatAsName(getName())+"s!");
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isVampire() {
		return vampire;
	}

	@Override
	public void setVampire(boolean vampire) {
		this.vampire = vampire;
	}

}
