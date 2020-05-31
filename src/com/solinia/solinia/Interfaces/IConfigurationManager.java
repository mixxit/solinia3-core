package com.solinia.solinia.Interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Exceptions.InvalidWorldSettingException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;
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
import com.solinia.solinia.Models.CharacterCreation;
import com.solinia.solinia.Models.ConfigSettings;
import com.solinia.solinia.Models.EQItem;
import com.solinia.solinia.Models.EQMob;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.Flaw;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.NPCSpellList;
import com.solinia.solinia.Models.Oath;
import com.solinia.solinia.Models.PlayerState;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaGod;
import com.solinia.solinia.Models.SoliniaMetrics;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.Trait;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaQuest;
import com.solinia.solinia.Models.SoliniaWorld;

public interface IConfigurationManager {
	
	public List<String> WeaponMaterials = new ArrayList<String>() {
		private static final long serialVersionUID = 4699754456865065676L;
	{ 
		add("WOOD_SWORD"); 
		add("WOODEN_SWORD"); 
		add("STONE_SWORD"); 
		add("IRON_SWORD"); 
		add("GOLD_SWORD"); 
		add("GOLDEN_SWORD"); 
		add("DIAMOND_SWORD"); 
		add("WOOD_AXE"); 
		add("WOODEN_AXE"); 
		add("STONE_AXE"); 
		add("IRON_AXE"); 
		add("GOLD_AXE");
		add("GOLDEN_AXE");
		add("DIAMOND_AXE"); 
		add("WOOD_SHOVEL"); 
		add("WOODEN_SHOVEL"); 
		add("STONE_SHOVEL"); 
		add("IRON_SHOVEL"); 
		add("GOLD_SHOVEL"); 
		add("GOLDEN_SHOVEL"); 
		add("DIAMOND_SHOVEL"); 
		add("WOOD_SPADE"); 
		add("WOODEN_SPADE"); 
		add("STONE_SPADE"); 
		add("IRON_SPADE"); 
		add("GOLD_SPADE"); 
		add("GOLDEN_SPADE"); 
		add("DIAMOND_SPADE"); 
		add("WOOD_HOE");
		add("WOODEN_HOE");
		add("STONE_HOE");
		add("IRON_HOE");
		add("GOLD_HOE");
		add("GOLDEN_HOE");
		add("DIAMOND_HOE");
		add("WOOD_PICKAXE");
		add("WOODEN_PICKAXE");
		add("STONE_PICKAXE");
		add("IRON_PICKAXE");
		add("GOLD_PICKAXE");
		add("GOLDEN_PICKAXE");
		add("DIAMOND_PICKAXE");
		}};
		
	public List<String> MiningMaterials = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 8581780572124514662L;

	{
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
	
	public List<String> ForagingMaterials = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = -8843603586328168943L;

	{
		add("GRASS");
		add("LONG_GRASS");
		add("DOUBLE_PLANT");
		add("RED_ROSE");
		add("YELLOW_FLOWER");
		add("CHORUS_FLOWER");
	}};
	
	public List<String> LumberingMaterials = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 3746642763197880376L;

