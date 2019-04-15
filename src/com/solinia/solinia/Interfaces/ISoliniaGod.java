package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;
import com.solinia.solinia.Models.FactionStandingEntry;

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
	
}
