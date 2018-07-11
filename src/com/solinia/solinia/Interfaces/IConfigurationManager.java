package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Exceptions.InvalidWorldSettingException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNPCMerchantListSettingException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Models.Bond;
import com.solinia.solinia.Models.Flaw;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.NPCSpellList;
import com.solinia.solinia.Models.Oath;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.Trait;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaQuest;
import com.solinia.solinia.Models.SoliniaWorld;
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
		
	public List<String> MiningMaterials = new ArrayList<String>() {{
		add("COAL_ORE");
		add("DIAMOND_ORE");
		add("EMERALD_ORE");
		add("ENDER_STONE");
		add("GLOWSTONE");
		add("GOLD_ORE");
		add("HARD_CLAY");
		add("IRON_ORE");
		add("LAPIS_ORE");
		add("MOSSY_COBBLESTONE");
		add("NETHERRACK");
		add("OBSIDIAN");
		add("PACKED_ICE");
		add("REDSTONE_ORE");
		add("SANDSTONE");
		add("STAINED_CLAY");
		add("STONE");
		add("QUARTZ_ORE");
		add("GLOWING_REDSTONE_ORE");
	}};
	
	public List<String> ForagingMaterials = new ArrayList<String>() {{
		add("GRASS");
		add("LONG_GRASS");
		add("DOUBLE_PLANT");
		add("RED_ROSE");
		add("YELLOW_FLOWER");
		add("CHORUS_FLOWER");
	}};
	
	public List<String> LumberingMaterials = new ArrayList<String>() {{
		add("LOG");
		add("LOG_2");
		add("HUGE_MUSHROOM_1");
		add("HUGE_MUSHROOM_2");
		add("LEAVES");
		add("LEAVES_2");
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
		
		public List<String> HandMaterials = new ArrayList<String>() {{ 
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
			add("SHIELD");
			add("BOW");
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
	
	ISoliniaItem getItemByDurability(ItemStack itemStack);
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

	void editSpell(int spellid, String setting, String value, String[] additional) throws InvalidSpellSettingException, NumberFormatException, CoreStateInitException;

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

	ISoliniaNPC getPetNPCByName(String name);

	List<ISoliniaAAAbility> getAAAbilities();

	ISoliniaAAAbility getAAAbility(int Id);

	ISoliniaAARank getAARank(int seekRankId);

	List<ISoliniaPatch> getPatches();

	List<ISoliniaAlignment> getAlignments();
	
	void editRace(int raceid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidRaceSettingException;

	List<ISoliniaAARank> getAARankCache();

	List<Integer> getAASpellRankCache(int spellId);

	void editSpawnGroup(int spawngroupid, String setting, String value) throws NumberFormatException, InvalidSpawnGroupSettingException, CoreStateInitException, IOException;

	void editFaction(int factionid, String setting, String value)
			throws NumberFormatException, InvalidFactionSettingException, CoreStateInitException, IOException;

	List<ISoliniaQuest> getQuests();

	ISoliniaQuest getQuest(int questId);

	int getNextQuestId();

	ISoliniaQuest addQuest(SoliniaQuest quest, boolean operatorCreated);

	void editLootDrop(int lootDropid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidLootDropSettingException;

	void editLootTable(int loottableid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidLootTableSettingException;

	void editNpcTriggerEvent(int npcid, String triggertext, String setting, String value) throws InvalidNPCEventSettingException;

	List<Integer> getLootDropIdsWithItemId(int itemId);

	List<Integer> getLootTablesWithLootDrops(List<Integer> lootDropIds);

	List<ISoliniaItem> getItemsByPartialName(String itemMatch);

	void editAAAbility(int aaid, String setting, String value) throws InvalidAASettingException;

	ISoliniaAARank getAARankCache(int rankId);

	void resetAARankRepository();

	void updateEmperors();

	int getNextAlignmentId();

	ISoliniaAlignment getAlignment(int Id);

	ISoliniaAlignment getAlignment(String alignment);

	void addAlignment(String upperCase) throws Exception;

	List<ISoliniaPlayer> getCharacters();

	List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID);

	ISoliniaPlayer getCharacterByCharacterUUID(UUID characterUUID);

	void commitPlayerToCharacterLists(ISoliniaPlayer player);

	IRepository<ISoliniaPlayer> getCharactersRepository();

	IRepository<NPCSpellList> getNpcspelllistsRepository();

	void setNpcspelllistsRepository(IRepository<NPCSpellList> npcspelllistsRepository);

	NPCSpellList getNPCSpellList(int Id);

	List<NPCSpellList> getNPCSpellLists();

	List<SoliniaAccountClaim> getAccountClaims(String mcname);

	void removeClaim(int id);

	SoliniaAccountClaim getAccountClaim(String mcname, int seekClaimId);

	SoliniaAccountClaim getAccountClaim(int seekClaimId);

	void addAccountClaim(SoliniaAccountClaim claim);

	int getNextAccountClaimId();

	List<SoliniaAccountClaim> getAccountClaims();

	ISoliniaItem getItemByOxygen(ItemStack itemStack);

	void addZone(SoliniaZone zone);

	int getNextZoneId();

	List<SoliniaZone> getZones();

	List<SoliniaZone> getZones(String name);

	void editZone(int zoneid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidZoneSettingException;

	SoliniaZone getZone(int Id);

	SoliniaZone getZone(String name);

	int getNextCraftId();

	List<SoliniaCraft> getCrafts();

	void addCraft(SoliniaCraft craft);

	SoliniaCraft getCraft(int Id);

	void editCraft(int craftid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidCraftSettingException;

	SoliniaCraft getCraft(String name);

	List<SoliniaCraft> getCrafts(int itemid1, int itemid2);

	boolean isCraftsHasComponent(int id);

	int getNextWorldId();

	List<SoliniaWorld> getWorlds();

	void addWorld(SoliniaWorld world);

	SoliniaWorld getWorld(String name);

	SoliniaWorld getWorld(int Id);

	void editWorld(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidWorldSettingException;

	boolean isWorldNameFree(String upperCase);

	boolean isSpellsChanged();

	void setSpellsChanged(boolean spellsChanged);

	void addSpell(ISoliniaSpell spell);

	boolean isItemsChanged();

	void setItemsChanged(boolean itemsChanged);

	void editQuest(int id, String setting, String value, String[] additional)
			throws InvalidQuestSettingException, NumberFormatException, CoreStateInitException;

	List<ISoliniaAAAbility> getAAbilitiesBySysname(String sysname);

	void editNPCMerchantList(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidNPCMerchantListSettingException;

	void commitCsvs();

	void editAlignment(String alignmentid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidAlignmentSettingException;

	List<Oath> getOaths();

	List<Trait> getTraits();

	List<Bond> getBonds();

	List<Flaw> getFlaws();

	void setBonds(List<Bond> bonds);

	void setFlaws(List<Flaw> flaws);

	void setOaths(List<Oath> oaths);

	void setTraits(List<Trait> traits);

	List<Ideal> getIdeals();

	void setIdeals(List<Ideal> ideals);

	Bond getBond(int bondId);

	Flaw getFlaw(int flawId);

	Ideal getIdeal(int idealId);

	Trait getTrait(int firstTraitId);
}
