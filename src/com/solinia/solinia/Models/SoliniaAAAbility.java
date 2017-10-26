package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAAEffect;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaAAAbility implements ISoliniaAAAbility {
	private int id;
	private String sysname;
	private String name;
	private List<String> classes;
	private List<ISoliniaAARank> ranks = new ArrayList<ISoliniaAARank>();
	private List<ISoliniaAAEffect> effects = new ArrayList<ISoliniaAAEffect>();
	private boolean isEnabled = false;
	
	@Override
	public String getSysname() {
		return sysname;
	}
	
	@Override
	public void setSysname(String sysname) {
		this.sysname = sysname;
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
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public List<String> getClasses() {
		return classes;
	}

	@Override
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	@Override
	public List<ISoliniaAARank> getRanks() {
		return ranks;
	}

	@Override
	public void setRanks(List<ISoliniaAARank> ranks) {
		this.ranks = ranks;
	}

	@Override
	public boolean canClassUseAbility(ISoliniaClass iSoliniaClass) {
		if (iSoliniaClass == null)
			return false;
		
		if (getClasses().size() == 0)
			return true;
		
		if (getClasses().contains(iSoliniaClass.getName().toUpperCase()))
			return true;
		
		return false;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public void sendAASettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "AA Ability Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- sysname: " + ChatColor.GOLD + getSysname() + ChatColor.RESET);
		sender.sendMessage("- enabled: " + ChatColor.GOLD + isEnabled() + ChatColor.RESET);
		for(ISoliniaAARank rank : this.getRanks())
		{
			sender.sendMessage("RANK ID: " + rank.getId() + " position: " + rank.getPosition() + " cost: " + rank.getCost() + " level_req: " + rank.getLevel_req() + " recast_time: " + rank.getRecast_time() + " spell_type: " + rank.getSpell_type() + " spellid: " + ChatColor.GOLD + rank.getSpell());
			for (SoliniaAARankEffect rankEffect : rank.getEffects())
			{
				sender.sendMessage("effectId: " + rankEffect.getEffectId() + " base1: " + rankEffect.getBase1() + " base2: " + rankEffect.getBase2());
			}
		}
	}

	@Override
	public void editSetting(String setting, String value) throws InvalidAASettingException {
		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidAASettingException("Name is empty");
			setName(value);
			break;
		case "sysname":
			if (value.equals(""))
				throw new InvalidAASettingException("Sysname is empty");
			setSysname(value);
			break;
		case "enabled":
			setEnabled(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidAASettingException(
					"Invalid AA Ability setting. Valid Options are: name, sysname, enabled");
		}
		
		try
		{
			StateManager.getInstance().getConfigurationManager().resetAARankRepository();
		} catch (CoreStateInitException e)
		{
			//
		}
	}

	@Override
	public List<ISoliniaAAEffect> getEffects() {
		return effects;
	}

	@Override
	public void setEffects(List<ISoliniaAAEffect> effects) {
		this.effects = effects;
	}
}
