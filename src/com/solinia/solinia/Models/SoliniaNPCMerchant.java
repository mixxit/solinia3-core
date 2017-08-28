package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;

public class SoliniaNPCMerchant implements ISoliniaNPCMerchant {
	private int id;
	private String name;

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
}
