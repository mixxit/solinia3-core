package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaRace;

public class SoliniaRace implements ISoliniaRace {

	private int id;
	private boolean isadmin;
	private String name;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return this.isadmin;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

}
