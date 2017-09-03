package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;

public class SoliniaNPCMerchantEntry implements ISoliniaNPCMerchantEntry {
	private int id;
	private int itemid;
	private int merchantid;
	
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
	public int getMerchantid() {
		return merchantid;
	}

	@Override
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
}
