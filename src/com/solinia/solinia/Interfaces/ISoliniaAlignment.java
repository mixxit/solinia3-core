package com.solinia.solinia.Interfaces;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;

public interface ISoliniaAlignment extends IPersistable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	void editSetting(String setting, String value)
			throws InvalidAlignmentSettingException, NumberFormatException, CoreStateInitException;

	void sendAlignmentSettingsToSender(CommandSender sender) throws CoreStateInitException;

}
