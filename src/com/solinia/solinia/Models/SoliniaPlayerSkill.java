package com.solinia.solinia.Models;

public class SoliniaPlayerSkill {

	private int value;
	private String skillname;
	
	public SoliniaPlayerSkill(String skillname, int value) {
		this.skillname = skillname;
		this.value = value;
	}

	public String getSkillName() {
		return skillname;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}
