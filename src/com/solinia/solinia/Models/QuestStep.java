package com.solinia.solinia.Models;

public class QuestStep {
	private int sequence = 0;
	private String description = "";
	private boolean isOperatorCreated = true;
	private int questId;
	private String triggerQuestFlag = "";
	private String completeQuestFlag = "";
	public QuestStep(int stepId) {
		this.sequence = stepId;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isOperatorCreated() {
		return isOperatorCreated;
	}
	public void setOperatorCreated(boolean isOperatorCreated) {
		this.isOperatorCreated = isOperatorCreated;
	}
	public int getQuestId() {
		return questId;
	}
	public void setQuestId(int questId) {
		this.questId = questId;
	}
	public String getCompleteQuestFlag() {
		return completeQuestFlag;
	}
	public void setCompleteQuestFlag(String completeQuestFlag) {
		this.completeQuestFlag = completeQuestFlag;
	}
	public String getTriggerQuestFlag() {
		return triggerQuestFlag;
	}
	public void setTriggerQuestFlag(String triggerQuestFlag) {
		this.triggerQuestFlag = triggerQuestFlag;
	}
}
