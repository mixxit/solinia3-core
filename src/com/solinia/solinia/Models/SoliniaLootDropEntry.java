package com.solinia.solinia.Models;


import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Managers.StateManager;

public class SoliniaLootDropEntry implements ISoliniaLootDropEntry,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private int itemid;
	private boolean always;
	private int count;
	private int chance;
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
	public UUID getPrimaryUUID() {
		// TODO Auto-generated method stub
		return this.primaryUUID;
	}
	@Override
	public void setPrimaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.primaryUUID = uuid;
	}
	@Override
	public UUID getSecondaryUUID() {
		// TODO Auto-generated method stub
		return this.secondaryUUID;
	}
	@Override
	public void setSecondaryUUID(UUID uuid) {
		// TODO Auto-generated method stub
		this.secondaryUUID = uuid;
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

	@Override
	public int getLootdropid() {
		return this.lootdropid;
	}
	
	@Override
	public void setLootdropid(int lootdropid) {
		this.lootdropid = lootdropid;
	}

	@Override
	public ISoliniaItem getItem() {
		if (this.itemid < 1)
			return null;
		
		try
		{
			ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(this.itemid);
			return item;
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
}
