package com.solinia.solinia.Interfaces;

import java.util.ArrayList;
import java.util.List;

public interface IConfigurationManager {
	
	public List<String> WeaponMaterials = new ArrayList<String>() {{ 
		add("WOOD_SWORD"); 
		add("STONE_SWORD"); 
		add("IRON_SWORD"); 
		add("GOLD_SWORD"); 
		add("DIAMOND_SWORD"); 
		add("WOOD_AXE"); 
		add("STONE_AXE"); 
		add("IRON_AXE"); 
		add("GOLD_AXE");
		add("DIAMOND_AXE"); 
		add("WOOD_SPADE"); 
		add("STONE_SPADE"); 
		add("IRON_SPADE"); 
		add("GOLD_SPADE"); 
		add("DIAMOND_SPADE"); 
		}};
		
	public List<String> ArmourMaterials = new ArrayList<String>() {{ 
		add("leather_helmet");
		add("leather_chestplate");
		add("leather_leggings");
		add("leather_boots");
		add("chainmail_helmet");
		add("chainmail_chestplate");
		add("chainmail_leggings");
		add("chainmail_boots");
		add("iron_helmet");
		add("iron_chestplate");
		add("iron_leggings");
		add("iron_boots");
		add("diamond_helmet");
		add("diamond_chestplate");
		add("diamond_leggings");
		add("diamond_boots");
		add("golden_helmet");
		add("golden_chestplate");
		add("golden_leggings");
		add("golden_boots");
		}};

	List<ISoliniaRace> getRaces();

	ISoliniaRace getRace(int raceId);

	ISoliniaRace getRace(String race);

	void commit();

	void addRace(ISoliniaRace race);

	int getNextRaceId();

	List<ISoliniaClass> getClasses();

	ISoliniaClass getClassObj(int classId);

	ISoliniaClass getClassObj(String classname);

	void addClass(ISoliniaClass classobj);

	int getNextClassId();

	boolean isValidRaceClass(int raceId, int classId);

	void addRaceClass(int raceId, int classId);

	List<ISoliniaItem> getItems();

	ISoliniaItem getItem(int Id);

	int getNextItemId();

	void addItem(ISoliniaItem item);
}
