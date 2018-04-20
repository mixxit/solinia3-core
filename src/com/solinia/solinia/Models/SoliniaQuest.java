package com.solinia.solinia.Models;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import net.md_5.bungee.api.ChatColor;

public class SoliniaQuest implements ISoliniaQuest {
	private int Id;
	private String name = "";
	private boolean isOperatorCreated = true;
	private String questFlagCompletion = "";
	
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
	public boolean isOperatorCreated() {
		return isOperatorCreated;
	}

	@Override
	public void setOperatorCreated(boolean isOperatorCreated) {
		this.isOperatorCreated = isOperatorCreated;
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
	public void editSetting(String setting, String value, String[] additional) throws InvalidQuestSettingException {
		//StateManager.getInstance().getConfigurationManager().setQuestsChanged(true);
		
		switch (setting.toLowerCase()) {
		case "name":
			setName(value.toUpperCase());
			break;
		case "completequestflag":
			setQuestFlagCompletion(value.toUpperCase());
			break;
		default:
			throw new InvalidQuestSettingException(
					"Invalid setting. Valid Options are: name,completequestflag");
		}
		
		//StateManager.getInstance().getConfigurationManager().setQuestsChanged(true);
	}

	@Override
	public void sendQuestSettingsToSender(CommandSender sender) {
		// TODO Auto-generated method stub
		sender.sendMessage(ChatColor.RED + "Spell Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- completequestflag: " + ChatColor.GOLD + getQuestFlagCompletion() + ChatColor.RESET);
	}
}
