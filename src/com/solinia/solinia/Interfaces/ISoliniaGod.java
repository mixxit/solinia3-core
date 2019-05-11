package com.solinia.solinia.Interfaces;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;

public interface ISoliniaGod {

	int getId();

	String getName();

	void setName(String name);

	void setId(int id);

	void sendGodSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidGodSettingException;

	String getDescription();

	void setDescription(String description);

	String getAlignment();

	void setAlignment(String alignment);

	int getPassiveAbilityId();

	void setPassiveAbilityId(int passiveAbilityId);
	
}
