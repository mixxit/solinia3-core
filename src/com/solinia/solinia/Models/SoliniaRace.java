package com.solinia.solinia.Models;

import com.solinia.solinia.Interfaces.ISoliniaRace;

public class SoliniaRace implements ISoliniaRace {

	private int id;
	private boolean isadmin = true;
	private String name = "";
	
	private int strength = 1;
	private int stamina = 1;
	private int agility = 1;
	private int dexterity = 1;
	private int wisdom = 1;
	private int intelligence = 1;
	private int charisma = 1;

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
	public void setAdmin(boolean isadmin) {
		// TODO Auto-generated method stub
		this.isadmin = isadmin;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	@Override
	public int getStamina() {
		return stamina;
	}

	@Override
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	@Override
	public int getAgility() {
		return agility;
	}

	@Override
	public void setAgility(int agility) {
		this.agility = agility;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	@Override
	public int getWisdom() {
		return wisdom;
	}

	@Override
	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
	}

	@Override
	public int getIntelligence() {
		return intelligence;
	}

	@Override
	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	@Override
	public int getCharisma() {
		return charisma;
	}

	@Override
	public void setCharisma(int charisma) {
		this.charisma = charisma;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
