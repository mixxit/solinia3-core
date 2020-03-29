package com.solinia.solinia.Models;

public class SkillReward {
	private SkillType skillType;
	private int xp;
	
	public SkillReward(SkillType skillType, int xp)
	{
		this.skillType = skillType;
		this.xp = xp;
	}
	
	public SkillType getSkillType() {
		return skillType;
	}
	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}
	public int getXp() {
		return xp;
	}
	public void setXp(int xp) {
		this.xp = xp;
	}
}
