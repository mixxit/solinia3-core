package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.SpellTargetType;
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
	private int bodyType = 1;
	
	private String description = "";
	private String alignment = "NEUTRAL";
	private String shortName = "";
	private boolean vampire = false;
	
	private SkillType language = SkillType.UnknownTongue;

	private int passiveAbilityId = 0;
	private int raceLootTableId = 0;
	
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
		sender.sendMessage("- bodytype: " + ChatColor.GOLD + getBodyType() + ChatColor.RESET);
		sender.sendMessage("- vampire: " + ChatColor.GOLD + isVampire() + ChatColor.RESET);
		sender.sendMessage("- admin: " + ChatColor.GOLD + isAdmin() + ChatColor.RESET);
		sender.sendMessage("- language: " + ChatColor.GOLD + getLanguage() + ChatColor.RESET);
		if (getPassiveAbilityId() != 0) {
			sender.sendMessage("- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getSpell(getPassiveAbilityId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " (No Ability)" + ChatColor.RESET);
		}
		if (getRaceLootTableId() != 0) {
			sender.sendMessage("- raceloottableid: " + ChatColor.GOLD + getRaceLootTableId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getRaceLootTableId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- raceloottableid: " + ChatColor.GOLD + getRaceLootTableId() + " (No Loot Table)" + ChatColor.RESET);
		}
		sender.sendMessage("----------------------------");
	}
	
	@Override
	public int getRaceLootTableId() {
		return raceLootTableId ;
	}

	@Override
	public void setRaceLootTableId(int raceLootTableId) {
		this.raceLootTableId = raceLootTableId;
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
		case "bodytype":
			setBodyType(Integer.parseInt(value));
			break;
		case "language":
			String types = "";
			for(SkillType type: SkillType.values())
			{
				if (Utils.IsValidLanguage(type))
					types += type+",";
			}

			try
			{
				
				if (!Utils.IsValidLanguage(SkillType.valueOf(value)))
				{
					throw new InvalidRaceSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
				}
				
				setLanguage(SkillType.valueOf(value));
			} catch (IllegalArgumentException e)
			{
				throw new InvalidRaceSettingException("Invalid type, type must be exactly the same case and can be one of the following: " + types);
			}
			break;
		case "alignment":
			if (!value.toUpperCase().equals("EVIL") && !value.toUpperCase().equals("NEUTRAL") && !value.toUpperCase().equals("GOOD"))
				throw new InvalidRaceSettingException("Invalid Race Alignment (GOOD,NEUTRAL,EVIL)");
			setAlignment(value.toUpperCase());
			break;
		case "raceloottableid":
			if (Integer.parseInt(value) == 0)
			{
				setRaceLootTableId(0);
				break;
			}
			
			ISoliniaLootTable loottable1 = StateManager.getInstance().getConfigurationManager()
			.getLootTable(Integer.parseInt(value));
			if (loottable1 == null)
				throw new InvalidRaceSettingException("Loottable ID does not exist");
			setRaceLootTableId(Integer.parseInt(value));
			break;
		case "passiveabilityid":
			int abilityid = Integer.parseInt(value);
			if (abilityid < 1)
			{
				setPassiveAbilityId(0);
				break;
			}
			try
			{
				ISoliniaSpell ability = StateManager.getInstance().getConfigurationManager().getSpell(abilityid);
				if (ability == null)
					throw new InvalidRaceSettingException("Invalid id");
				
				if (!ability.isBuffSpell() || !Utils.getSpellTargetType(ability.getTargettype()).equals(SpellTargetType.Self))
					throw new InvalidRaceSettingException("Only Self only buff type spells can be set as a passive spell");

			} catch (CoreStateInitException e)
			{
				throw new InvalidRaceSettingException("State not initialised");
			}
			setPassiveAbilityId(abilityid);
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
	public boolean isVampire() {
		return vampire;
	}

	@Override
	public void setVampire(boolean vampire) {
		this.vampire = vampire;
	}

	@Override
	public int getPassiveAbilityId() {
		return passiveAbilityId;
	}

	@Override
	public void setPassiveAbilityId(int passiveAbilityId) {
		this.passiveAbilityId = passiveAbilityId;
	}

	@Override
	public int getBodyType() {
		return bodyType;
	}

	@Override
	public void setBodyType(int bodyType) {
		this.bodyType = bodyType;
	}

	@Override
	public SkillType getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(SkillType language) {
		this.language = language;
	}

}
