package com.solinia.solinia.Interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Models.InteractionType;

public interface ISoliniaNPCEventHandler {

	InteractionType getInteractiontype();

	void setInteractiontype(InteractionType interactiontype);

	String getTriggerdata();

	void setTriggerdata(String triggerdata);

	String getChatresponse();

	void setChatresponse(String chatresponse);

	int getRequiresQuest();

	void setRequiresQuest(int requiresQuest);

	int getAwardsQuest();

	void setAwardsQuest(int awardsQuest);

	String getRequiresQuestFlag();

	void setRequiresQuestFlag(String requiresQuestFlag);

	String getAwardsQuestFlag();

	void setAwardsQuestFlag(String awardsQuestFlag);

	void sendNPCEvent(CommandSender sender);

	void editTriggerEventSetting(String setting, String value) throws InvalidNPCEventSettingException;

	int getNpcId();

	void setNpcId(int npcId);

	boolean playerMeetsRequirements(Player triggerentity);

	void awardPlayer(Player triggerentity);

	String getTeleportResponse();

	void setTeleportResponse(String teleportResponse);
	
}