	{
		add("LOG");
		add("LOG_2");
		add("HUGE_MUSHROOM_1");
		add("HUGE_MUSHROOM_2");
		add("LEAVES");
		add("LEAVES_2");
	}};
	
	public List<String> ArmourMaterials = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 3600386258510381521L;

	{ 
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
		add("GOLDEN_HELMET");
		add("GOLDEN_CHESTPLATE");
		add("GOLDEN_LEGGINGS");
		add("GOLDEN_BOOTS");
		}};
		

		public List<String> HandMaterials = new ArrayList<String>() {/**
			 * 
			 */
			private static final long serialVersionUID = -3933524056654169744L;

		{ 
			add("WOOD_SWORD");
			add("WOODEN_SWORD");
			add("STONE_SWORD");
			add("IRON_SWORD");
			add("GOLD_SWORD");
			add("GOLDEN_SWORD");
			add("DIAMOND_SWORD");
			add("WOOD_AXE");
			add("WOODEN_AXE");
			add("STONE_AXE");
			add("IRON_AXE");
			add("GOLD_AXE");
			add("GOLDEN_AXE");
			add("DIAMOND_AXE");
			add("WOOD_SPADE");
			add("WOODEN_SPADE");
			add("STONE_SPADE");
			add("IRON_SPADE");
			add("GOLD_SPADE");
			add("GOLDEN_SPADE");
			add("DIAMOND_SPADE");
			add("WOOD_SHOVEL");
			add("WOODEN_SHOVEL");
			add("STONE_SHOVEL");
			add("IRON_SHOVEL");
			add("GOLD_SHOVEL");
			add("GOLDEN_SHOVEL");
			add("DIAMOND_SHOVEL");
			add("WOOD_HOE");
			add("WOODEN_HOE");
			add("STONE_HOE");
			add("IRON_HOE");
			add("GOLD_HOE");
			add("GOLDEN_HOE");
			add("DIAMOND_HOE");
			add("WOOD_PICKAXE");
			add("WOODEN_PICKAXE");
			add("STONE_PICKAXE");
			add("IRON_PICKAXE");
			add("GOLD_PICKAXE");
			add("GOLDEN_PICKAXE");
			add("DIAMOND_PICKAXE");
			add("SHIELD");
			add("BOW");
			add("CROSSBOW");
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

	boolean editNPC(int npcid, String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, IOException;

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

	void addSpawnGroup(ISoliniaSpawnGroup spawngroup, boolean providerReload);

	void updateSpawnGroup(ISoliniaSpawnGroup spawngroup);

	ISoliniaSpawnGroup getSpawnGroup(String upperCase);

	void updateSpawnGroupLoc(int spawngroupid, Location location);

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

	ISoliniaQuest addQuest(SoliniaQuest quest);

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

	int getNextAlignmentId();

	ISoliniaAlignment getAlignment(int Id);

	ISoliniaAlignment getAlignment(String alignment);

	void addAlignment(String upperCase) throws Exception;

	List<ISoliniaPlayer> getCharacters();

	List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID);

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

	Oath getOath(int oathId);

	void editQuest(CommandSender sender, int id, String setting, String value, String[] additional)
			throws InvalidQuestSettingException, NumberFormatException, CoreStateInitException;

	void editGod(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidGodSettingException;

	int getNextGodId();

	List<ISoliniaGod> getGods();

	ISoliniaGod addGod(SoliniaGod god);

	boolean isGodNameFree(String godNameUpperCase);

	ISoliniaGod getGod(int Id);

	ISoliniaGod getGod(String name);

	CharacterCreation getCharacterCreationChoices();

	void setConfigSettings(ConfigSettings configSettings);

	ConfigSettings getConfigSettings();

	int getMaxLevel();

	int getNextSpellId();

	ConcurrentHashMap<UUID, String> getQueuedCastingPercentPackets();

	void setQueuedCastingPercentPackets(ConcurrentHashMap<UUID, String> queuedCastingPercentPackets);

	void setQueuedEffectsPackets(ConcurrentHashMap<UUID, String> queuedEffectsPackets);

	ConcurrentHashMap<UUID, String> getQueuedEquipSlotsPackets();

	void setQueuedEquipSlotsPackets(ConcurrentHashMap<UUID, String> queuedEquipSlotsPackets);

	ConcurrentHashMap<UUID, String> getQueuedMemorisedSpellsPackets();

	void setQueuedMemorisedSpellsPackets(ConcurrentHashMap<UUID, String> queuedMemorisedSpellsPackets);

	ConcurrentHashMap<UUID, String> getQueueSpellbookPagePackets();

	void setQueueSpellbookPagePackets(ConcurrentHashMap<UUID, String> queueSpellbookPagePackets);

	ConcurrentHashMap<UUID, String> getQueuedEffectsPackets();

	ConcurrentHashMap<UUID, String> getQueuedCharCreationPackets();

	void setQueuedCharCreationPackets(ConcurrentHashMap<UUID, String> queuedCharCreationPackets);

	ConcurrentHashMap<UUID, String> getQueueMobVitalsPackets(int queueNumber) throws Exception;

	void setQueueMobVitalsPackets(int queueNumber, ConcurrentHashMap<UUID, String> queueMobVitalsPackets) throws Exception;

	ConcurrentHashMap<UUID, Float> getLastSentPlayerManaPercent();

	void setLastSentPlayerManaPercent(ConcurrentHashMap<UUID, Float> lastSentPlayerManaPercent);

	double getMaxExperience();

	List<Fellowship> getFellowships();

	Fellowship addFellowship(Fellowship fellowship);

	int getNextFellowshipId();

	Fellowship getFellowship(int Id);

	void removeFellowship(int id);

	List<PlayerState> getPlayerStates();

	PlayerState addPlayerState(PlayerState playerstate);

	PlayerState getPlayerState(UUID Id);

	List<ISoliniaPlayer> getActiveCharacters();

	List<Integer> getActiveCharacterCharacterIds();

	int getNextPlayerId();

	ISoliniaPlayer getCharacterById(Integer characterId);

	IRepository<ISoliniaPatch> getPatchesRepo();

	SoliniaMetrics getSoliniaMetrics();

	ISoliniaNPC addNPC(SoliniaNPC npc, boolean reloadProvider);

	List<EQItem> getImportItems(String name);

	List<EQMob> getImportNPCs(String name);

	EQItem getImportItems(int id);

	EQMob getImportNPCs(int id);

	boolean isNPCSpellsChanged();

	void setNPCSpellsChanged(boolean npcspellsChanged);
}
