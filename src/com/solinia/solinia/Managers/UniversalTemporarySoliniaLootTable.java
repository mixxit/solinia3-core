package com.solinia.solinia.Managers;

import com.solinia.solinia.Interfaces.ISoliniaLootTable;

public class UniversalTemporarySoliniaLootTable {
	public int distance;
	public ISoliniaLootTable soliniaLootTable;
	
	public UniversalTemporarySoliniaLootTable(ISoliniaLootTable soliniaLootTable, int distance)
	{
		this.distance = distance;
		this.soliniaLootTable = soliniaLootTable;
	}

}
