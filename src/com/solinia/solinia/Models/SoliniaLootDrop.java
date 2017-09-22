package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;

public class SoliniaLootDrop implements ISoliniaLootDrop {
	private int id;
	private String name;
	private List<ISoliniaLootDropEntry> entries = new ArrayList<ISoliniaLootDropEntry>();	

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
	public List<ISoliniaLootDropEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void setEntries(List<ISoliniaLootDropEntry> entries) {
		this.entries = entries;
	}
}
