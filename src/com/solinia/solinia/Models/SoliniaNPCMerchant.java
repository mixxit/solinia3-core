package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;

public class SoliniaNPCMerchant implements ISoliniaNPCMerchant {
	private int id;
	private String name;
	private List<ISoliniaNPCMerchantEntry> entries = new ArrayList<ISoliniaNPCMerchantEntry>();	

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<ISoliniaNPCMerchantEntry> getEntries() {
		return entries;
	}

	@Override
	public void setEntries(List<ISoliniaNPCMerchantEntry> entries) {
		this.entries = entries;
	}
}
