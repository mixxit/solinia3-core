package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaQuest;

public class SoliniaQuest implements ISoliniaQuest {
	private int Id;
	private String name = "";
	
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
}
