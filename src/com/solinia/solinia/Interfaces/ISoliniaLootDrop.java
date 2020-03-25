package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Models.SoliniaLootDropEntry;

public interface ISoliniaLootDrop {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	List<ISoliniaLootDropEntry> getEntries();
	
	void setEntries(List<ISoliniaLootDropEntry> entries);

	void sendLootDropSettingsToSender(CommandSender sender);

	void editSetting(String setting, String value) throws InvalidLootDropSettingException, NumberFormatException, CoreStateInitException;

	List<ISoliniaLootDropEntry> getEntriesForClass(ISoliniaClass classObj);

}
