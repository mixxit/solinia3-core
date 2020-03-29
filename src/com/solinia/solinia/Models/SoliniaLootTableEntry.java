package com.solinia.solinia.Models;

import java.util.UUID;

import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;

public class SoliniaLootTableEntry implements ISoliniaLootTableEntry,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private int lootdropid;
	private int loottableid;
	
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
	public int getLootdropid() {
		return lootdropid;
	}

	@Override
	public void setLootdropid(int lootdropid) {
		this.lootdropid = lootdropid;
	}
	
	@Override
	public int getLoottableid(int loottableid) {
		return this.loottableid;
	}

	@Override
	public void setLoottableid(int loottableid) {
		this.loottableid = loottableid;
	}
	
}
