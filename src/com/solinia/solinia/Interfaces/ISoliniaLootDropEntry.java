package com.solinia.solinia.Interfaces;

public interface ISoliniaLootDropEntry {

	int getId();

	void setId(int id);

	int getItemid();

	void setItemid(int itemid);

	boolean isAlways();

	void setAlways(boolean always);

	int getCount();

	void setCount(int count);

	int getChance();

	void setChance(int chance);

	int getLootdropid();

	void setLootdropid(int lootdropid);

}
