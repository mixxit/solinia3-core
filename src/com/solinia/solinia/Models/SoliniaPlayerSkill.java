package com.solinia.solinia.Models;

public class SoliniaPlayerSkill {

	private int value;
	private SkillType skillType;
	private String skillname; // to be removed
	
	public SoliniaPlayerSkill(SkillType skillType, int value) {
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
