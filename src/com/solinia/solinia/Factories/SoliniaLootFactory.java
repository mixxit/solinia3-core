package com.solinia.solinia.Factories;

import java.util.ArrayList;
import java.util.List;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaLootDrop;
import com.solinia.solinia.Models.SoliniaLootDropEntry;
import com.solinia.solinia.Models.SoliniaLootTable;
import com.solinia.solinia.Models.SoliniaLootTableEntry;

public class SoliniaLootFactory {

	public static void CreateLootDrop(String lootdropname) throws CoreStateInitException {
		SoliniaLootDrop lootdrop = new SoliniaLootDrop();
		lootdrop.setId(StateManager.getInstance().getConfigurationManager().getNextLootDropId());
		lootdrop.setName(lootdropname);
		StateManager.getInstance().getConfigurationManager().addLootDrop(lootdrop);
	}

	public static void CreateLootTable(String loottablename) throws CoreStateInitException {
		SoliniaLootTable loottable = new SoliniaLootTable();
		loottable.setId(StateManager.getInstance().getConfigurationManager().getNextLootTableId());
		loottable.setName(loottablename);
		StateManager.getInstance().getConfigurationManager().addLootTable(loottable);
	}

	public static void CreateLootTableDrop(int loottableid, int lootdropid) throws CoreStateInitException {
		SoliniaLootTableEntry loottable = new SoliniaLootTableEntry();
		loottable.setId(StateManager.getInstance().getConfigurationManager().getNextLootTableEntryId());
		loottable.setLoottableid(loottableid);
		loottable.setLootdropid(lootdropid);
		StateManager.getInstance().getConfigurationManager().addLootTableEntry(loottable);
	}

	public static void CreateLootDropItem(int lootdropid, int itemid, int count, boolean always, int chance) throws CoreStateInitException {
		SoliniaLootDropEntry lootdrop = new SoliniaLootDropEntry();
		lootdrop.setId(StateManager.getInstance().getConfigurationManager().getNextLootDropEntryId());
		lootdrop.setLootdropid(lootdropid);
		lootdrop.setItemid(itemid);
		lootdrop.setCount(count);
		lootdrop.setAlways(always);
		lootdrop.setChance(chance);
		StateManager.getInstance().getConfigurationManager().addLootDropEntry(lootdrop);
	}

}
