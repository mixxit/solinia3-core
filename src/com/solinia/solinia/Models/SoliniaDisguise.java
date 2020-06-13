package com.solinia.solinia.Models;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidDisguiseSettingException;
import com.solinia.solinia.Interfaces.IPersistable;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.ModdedDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.utilities.parser.DisguiseParseException;
import me.libraryaddict.disguise.utilities.parser.DisguiseParser;
import me.libraryaddict.disguise.utilities.parser.DisguisePerm;
import net.md_5.bungee.api.ChatColor;

public class SoliniaDisguise implements IPersistable {
	private int id = 0;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private String disguiseName = "";
	private String disguiseType = "WOLF";
	private String disguiseType2 = "WOLF";
	private String disguiseType3 = "WOLF";
	private String disguiseType4 = "WOLF";
	private String description = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
	}
	
	public void sendDisguiseSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "Disguise Settings for Recipe " + ChatColor.GOLD + getDisguiseName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- disguisename: " + ChatColor.GOLD + getDisguiseName() + ChatColor.RESET);
		if (getLibsDisguisesDisguisePerm(0) != null)
		{
			sender.sendMessage("- playerdisguise1: " + ChatColor.GOLD + getLibsDisguisesDisguisePerm(0).isPlayer() + ChatColor.RESET);
			sender.sendMessage("- mobdisguise1: " + ChatColor.GOLD + getLibsDisguisesDisguisePerm(0).isMob() + ChatColor.RESET);
			sender.sendMessage("- moddeddisguise1: " + ChatColor.GOLD + getLibsDisguisesDisguisePerm(0).isCustomDisguise() + ChatColor.RESET);
		}
		sender.sendMessage("- disguisetype1: " + ChatColor.GOLD + getDisguiseType() + ChatColor.RESET);
		sender.sendMessage("- disguisetype2: " + ChatColor.GOLD + getDisguiseType2() + ChatColor.RESET);
		sender.sendMessage("- disguisetype3: " + ChatColor.GOLD + getDisguiseType3() + ChatColor.RESET);
		sender.sendMessage("- disguisetype4: " + ChatColor.GOLD + getDisguiseType4() + ChatColor.RESET);
		sender.sendMessage("- description: " + ChatColor.GOLD + getDescription() + ChatColor.RESET);
	}
	
	public DisguisePerm getLibsDisguisesDisguisePerm(int type)
	{
		DisguisePerm a = DisguiseParser.getDisguisePerm(this.getDisguiseType(type).toUpperCase());
		if (a != null)
			return a;
		
		return null;
	}
	
	public boolean isPlayerDisguise(int type)
	{
		DisguisePerm a = DisguiseParser.getDisguisePerm(this.getDisguiseType(type).toUpperCase());
		if (a != null)
			return a.isPlayer();
		
		return false;
	}

	public void editSetting(String setting, String value)
			throws InvalidDisguiseSettingException, NumberFormatException, CoreStateInitException {

		switch (setting.toLowerCase()) {
		case "disguisename":
			if (value.equals(""))
				throw new InvalidDisguiseSettingException("Name is empty");
			setDisguiseName(value.toUpperCase());
			break;
		case "disguisetype":
			if (value.equals(""))
				throw new InvalidDisguiseSettingException("Type is empty");
			setDisguiseType(value.toUpperCase());
			break;
		case "disguisetype2":
			if (value.equals(""))
				throw new InvalidDisguiseSettingException("Type is empty");
			setDisguiseType2(value.toUpperCase());
			break;
		case "disguisetype3":
			if (value.equals(""))
				throw new InvalidDisguiseSettingException("Type is empty");
			setDisguiseType3(value.toUpperCase());
			break;
		case "disguisetype4":
			if (value.equals(""))
				throw new InvalidDisguiseSettingException("Type is empty");
			setDisguiseType4(value.toUpperCase());
			break;
		case "description":
			setDescription(value.toUpperCase());
			break;
		default:
			throw new InvalidDisguiseSettingException(
					"Invalid Disguise setting. Valid Options are: disguisename,disguisetype,description");
		}
		
	}
	
	public String getDisguiseName() {
		return disguiseName;
	}
	public void setDisguiseName(String disguiseName) {
		this.disguiseName = disguiseName;
	}
	
	public String getDisguiseType(int type) {
		switch(type)
		{
			case 2:
				return getDisguiseType2();
			case 3:
				return getDisguiseType3();
			case 4:
				return getDisguiseType4();
			default:
				return getDisguiseType();
		}
	}
	
	public String getDisguiseType() {
		return disguiseType;
	}
	public void setDisguiseType(String disguiseType) {
		this.disguiseType = disguiseType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Disguise getLibsDisguise(int type) {
		
		DisguisePerm disguisePerm = this.getLibsDisguisesDisguisePerm(type);
		if (disguisePerm == null)
			return new MobDisguise(DisguiseType.WOLF);
		if (disguisePerm.isCustomDisguise())
			return new ModdedDisguise(this.getDisguiseType(type).toUpperCase());
		if (disguisePerm.isPlayer())
			return new PlayerDisguise(this.getDisguiseType(type).toUpperCase());
		if (disguisePerm.isMob())
			return new MobDisguise(disguisePerm.getType());

		return new MobDisguise(DisguiseType.WOLF);
	}
	public DisguisePackage getDisguisePackage(int type) {
		if (getLibsDisguise(type) == null)
			return null;
		
		String typeName = getDisguiseType(type);
		return new DisguisePackage(getLibsDisguise(type).getType(),typeName, typeName);
	}
	public String getDisguiseType2() {
		return disguiseType2;
	}
	public void setDisguiseType2(String disguiseType2) {
		this.disguiseType2 = disguiseType2;
	}
	public String getDisguiseType3() {
		return disguiseType3;
	}
	public void setDisguiseType3(String disguiseType3) {
		this.disguiseType3 = disguiseType3;
	}
	public String getDisguiseType4() {
		return disguiseType4;
	}
	public void setDisguiseType4(String disguiseType4) {
		this.disguiseType4 = disguiseType4;
	}
}
