package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Models.QuestStep;

public interface ISoliniaQuest extends IPersistable {

	int getId();

	void setId(int id);

	String getName();

	void setName(String name);

	String getQuestFlagCompletion();

	void setQuestFlagCompletion(String questFlagCompletion);

	void sendQuestSettingsToSender(CommandSender sender);

	ConcurrentHashMap<Integer, QuestStep> getQuestSteps();

	void setQuestSteps(ConcurrentHashMap<Integer, QuestStep> questSteps);

	void setQuestStep(int stepId, String property, List<String> data) throws InvalidQuestSettingException;

	void editSetting(CommandSender sender, String setting, String value, String[] additional)
			throws InvalidQuestSettingException;
	
}
