package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaNPCEventHandler implements ISoliniaNPCEventHandler {
	private InteractionType interactiontype;
	private String triggerdata;
	private String chatresponse;
	private int requiresQuest = 0;
	private int awardsQuest = 0;
	private String requiresQuestFlag = null;
	private String awardsQuestFlag = null;
	private int npcId;

	@Override
	public InteractionType getInteractiontype() {
		return interactiontype;
	}

	@Override
	public void setInteractiontype(InteractionType interactiontype) {
		this.interactiontype = interactiontype;
	}

	@Override
	public String getTriggerdata() {
		return triggerdata;
	}

	@Override
	public void setTriggerdata(String triggerdata) {
		this.triggerdata = triggerdata;
	}

	@Override
	public String getChatresponse() {
		return chatresponse;
	}

	@Override
	public void setChatresponse(String chatresponse) {
		this.chatresponse = chatresponse;
	}

	@Override
	public int getRequiresQuest() {
		return requiresQuest;
	}

	@Override
	public void setRequiresQuest(int requiresQuest) {
		this.requiresQuest = requiresQuest;
	}

	@Override
	public int getAwardsQuest() {
		return awardsQuest;
	}

	@Override
	public void setAwardsQuest(int awardsQuest) {
		this.awardsQuest = awardsQuest;
	}

	@Override
	public String getRequiresQuestFlag() {
		return requiresQuestFlag;
	}

	@Override
	public void setRequiresQuestFlag(String requiresQuestFlag) {
		this.requiresQuestFlag = requiresQuestFlag;
	}

	@Override
	public String getAwardsQuestFlag() {
		return awardsQuestFlag;
	}

	@Override
	public void setAwardsQuestFlag(String awardsQuestFlag) {
		this.awardsQuestFlag = awardsQuestFlag;
	}

	@Override
	public void sendNPCEvent(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "NPC Trigger Event [" + getTriggerdata() + "]" + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- triggerdata: " + ChatColor.GOLD + getTriggerdata() + ChatColor.RESET);
		sender.sendMessage("- chatresponse: " + ChatColor.GOLD + getChatresponse() + ChatColor.RESET);
		sender.sendMessage("- interactiontype: " + ChatColor.GOLD + getInteractiontype() + ChatColor.RESET);
		sender.sendMessage("- requiresquest: " + ChatColor.GOLD + getRequiresQuest() + ChatColor.RESET);
		sender.sendMessage("- awardsquest: " + ChatColor.GOLD + getAwardsQuest() + ChatColor.RESET);
		sender.sendMessage("- requiresquestflag: " + ChatColor.GOLD + getRequiresQuestFlag() + ChatColor.RESET);
		sender.sendMessage("- awardsquestflag: " + ChatColor.GOLD + getAwardsQuestFlag() + ChatColor.RESET);
	}

	@Override
	public void editTriggerEventSetting(String setting, String value) throws InvalidNPCEventSettingException {
		switch (setting.toLowerCase()) {
		case "triggerdata":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Triggerdata is empty");
			if (value.contains(" "))
				throw new InvalidNPCEventSettingException("Triggerdata can only be one value");
			setTriggerdata(value.toUpperCase());
			break;
		case "chatresponse":
			if (value.equals(""))
				throw new InvalidNPCEventSettingException("Chatresponse is empty");
			setChatresponse(value);
			break;
		case "requiresquest":
			int questid = Integer.parseInt(value);
			if (questid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try
			{
			ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(questid);
			if (quest == null)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setRequiresQuest(questid);
			break;
		case "awardsquest":
			int aquestid = Integer.parseInt(value);
			if (aquestid < 1)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			try
			{
			ISoliniaQuest quest = StateManager.getInstance().getConfigurationManager().getQuest(aquestid);
			if (quest == null)
				throw new InvalidNPCEventSettingException("Invalid quest id");
			} catch (CoreStateInitException e)
			{
				throw new InvalidNPCEventSettingException("State not initialised");
			}
			setAwardsQuest(aquestid);
			break;
		default:
			throw new InvalidNPCEventSettingException(
					"Invalid NPC Event setting. Valid Options are: triggerdata,chatresponse,interactiontype,requiresquest,awardsquest,requiresquestflag,awardsquestflag");
		}
	}

	@Override
	public int getNpcId() {
		return npcId;
	}

	@Override
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

}
