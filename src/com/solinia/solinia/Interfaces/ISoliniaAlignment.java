package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Models.SoliniaChunk;
import com.solinia.solinia.Models.SoliniaZone;

public interface ISoliniaAlignment {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	void editSetting(String setting, String value)
			throws InvalidAlignmentSettingException, NumberFormatException, CoreStateInitException;

	void sendAlignmentSettingsToSender(CommandSender sender) throws CoreStateInitException;

}
