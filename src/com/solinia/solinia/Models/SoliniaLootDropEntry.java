package com.solinia.solinia.Models;


import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;

public class SoliniaLootDropEntry implements ISoliniaLootDropEntry {
	private int id;
	private int itemid;
	private boolean always;
	private int count;
	private int chance;
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int getItemid() {
		return itemid;
	}
	
	@Override
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	
	@Override
	public boolean isAlways() {
		return always;
	}
	
	@Override
	public void setAlways(boolean always) {
		this.always = always;
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public int getChance() {
		return chance;
	}
	
	@Override
	public void setChance(int chance) {
		this.chance = chance;
	}
}
