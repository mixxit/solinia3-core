package com.solinia.solinia.Models;

public class SoliniaPlayerSkill {

	private int value;
	private String skillname;
	private SkillType skillType;
	
	public SoliniaPlayerSkill(String skillname, SkillType skillType, int value) {
		this.skillname = skillname;
		this.skillType = skillType;
		this.value = value;
	}

	public String getSkillName() {
		return skillname;
	}
	
	public SkillType getSkillType() {
		return skillType;
	}
	
	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}
