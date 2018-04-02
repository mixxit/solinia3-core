package com.solinia.solinia.Interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;

public interface ISoliniaLootTable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	List<ISoliniaLootTableEntry> getEntries();
	
	void setEntries(List<ISoliniaLootTableEntry> entries);

	void sendLootTableSettingsToSender(CommandSender sender);

	void editSetting(String setting, String value) throws InvalidLootTableSettingException, NumberFormatException, CoreStateInitException;

	void setOperatorCreated(boolean operatorCreated);

	boolean isOperatorCreated();

	boolean isMiningLootTable();

	void setMiningLootTable(boolean miningLootTable);

	boolean isForestryLootTable();

	void setForestryLootTable(boolean forestryLootTable);

	boolean isFishingLootTable();

	void setFishingLootTable(boolean fishingLootTable);
}
