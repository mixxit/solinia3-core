package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;

public class UniversalMerchantEntry {
	private int itemid;
	private int temporaryquantitylimit;
	
	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public int getTemporaryquantitylimit() {
		return temporaryquantitylimit;
	}

	public void setTemporaryquantitylimit(int temporaryquantitylimit) {
		this.temporaryquantitylimit = temporaryquantitylimit;
	}
	
	public static UniversalMerchantEntry FromNPCMerchantEntry(ISoliniaNPCMerchantEntry npcMerchantEntry)
	{
		UniversalMerchantEntry entry = new UniversalMerchantEntry();
		entry.itemid = npcMerchantEntry.getItemid();
		entry.temporaryquantitylimit = npcMerchantEntry.getTemporaryquantitylimit();
		return entry;
	}
}
