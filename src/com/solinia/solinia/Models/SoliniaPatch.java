package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Interfaces.ISoliniaPatch;

public class SoliniaPatch implements ISoliniaPatch {
	private int id;
	private List<String> classes = new ArrayList<String>();
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
}
