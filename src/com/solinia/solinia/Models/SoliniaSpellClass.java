package com.solinia.solinia.Models;

public class SoliniaSpellClass {
	private String classname;
	private int minlevel;
	public SoliniaSpellClass() {
	}
	public SoliniaSpellClass(String classname, Integer minlevel) {
		this.classname = classname;
		this.minlevel = minlevel;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public int getMinlevel() {
		return minlevel;
	}
	public void setMinlevel(int minlevel) {
		this.minlevel = minlevel;
	}
}
