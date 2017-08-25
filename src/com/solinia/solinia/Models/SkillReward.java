package com.solinia.solinia.Models;

public class SkillReward {
	private String skillname;
	private int xp;
	
	public SkillReward(String skillname, int xp)
	{
		this.skillname = skillname;
		this.xp = xp;
	}
	
	public String getSkillname() {
		return skillname;
	}
	public void setSkillname(String skillname) {
		this.skillname = skillname;
	}
	public int getXp() {
		return xp;
	}
	public void setXp(int xp) {
		this.xp = xp;
	}
}
