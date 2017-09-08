package com.solinia.solinia.Factories;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
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

	public static void CreateLootDropFromMerchant(ISoliniaNPCMerchant merchant, String name, int count, boolean always, int chance) throws CoreStateInitException
	{
		if (merchant.getEntries().size() == 0)
			return;

		if (StateManager.getInstance().getConfigurationManager().getLootDrop(name.toUpperCase()) != null)
			return;
		
		CreateLootDrop(name);
		
		ISoliniaLootDrop lootdrop = StateManager.getInstance().getConfigurationManager().getLootDrop(name.toUpperCase());
		for(ISoliniaNPCMerchantEntry item : merchant.getEntries())
		{
			CreateLootDropItem(lootdrop.getId(), item.getId(), count, always, chance);
		}
	}
	
	public static void CreateLootTable(String loottablename) throws CoreStateInitException {
		SoliniaLootTable loottable = new SoliniaLootTable();
		loottable.setId(StateManager.getInstance().getConfigurationManager().getNextLootTableId());
		loottable.setName(loottablename);
		StateManager.getInstance().getConfigurationManager().addLootTable(loottable);
	}

	public static void CreateLootTableDrop(int loottableid, int lootdropid) throws CoreStateInitException {
		ISoliniaLootTable loottabletable = StateManager.getInstance().getConfigurationManager().getLootTable(loottableid);
		SoliniaLootTableEntry loottable = new SoliniaLootTableEntry();
		int id = 1;
		for(ISoliniaLootTableEntry entry : loottabletable.getEntries())
		{
			if (entry.getId() > id)
				id = entry.getId() + 1;
		}
		
		loottable.setId(id);
		loottable.setLoottableid(loottableid);
		loottable.setLootdropid(lootdropid);
		StateManager.getInstance().getConfigurationManager().getLootTable(loottableid).getEntries().add(loottable);
	}

	public static void CreateLootDropItem(int lootdropid, int itemid, int count, boolean always, int chance) throws CoreStateInitException {
		ISoliniaLootDrop lootdroptable = StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid);
		SoliniaLootDropEntry lootdrop = new SoliniaLootDropEntry();
		int id = 1;
		for(ISoliniaLootDropEntry entry : lootdroptable.getEntries())
		{
			if (entry.getId() > id)
				id = entry.getId() + 1;
		}
		
		lootdrop.setId(id);
		lootdrop.setLootdropid(lootdropid);
		lootdrop.setItemid(itemid);
		lootdrop.setCount(count);
		lootdrop.setAlways(always);
		lootdrop.setChance(chance);
		StateManager.getInstance().getConfigurationManager().getLootDrop(lootdropid).getEntries().add(lootdrop);
	}

}
