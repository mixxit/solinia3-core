package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Models.FactionStandingEntry;

public interface ISoliniaFaction extends IPersistable{

	int getId();

	String getName();

	int getBase();

	void setName(String name);

	void setBase(int base);

	void setId(int id);

	void sendFactionSettingsToSender(CommandSender sender) throws CoreStateInitException;

	FactionStandingEntry getFactionEntry(int factionId);

	void setFactionEntries(List<FactionStandingEntry> factionEntries);

	List<FactionStandingEntry> getFactionEntries();

	FactionStandingEntry createFactionStandingEntry(int factionId);

	void setFactionEntry(int factionId, int value);

	void editSetting(String setting, String value)
			throws InvalidFactionSettingException, NumberFormatException, CoreStateInitException, IOException;

	String getAllyGrantsTitle();

	void setAllyGrantsTitle(String allyGrantsTitle);

	String getScowlsGrantsTitle();

	void setScowlsGrantsTitle(String scowlsGrantTitle);

	void removeFactionStandingEntry(int factionId);
	
}
