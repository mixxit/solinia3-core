package com.solinia.solinia.Models;

import org.bukkit.command.Command;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Managers.StateManager;

public class PlayerQuest {
	private int questId;
	private boolean complete = false;
	public int getQuestId() {
		return questId;
	}
	public void setQuestId(int questId) {
		this.questId = questId;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public ISoliniaQuest getQuest() {
		try
		{
			return StateManager.getInstance().getConfigurationManager().getQuest(getQuestId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}

}
