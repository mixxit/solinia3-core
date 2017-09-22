package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;

public class SoliniaAAAbility implements ISoliniaAAAbility {
	private int id;
	private String sysname;
	private String name;
	private List<String> classes;
	private List<ISoliniaAARank> ranks = new ArrayList<ISoliniaAARank>();
	
	@Override
	public String getSysname() {
		return sysname;
	}
	
	@Override
	public void setSysname(String sysname) {
		this.sysname = sysname;
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
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public List<String> getClasses() {
		return classes;
	}

	@Override
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	@Override
	public List<ISoliniaAARank> getRanks() {
		return ranks;
	}

	@Override
	public void setRanks(List<ISoliniaAARank> ranks) {
		this.ranks = ranks;
	}

	@Override
	public boolean canClassUseAbility(ISoliniaClass iSoliniaClass) {
		if (iSoliniaClass == null)
			return false;
		
		if (getClasses().size() == 0)
			return true;
		
		if (getClasses().contains(iSoliniaClass.getName().toUpperCase()))
			return true;
		
		return false;
	}
}
