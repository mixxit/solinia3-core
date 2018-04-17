package com.solinia.solinia.Interfaces;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.InvalidQuestSettingException;

public interface ISoliniaQuest {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	boolean isOperatorCreated();

	void setOperatorCreated(boolean isOperatorCreated);

	String getQuestFlagCompletion();

	void setQuestFlagCompletion(String questFlagCompletion);

	void editSetting(String setting, String value, String[] additional) throws InvalidQuestSettingException;

	void sendQuestSettingsToSender(CommandSender sender);
	
}
