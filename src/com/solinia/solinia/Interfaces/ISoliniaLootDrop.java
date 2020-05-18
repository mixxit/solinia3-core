package com.solinia.solinia.Interfaces;

import java.sql.Timestamp;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;

public interface ISoliniaLootDrop extends IPersistable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	List<ISoliniaLootDropEntry> getEntries();
	
	void setEntries(List<ISoliniaLootDropEntry> entries);

	void sendLootDropSettingsToSender(CommandSender sender);

	void editSetting(String setting, String value) throws InvalidLootDropSettingException, NumberFormatException, CoreStateInitException;

	List<ISoliniaLootDropEntry> getEntriesForClass(ISoliniaClass classObj);

	Timestamp getLastUpdatedTime();

	void setLastUpdatedTime(Timestamp lastUpdatedTime);

	void setLastUpdatedTimeNow();

}
