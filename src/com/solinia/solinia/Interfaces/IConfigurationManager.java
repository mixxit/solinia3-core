package com.solinia.solinia.Interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.WorldWidePerk;

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
		add("gold_helmet");
		add("gold_chestplate");
		add("gold_leggings");
		add("gold_boots");
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

	ISoliniaItem getItem(ItemStack itemStack);
	int getNextItemId();

	void addItem(ISoliniaItem item);

	ISoliniaSpell getSpell(int Id);

	List<ISoliniaSpell> getSpells();

	List<ISoliniaItem> getSpellItem(int Id);

	void updateItem(ISoliniaItem item);

	List<ISoliniaFaction> getFactions();

	List<ISoliniaNPC> getNPCs();

	ISoliniaFaction getFaction(int Id);

	ISoliniaNPC getNPC(int Id);

	void editNPC(int npcid, String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException;

	List<ISoliniaNPCMerchant> getNPCMerchants();

	List<ISoliniaLootTable> getLootTables();

	List<ISoliniaLootDrop> getLootDrops();

	ISoliniaNPCMerchant getNPCMerchant(int Id);

	ISoliniaLootTable getLootTable(int Id);

	ISoliniaLootDrop getLootDrop(int Id);

	void addNPCMerchant(ISoliniaNPCMerchant merchant);

	void addLootTable(ISoliniaLootTable table);

	void addLootDrop(ISoliniaLootDrop drop);

	int getNextFactionId();

	void addFaction(SoliniaFaction faction);

	int getNextNPCId();

	void addNPC(SoliniaNPC npc);

	ISoliniaLootDrop getLootDrop(String lootdropname);

	ISoliniaLootTable getLootTable(String loottablename);

	int getNextLootDropId();

	int getNextLootTableId();

	void editSpell(int spellid, String setting, String value) throws InvalidSpellSettingException, NumberFormatException, CoreStateInitException;

	int getNextNPCMerchantId();

	ISoliniaNPCMerchant getNPCMerchant(String merchantlistname);

	void editItem(int itemid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidItemSettingException;

	List<ISoliniaSpawnGroup> getSpawnGroups();

	ISoliniaSpawnGroup getSpawnGroup(int Id);

	int getNextSpawnGroupId();

	void addSpawnGroup(ISoliniaSpawnGroup spawngroup);

	void updateSpawnGroup(ISoliniaSpawnGroup spawngroup);

	ISoliniaSpawnGroup getSpawnGroup(String upperCase);

	void updateSpawnGroupLoc(int spawngroupid, Location location);

	void reloadPerks();

	List<WorldWidePerk> getWorldWidePerks();
}
