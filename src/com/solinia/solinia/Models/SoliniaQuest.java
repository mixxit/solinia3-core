package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import net.md_5.bungee.api.ChatColor;

public class SoliniaQuest implements ISoliniaQuest {
	private int Id;
	private String name = "";
	private String questFlagCompletion = "";
	private ConcurrentHashMap<Integer, QuestStep> questSteps = new ConcurrentHashMap<Integer, QuestStep>();
	
	@Override
	public int getId() {
		return Id;
	}
	
	@Override
	public void setId(int id) {
		Id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getQuestFlagCompletion() {
		return questFlagCompletion;
	}

	@Override
	public void setQuestFlagCompletion(String questFlagCompletion) {
		this.questFlagCompletion = questFlagCompletion;
	}

	@Override
	public void editSetting(CommandSender sender, String setting, String value, String[] additional) throws InvalidQuestSettingException {
		//StateManager.getInstance().getConfigurationManager().setQuestsChanged(true);
		
		switch (setting.toLowerCase()) {
		case "name":
			setName(value.toUpperCase());
			break;
		case "completequestflag":
			setQuestFlagCompletion(value.toUpperCase());
			break;
		case "step":
			int stepId = Integer.parseInt(value);
			if (additional.length == 0)
			{
				if (this.getQuestSteps().get(stepId) != null)
				{
					sendQuestStep(sender, stepId);
					return;
				}
				else {
					throw new InvalidQuestSettingException("Invalid quest step. This step sequence doesnt exist: " + stepId + " - did you want to create a quest step? /editquest [questid] step [stepid] description [textualdescription]");
				}
			}
			
			String property = additional[0];
			List<String> data = new ArrayList<String>();
			for(int i = 1; i < additional.length; i++)
				data.add(additional[i]);
			
			setQuestStep(stepId,property,data);
			break;
		default:
			throw new InvalidQuestSettingException(
					"Invalid setting. Valid Options are: name,completequestflag,step");
		}
		
		//StateManager.getInstance().getConfigurationManager().setQuestsChanged(true);
	}

	@Override
	public void setQuestStep(int stepId, String property, List<String> data) throws InvalidQuestSettingException {
		if (this.getQuestSteps().get(stepId) == null)
			this.getQuestSteps().put(stepId, new QuestStep(stepId));
		
		QuestStep step = this.getQuestSteps().get(stepId);
		switch(property)
		{
			case "description":
				step.setDescription(StringUtils.join(data, " "));
				break;
			case "completequestflag":
				step.setCompleteQuestFlag(data.get(0));
				break;
			case "triggerquestflag":
				step.setTriggerQuestFlag(data.get(0));
				break;
			case "remove":
				removeQuestStep(stepId);
				break;
			default:
				throw new InvalidQuestSettingException("Invalid quest step. Valid Options are: description,remove,triggerquestflag,completequestflag");
		}
	}

	private void removeQuestStep(int stepId) {
		this.getQuestSteps().remove(stepId);
	}

	@Override
	public void sendQuestSettingsToSender(CommandSender sender) {
		// TODO Auto-generated method stub
		sender.sendMessage(ChatColor.RED + "Quest Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- completequestflag: " + ChatColor.GOLD + getQuestFlagCompletion() + ChatColor.RESET);
		sender.sendMessage("- step:");
		for(int questStepId : this.getQuestSteps().keySet())
		{
			sendQuestStep(sender, questStepId);
		}
	}

	private void sendQuestStep(CommandSender sender, int questStepId) {
		QuestStep questStep = this.getQuestSteps().get(questStepId);
		sender.sendMessage("  [" +  questStep.getSequence() + "] - Description: " + questStep.getDescription() + " triggerquestflag: " + questStep.getTriggerQuestFlag() + " completequestflag: " + questStep.getCompleteQuestFlag());
	}

	@Override
	public ConcurrentHashMap<Integer, QuestStep> getQuestSteps() {
		return questSteps;
	}

	@Override
	public void setQuestSteps(ConcurrentHashMap<Integer, QuestStep> questSteps) {
		this.questSteps = questSteps;
	}
}
