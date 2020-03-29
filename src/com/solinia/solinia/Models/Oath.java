package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

public class Oath {
	public Oath(int id, String oathname, ArrayList<String> tenets)
	{
		this.id = id;
		this.oathname = oathname;
		this.tenets = tenets;
	}
	
	public int id;
	public String oathname;
	public List<String> tenets = new ArrayList<String>();
}
