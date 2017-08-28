package com.solinia.solinia.Interfaces;

import java.util.List;

public interface ISoliniaLootTable {

	int getId();

	void setId(int id);

	List<ISoliniaLootTableEntry> getEntries();

	void setEntries(List<ISoliniaLootTableEntry> entries);

	String getName();

	void setName(String name);


}
