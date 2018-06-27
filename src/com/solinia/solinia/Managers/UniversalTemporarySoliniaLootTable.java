package com.solinia.solinia.Managers;

import com.solinia.solinia.Interfaces.ISoliniaLootTable;

public class UniversalTemporarySoliniaLootTable {
	public long distance;
	public ISoliniaLootTable soliniaLootTable;
	
	public UniversalTemporarySoliniaLootTable(ISoliniaLootTable soliniaLootTable, long distance)
	{
		this.distance = distance;
		this.soliniaLootTable = soliniaLootTable;
	}

}
