package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaGod implements ISoliniaGod {
	private int id;
	private String name;
	private String description;
	private String alignment = "NEUTRAL";
	private int passiveAbilityId = 0;
	
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
		sender.sendMessage("- description: " + ChatColor.GOLD + getDescription() + ChatColor.RESET);
		sender.sendMessage("- alignment: " + ChatColor.GOLD + getAlignment() + ChatColor.RESET);
		if (getPassiveAbilityId() != 0) {
			sender.sendMessage("- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " ("
					+ StateManager.getInstance().getConfigurationManager().getSpell(getPassiveAbilityId()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- passiveabilityid: " + ChatColor.GOLD + getPassiveAbilityId() + " (No Ability)" + ChatColor.RESET);
		}
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
					throw new InvalidGodSettingException("Invalid id");
				
				if (!ability.isBuffSpell() || !Utils.getSpellTargetType(ability.getTargettype()).equals(SpellTargetType.Self))
					throw new InvalidGodSettingException("Only Self only buff type spells can be set as a passive spell");
					
			} catch (CoreStateInitException e)
			{
				throw new InvalidGodSettingException("State not initialised");
			}
			setPassiveAbilityId(abilityid);
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

	@Override
	public int getPassiveAbilityId() {
		return passiveAbilityId;
	}

	@Override
	public void setPassiveAbilityId(int passiveAbilityId) {
		this.passiveAbilityId = passiveAbilityId;
	}
}
