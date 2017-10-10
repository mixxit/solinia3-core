package com.solinia.solinia.Interfaces;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;

public interface ISoliniaFaction {

	int getId();

	String getName();

	int getBase();

	void setName(String name);

	void setBase(int base);

	void setId(int id);

	void sendFactionSettingsToSender(CommandSender sender) throws CoreStateInitException;

	void editSetting(String setting, String value)
			throws InvalidFactionSettingException, NumberFormatException, CoreStateInitException, IOException;
	
}
