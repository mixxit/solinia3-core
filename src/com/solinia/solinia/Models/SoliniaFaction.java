package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaFaction;

public class SoliniaFaction implements ISoliniaFaction {
	private int id;
	private String name;
	private int base;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
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
	public int getBase() {
		return base;
	}
	
	@Override
	public void setBase(int base) {
		this.base = base;
	}
}
