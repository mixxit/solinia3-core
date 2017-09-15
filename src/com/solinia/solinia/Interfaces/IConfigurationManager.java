package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
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
		add("LEATHER_HELMET");
		add("LEATHER_CHESTPLATE");
		add("LEATHER_LEGGINGS");
		add("LEATHER_BOOTS");
		add("CHAINMAIL_HELMET");
		add("CHAINMAIL_CHESTPLATE");
		add("CHAINMAIL_LEGGINGS");
		add("CHAINMAIL_BOOTS");
		add("IRON_HELMET");
		add("IRON_CHESTPLATE");
		add("IRON_LEGGINGS");
		add("IRON_BOOTS");
		add("DIAMOND_HELMET");
		add("DIAMOND_CHESTPLATE");
		add("DIAMOND_LEGGINGS");
		add("DIAMOND_BOOTS");
		add("GOLD_HELMET");
		add("GOLD_CHESTPLATE");
		add("GOLD_LEGGINGS");
		add("GOLD_BOOTS");
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

	ISoliniaFaction getFaction(String factionname);

	ISoliniaNPC getNPC(int Id);

	void editNPC(int npcid, String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, IOException;

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

	ISoliniaFaction addFaction(SoliniaFaction faction);

	int getNextNPCId();

	ISoliniaNPC addNPC(SoliniaNPC npc);

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

	ISoliniaFaction getFaction(int Id);

	void editClass(int classid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidClassSettingException;

	List<ISoliniaSpell> getSpellsByClassId(int classId);

	List<ISoliniaSpell> getSpellsByClassIdAndMaxLevel(int classId, int level);
}
