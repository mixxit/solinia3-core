package com.solinia.solinia.Models;

import java.util.UUID;

import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaAAEffect;

public class SoliniaAAEffect implements ISoliniaAAEffect,IPersistable {
	private int id;
	private UUID primaryUUID = UUID.randomUUID();
	private UUID secondaryUUID = UUID.randomUUID();

	private int aaid;
	private int slot;
	private int effectid;
	private int base1;
	private int base2;
	
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
	public int getAaid() {
		return aaid;
	}
	
	@Override
	public void setAaid(int aaid) {
		this.aaid = aaid;
	}
	
	@Override
	public int getSlot() {
		return slot;
	}
	
	@Override
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	@Override
	public int getEffectid() {
		return effectid;
	}
	
	@Override
	public void setEffectid(int effectid) {
		this.effectid = effectid;
	}
	
	@Override
	public int getBase1() {
		return base1;
	}
	
	@Override
	public void setBase1(int base1) {
		this.base1 = base1;
	}
	
	@Override
	public int getBase2() {
		return base2;
	}
	
	@Override
	public void setBase2(int base2) {
		this.base2 = base2;
	}
}
