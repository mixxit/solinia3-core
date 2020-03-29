package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.IPersistable;

public class SoliniaAccountClaim implements IPersistable {
	private int id;
	private String mcname;
	private int itemid;
	private boolean claimed = false;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMcname() {
		return mcname;
	}
	public void setMcname(String mcname) {
		this.mcname = mcname;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public boolean isClaimed() {
		return claimed;
	}
	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}
}
