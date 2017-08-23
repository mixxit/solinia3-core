package com.solinia.solinia.Interfaces;

public interface ISoliniaRace {

	public String getName();

	public boolean isAdmin();

	public int getId();

	void setAdmin(boolean isadmin);

	void setAgility(int agility);

	void setCharisma(int charisma);

	void setDexterity(int dexterity);

	void setIntelligence(int intelligence);

	void setStamina(int stamina);

	void setStrength(int strength);

	void setWisdom(int wisdom);

	public int getAgility();

	public int getCharisma();

	public int getDexterity();

	public int getIntelligence();

	public int getStamina();

	public int getStrength();

	public int getWisdom();
}
