package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaClass;

public class SoliniaClass implements ISoliniaClass {

	private int id;
	private boolean isadmin = true;
	private String name = "";
	private String description = "";
	private List<Integer> validRaces = new ArrayList<Integer>();
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setAdmin(boolean adminonly) {
		this.isadmin = adminonly;
	}
	
	@Override
	public boolean isAdmin()
	{
		return this.isadmin;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public List<Integer> getValidRaces() {
		return validRaces;
	}

	@Override
	public void setValidRaces(List<Integer> validRaces) {
		this.validRaces = validRaces;
	}

}
