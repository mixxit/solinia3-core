package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;

public class SoliniaNPCMerchantEntry implements ISoliniaNPCMerchantEntry,IPersistable {
	private int id;
	private int itemid;
	private int merchantid;
	private int temporaryquantitylimit;
	
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

	@Override
	public int getTemporaryquantitylimit() {
		return temporaryquantitylimit;
	}

	@Override
	public void setTemporaryquantitylimit(int temporaryquantitylimit) {
		this.temporaryquantitylimit = temporaryquantitylimit;
	}
}
