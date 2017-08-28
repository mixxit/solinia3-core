package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaLootDrop {

	int getId();

	void setId(int id);

	List<ISoliniaLootDropEntry> getEntries();

	void setEntries(List<ISoliniaLootDropEntry> entries);

	String getName();

	void setName(String name);

	

}
