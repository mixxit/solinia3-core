package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;

public class SoliniaLootTableEntry implements ISoliniaLootTableEntry {
	private int id;
	private int lootdropid;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getLootdropid() {
		return lootdropid;
	}

	@Override
	public void setLootdropid(int lootdropid) {
		this.lootdropid = lootdropid;
	}

}
