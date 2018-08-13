package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaAAEffect;

public class SoliniaAAEffect implements ISoliniaAAEffect {
	private int id;
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
