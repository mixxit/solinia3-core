package com.solinia.solinia.Managers;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Events.SoliniaNPCUpdatedEvent;
import com.solinia.solinia.Events.SoliniaSpawnGroupUpdatedEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidItemSettingException;
import com.solinia.solinia.Exceptions.InvalidLootDropSettingException;
import com.solinia.solinia.Exceptions.InvalidLootTableSettingException;
import com.solinia.solinia.Exceptions.InvalidNPCEventSettingException;
import com.solinia.solinia.Exceptions.InvalidNPCMerchantListSettingException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.InvalidQuestSettingException;
import com.solinia.solinia.Exceptions.InvalidRaceSettingException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Exceptions.InvalidWorldSettingException;
import com.solinia.solinia.Exceptions.InvalidZoneSettingException;
import com.solinia.solinia.Exceptions.SoliniaWorldCreationException;
import com.solinia.solinia.Factories.SoliniaWorldFactory;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.NPCSpellList;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaAlignment;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaQuest;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Models.WorldWidePerk;
import com.solinia.solinia.Repositories.JsonAAAbilityRepository;
import com.solinia.solinia.Repositories.JsonAccountClaimRepository;
import com.solinia.solinia.Repositories.JsonAlignmentRepository;
import com.solinia.solinia.Repositories.JsonCharacterListRepository;
import com.solinia.solinia.Repositories.JsonCraftRepository;
import com.solinia.solinia.Repositories.JsonFactionRepository;
import com.solinia.solinia.Repositories.JsonZoneRepository;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import com.solinia.solinia.Repositories.JsonLootDropRepository;
import com.solinia.solinia.Repositories.JsonLootTableRepository;
import com.solinia.solinia.Repositories.JsonNPCMerchantRepository;
import com.solinia.solinia.Repositories.JsonNPCRepository;
import com.solinia.solinia.Repositories.JsonNPCSpellListRepository;
import com.solinia.solinia.Repositories.JsonPatchRepository;
import com.solinia.solinia.Repositories.JsonQuestRepository;
import com.solinia.solinia.Repositories.JsonSpawnGroupRepository;
import com.solinia.solinia.Repositories.JsonWorldRepository;
import com.solinia.solinia.Repositories.JsonWorldWidePerkRepository;

public class ConfigurationManager implements IConfigurationManager {

	private IRepository<ISoliniaRace> raceRepository;
	private IRepository<ISoliniaClass> classRepository;
	private IRepository<ISoliniaItem> itemRepository;
	private IRepository<ISoliniaSpell> spellRepository;
	private IRepository<ISoliniaFaction> factionRepository;
	private IRepository<ISoliniaNPC> npcRepository;
	private IRepository<ISoliniaNPCMerchant> npcmerchantRepository;
	private IRepository<ISoliniaLootTable> loottableRepository;
	private IRepository<ISoliniaLootDrop> lootdropRepository;
	private IRepository<ISoliniaSpawnGroup> spawngroupRepository;
	private IRepository<WorldWidePerk> perkRepository;
	private IRepository<ISoliniaAAAbility> aaabilitiesRepository;
	private ConcurrentHashMap<Integer, ISoliniaAARank> aarankcache = new ConcurrentHashMap<Integer, ISoliniaAARank>();
	private ConcurrentHashMap<Integer, List<Integer>> spellaarankcache = new ConcurrentHashMap<Integer, List<Integer>>();
	private IRepository<ISoliniaQuest> questRepository;
	private IRepository<ISoliniaPatch> patchesRepository;
	private IRepository<ISoliniaAlignment> alignmentsRepository;
	private IRepository<ISoliniaPlayer> characterlistsRepository;
	private IRepository<NPCSpellList> npcspelllistsRepository;
	private IRepository<SoliniaAccountClaim> accountClaimsRepository;
	private IRepository<SoliniaZone> zonesRepository;
	private IRepository<SoliniaCraft> craftRepository;
	private IRepository<SoliniaWorld> worldRepository;
	
	// Commit tracking
	private boolean spellsChanged = false;
	private boolean itemsChanged = false;

	public ConfigurationManager(IRepository<ISoliniaRace> raceContext, IRepository<ISoliniaClass> classContext,
			IRepository<ISoliniaItem> itemContext, IRepository<ISoliniaSpell> spellContext,
			JsonFactionRepository factionContext, JsonNPCRepository npcContext,
			JsonNPCMerchantRepository npcmerchantContext, JsonLootTableRepository loottableContext,
			JsonLootDropRepository lootdropContext, JsonSpawnGroupRepository spawngroupContext, 
			JsonWorldWidePerkRepository perkContext, JsonAAAbilityRepository aaabilitiesContext, 
			JsonPatchRepository patchesContext, JsonQuestRepository questsContext, JsonAlignmentRepository alignmentsContext, 
			JsonCharacterListRepository characterlistsContext, JsonNPCSpellListRepository npcspelllistsContext, 
			JsonAccountClaimRepository accountClaimsContext, JsonZoneRepository zonesContext, JsonCraftRepository craftContext, JsonWorldRepository worldContext) {
		this.raceRepository = raceContext;
		this.classRepository = classContext;
		this.itemRepository = itemContext;
		this.spellRepository = spellContext;
		this.factionRepository = factionContext;
		this.npcRepository = npcContext;
		this.npcmerchantRepository = npcmerchantContext;
		this.loottableRepository = loottableContext;
		this.lootdropRepository = lootdropContext;
		this.spawngroupRepository = spawngroupContext;
		this.perkRepository = perkContext;
		this.aaabilitiesRepository = aaabilitiesContext;
		resetAARankRepository();
		this.patchesRepository = patchesContext;
		this.questRepository = questsContext;
		this.alignmentsRepository = alignmentsContext;
		this.characterlistsRepository = characterlistsContext;
		this.setNpcspelllistsRepository(npcspelllistsContext);
		this.accountClaimsRepository = accountClaimsContext;
		this.zonesRepository = zonesContext;
		this.craftRepository = craftContext;
		this.worldRepository = worldContext;
	}
	
	@Override
	public void commit() {
		this.raceRepository.commit();
		this.classRepository.commit();
		
		if (isItemsChanged() == true)
		{
			this.itemRepository.commit();
			this.itemsChanged = false;
		}
		this.factionRepository.commit();
		this.npcRepository.commit();
		this.npcmerchantRepository.commit();
		this.loottableRepository.commit();
		this.lootdropRepository.commit();
		
		// Only commit on change
		if (isSpellsChanged() == true)
		{
			this.spellRepository.commit();
			this.spellsChanged = false;
		}
		
		this.spawngroupRepository.commit();
		this.aaabilitiesRepository.commit();
		this.questRepository.commit();
		this.alignmentsRepository.commit();
		
		commitPlayersToCharacterLists();
		
		this.characterlistsRepository.commit();
		this.npcspelllistsRepository.commit();
		this.accountClaimsRepository.commit();
		this.zonesRepository.commit();
		this.craftRepository.commit();
		this.worldRepository.commit();
	}
	
	@Override 
	public IRepository<ISoliniaPlayer> getCharactersRepository()
	{
		return this.characterlistsRepository;		
	}
	
	@Override
	public void resetAARankRepository()
	{
		this.aarankcache.clear();
		this.spellaarankcache.clear();
		for (ISoliniaAAAbility ability : getAAAbilities())
		{
			for (ISoliniaAARank rank : ability.getRanks())
			{
				this.aarankcache.put(rank.getId(), rank);
				if (this.spellaarankcache.get(rank.getSpell()) == null)
					this.spellaarankcache.put(rank.getSpell(), new ArrayList<Integer>());
					
				if (this.spellaarankcache.get(rank.getSpell()).contains(rank.getId()))
					continue;
				
				this.spellaarankcache.get(rank.getSpell()).add(rank.getId());
			}
		}
		System.out.println("* AA Rank and SpelltoAARank cache has been reset");
	}
	
	@Override
	public List<Integer> getLootTablesWithLootDrops(List<Integer> lootDropIds)
	{
		List<Integer> lootTables = new ArrayList<Integer>();
		
		try
		{
			// Find all loot tables with an item in its list
			for (ISoliniaLootTable soliniaLootTable : StateManager.getInstance().getConfigurationManager()
					.getLootTables()) {
				for (ISoliniaLootTableEntry soliniaLootTableEntry : soliniaLootTable.getEntries()) {
					if (!lootDropIds.contains(soliniaLootTableEntry.getLootdropid()))
						continue;

					if (lootTables.contains(soliniaLootTable.getId()))
						continue;

					lootTables.add(soliniaLootTable.getId());
				}
			}

		} catch (CoreStateInitException e)
		{
			// do nothing
			System.out.println(e.getMessage());
		}
		
		return lootTables;
	}
	
	@Override
	public List<Integer> getLootDropIdsWithItemId(int itemId)
	{
		List<Integer> lootDrops = new ArrayList<Integer>();
		
		try
		{
			for(ISoliniaLootDrop lootDrop : StateManager.getInstance().getConfigurationManager().getLootDrops())
			{
				for(ISoliniaLootDropEntry lootDropEntry : lootDrop.getEntries())
				{
					if (lootDropEntry.getItemid() != itemId)
						continue;
					
					if (!lootDrops.contains(lootDrop.getId()))
						lootDrops.add(lootDrop.getId());
				}
			}
		} catch (CoreStateInitException e)
		{
			// do nothing
		}
		
		return lootDrops;
	}

	@Override
	public List<Integer> getAASpellRankCache(int spellId)
	{
		if (spellaarankcache.get(spellId) == null)
			return new ArrayList<Integer>();
		
		return spellaarankcache.get(spellId);
	}

	@Override
	public List<ISoliniaAARank> getAARankCache()
	{
		if (aarankcache.values() == null)
			return new ArrayList<ISoliniaAARank>();
		
		return new ArrayList<ISoliniaAARank>(aarankcache.values());
	}

	@Override
	public List<ISoliniaPatch> getPatches() {
		// TODO Auto-generated method stub
		return patchesRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaAlignment> getAlignments() {
		return alignmentsRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<SoliniaWorld> getWorlds() {
		return worldRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaNPCMerchant> getNPCMerchants() {
		// TODO Auto-generated method stub
		return npcmerchantRepository.query(q -> q.getId() > 0);
	}

	@Override
	public List<ISoliniaLootTable> getLootTables() {
		// TODO Auto-generated method stub
		return loottableRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaSpawnGroup> getSpawnGroups() {
		// TODO Auto-generated method stub
		return spawngroupRepository.query(q -> q.getId() > 0);
	}

	@Override
	public List<ISoliniaLootDrop> getLootDrops() {
		// TODO Auto-generated method stub
		return lootdropRepository.query(q -> q.getId() > 0);
	}

	@Override
	public List<ISoliniaFaction> getFactions() {
		// TODO Auto-generated method stub
		return factionRepository.query(q -> q.getName() != null);
	}

	@Override
	public List<ISoliniaNPC> getNPCs() {
		// TODO Auto-generated method stub
		return npcRepository.query(q -> q.getName() != null);
	}

	@Override
	public ISoliniaNPC getPetNPCByName(String name) {
		List<ISoliniaNPC> results = npcRepository.query(q -> q.isPet() == true && q.getName().equals(name));
		if (results.size() != 1)
			return null;
		
		return results.get(0);
	}
	
	@Override
	public List<ISoliniaRace> getRaces() {
		// TODO Auto-generated method stub
		return raceRepository.query(q -> q.getName() != null);
	}

	@Override
	public List<ISoliniaClass> getClasses() {
		// TODO Auto-generated method stub
		return classRepository.query(q -> q.getName() != null);
	}

	@Override
	public List<ISoliniaItem> getItems() {
		// TODO Auto-generated method stub
		return itemRepository.query(q -> q.getId() > 0);
	}

	@Override
	public ISoliniaRace getRace(int Id) {
		return raceRepository.getByKey(Id);
	}
	
	@Override
	public ISoliniaAlignment getAlignment(int Id) {
		return alignmentsRepository.getByKey(Id);
	}

	@Override
	public ISoliniaFaction getFaction(int Id) {
		return factionRepository.getByKey(Id);
	}
	
	@Override
	public ISoliniaFaction getFaction(String faction) {
		// TODO Auto-generated method stub
		List<ISoliniaFaction> list = factionRepository.query(q -> q.getName().equals(faction));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}
	
	@Override
	public ISoliniaAlignment getAlignment(String alignment) {
		// TODO Auto-generated method stub
		List<ISoliniaAlignment> list = alignmentsRepository.query(q -> q.getName().toUpperCase().equals(alignment.toUpperCase()));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}

	@Override
	public ISoliniaNPCMerchant getNPCMerchant(int Id) {
		return npcmerchantRepository.getByKey(Id);
	}

	@Override
	public ISoliniaLootTable getLootTable(int Id) {
		return loottableRepository.getByKey(Id);
	}
	
	@Override
	public ISoliniaSpawnGroup getSpawnGroup(int Id) {
		return spawngroupRepository.getByKey(Id);
	}

	@Override
	public ISoliniaLootDrop getLootDrop(int Id) {
		return lootdropRepository.getByKey(Id);
	}

	@Override
	public ISoliniaNPC getNPC(int Id) {
		return npcRepository.getByKey(Id);
	}

	@Override
	public ISoliniaClass getClassObj(int classId) {
		return classRepository.getByKey(classId);
	}

	@Override
	public ISoliniaRace getRace(String race) {

		List<ISoliniaRace> races = raceRepository.query(q -> q.getName().toUpperCase().equals(race.toUpperCase()));
		if (races.size() > 0)
			return races.get(0);

		return null;
	}

	@Override
	public ISoliniaSpawnGroup getSpawnGroup(String spawngroupname) {

		List<ISoliniaSpawnGroup> spawngroups = spawngroupRepository.query(q -> q.getName().toUpperCase().equals(spawngroupname.toUpperCase()));
		if (spawngroups.size() > 0)
			return spawngroups.get(0);

		return null;
	}
	
	@Override
	public ISoliniaItem getItem(int Id) {
		return itemRepository.getByKey(Id);
	}

	@Override
	public ISoliniaSpell getSpell(int Id) {
		return spellRepository.getByKey(Id);
	}

	@Override
	public ISoliniaItem getItemByDurability(ItemStack itemStack) {
		
		int durability = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
		
		int id = 0;
		if (durability > 999)
		{
			id = durability - 1000;
		}
		
		if (durability < 0)
		{
			id = (32768 + (durability + 32768)) - 1000;
		}
		
		return getItem(id);
	}
	
	@Override
	public ISoliniaItem getItem(ItemStack itemStack) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		
		String soliniaid = compound.getString("soliniaid");
		
		if (soliniaid == null || soliniaid.equals(""))
			return null;
		
		return getItem(Integer.parseInt(soliniaid));
	}


	@Override
	public ISoliniaItem getItemByOxygen(ItemStack itemStack) {
		int Id = (itemStack.getEnchantmentLevel(Enchantment.OXYGEN) - 1000);
		return getItem(Id);
	}
	
	@Override
	public ISoliniaClass getClassObj(String classname) {

		List<ISoliniaClass> classes = classRepository
				.query(q -> q.getName().toUpperCase().equals(classname.toUpperCase()));
		if (classes.size() > 0)
			return classes.get(0);

		return null;
	}

	private void commitPlayersToCharacterLists() {
		try {
			int count = 0;
			for(ISoliniaPlayer player : StateManager.getInstance().getPlayerManager().getPlayers())
			{
				commitPlayerToCharacterLists(player);
				
				count++;
			}
			System.out.println("Commited " + count + " characters to the CharacterList repository");
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void commitPlayerToCharacterLists(ISoliniaPlayer player) {
		this.characterlistsRepository.update(player);
	}

	@Override
	public void addNPCMerchant(ISoliniaNPCMerchant merchant) {
		this.npcmerchantRepository.add(merchant);

	}
	
	@Override
	public void addSpell(ISoliniaSpell spell) {
		this.spellRepository.add(spell);
	}

	@Override
	public void addLootTable(ISoliniaLootTable table) {
		this.loottableRepository.add(table);

	}

	@Override
	public void addLootDrop(ISoliniaLootDrop drop) {
		this.lootdropRepository.add(drop);

	}

	@Override
	public void addRace(ISoliniaRace race) {
		this.raceRepository.add(race);

	}
	
	@Override
	public void addWorld(SoliniaWorld world) {
		this.worldRepository.add(world);

	}

	@Override
	public void addClass(ISoliniaClass classobj) {
		this.classRepository.add(classobj);

	}

	@Override
	public int getNextRaceId() {
		int maxRace = 0;
		for (ISoliniaRace race : getRaces()) {
			if (race.getId() > maxRace)
				maxRace = race.getId();
		}

		return maxRace + 1;
	}
	
	@Override
	public int getNextWorldId() {
		int max = 0;
		for (SoliniaWorld entry : getWorlds()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}
	
	@Override
	public int getNextAlignmentId() {
		int maxAlignment = 0;
		for (ISoliniaAlignment alignment : getAlignments()) {
			if (alignment.getId() > maxAlignment)
				maxAlignment = alignment.getId();
		}

		return maxAlignment + 1;
	}

	@Override
	public int getNextClassId() {
		int maxClass = 0;
		for (ISoliniaClass classInstance : getClasses()) {
			if (classInstance.getId() > maxClass)
				maxClass = classInstance.getId();
		}

		return maxClass + 1;
	}

	@Override
	public int getNextItemId() {
		int maxItem = 0;
		for (ISoliniaItem itemInstance : getItems()) {
			if (itemInstance.getId() > maxItem)
				maxItem = itemInstance.getId();
		}

		return maxItem + 1;
	}
	
	@Override
	public int getNextNPCMerchantId() {
		int max = 0;
		for (ISoliniaNPCMerchant merchantInstance : getNPCMerchants()) {
			if (merchantInstance.getId() > max)
				max = merchantInstance.getId();
		}

		return max + 1;
	}
	
	@Override
	public int getNextSpawnGroupId() {
		int max = 0;
		for (ISoliniaSpawnGroup instance : getSpawnGroups()) {
			if (instance.getId() > max)
				max = instance.getId();
		}

		return max + 1;
	}

	@Override
	public boolean isValidRaceClass(int raceId, int classId) {
		ISoliniaClass classes = getClassObj(classId);
		if (classes == null)
			return false;

		if (classes.getValidRaces() == null)
			return false;

		if (classes.getValidRaces().contains(raceId))
			return true;

		return false;
	}

	@Override
	public void addRaceClass(int raceId, int classId) {
		if (getClassObj(classId) == null)
			return;

		if (getRace(raceId) == null)
			return;

		List<Integer> validRaces = getClassObj(classId).getValidRaces();
		if (validRaces == null)
			validRaces = new ArrayList<Integer>();

		if (validRaces.contains(raceId))
			return;

		validRaces.add(raceId);
		getClassObj(classId).setValidRaces(validRaces);

	}

	@Override
	public void addItem(ISoliniaItem item) {
		this.itemRepository.add(item);
	}
	
	@Override
	public void addSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		this.spawngroupRepository.add(spawngroup);
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroup.getId()));
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}

	@Override
	public List<ISoliniaSpell> getSpells() {
		return spellRepository.query(q -> q.getId() != null);
	}

	@Override
	public List<ISoliniaItem> getSpellItem(int Id) {
		return itemRepository.query(q -> q.isSpellscroll() == true && q.getAbilityid() == Id);
	}

	@Override
	public void updateItem(ISoliniaItem item) {
		this.itemRepository.update(item);
	}
	
	@Override
	public void updateSpawnGroup(ISoliniaSpawnGroup spawngroup) {
		this.spawngroupRepository.update(spawngroup);
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroup.getId()));
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}

	@Override
	public void editNPC(int npcid, String setting, String value)
			throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException, IOException {
		getNPC(npcid).editSetting(setting, value);

		SoliniaNPCUpdatedEvent soliniaevent = new SoliniaNPCUpdatedEvent(getNPC(npcid), true);
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}
	
	@Override
	public void editItem(int itemid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidItemSettingException {
		getItem(itemid).editSetting(setting, value);
	}
	
	@Override
	public void editNPCMerchantList(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidNPCMerchantListSettingException {
		getNPCMerchant(id).editSetting(setting, value);
	}
	
	@Override
	public void editZone(int zoneid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidZoneSettingException {
		getZone(zoneid).editSetting(setting, value);
	}
	
	@Override
	public void editCraft(int craftid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidCraftSettingException {
		getCraft(craftid).editSetting(setting, value);
	}
	
	@Override
	public void editWorld(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidWorldSettingException {
		getWorld(id).editSetting(setting, value);
	}
	
	@Override
	public void editLootDrop(int lootdropid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidLootDropSettingException {
		getLootDrop(lootdropid).editSetting(setting, value);
	}
	
	@Override
	public void editLootTable(int loottableid, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidLootTableSettingException {
		getLootTable(loottableid).editSetting(setting, value);
	}

	@Override
	public int getNextFactionId() {
		int max = 0;
		for (ISoliniaFaction entry : getFactions()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}

	@Override
	public ISoliniaFaction addFaction(SoliniaFaction faction) {
		this.factionRepository.add(faction);
		return getFaction(faction.getId());
	}

	@Override
	public int getNextNPCId() {
		int max = 0;
		for (ISoliniaNPC entry : getNPCs()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}

	@Override
	public ISoliniaNPC addNPC(SoliniaNPC npc) {
		this.npcRepository.add(npc);

		SoliniaNPCUpdatedEvent soliniaevent = new SoliniaNPCUpdatedEvent(npc, true);
		Bukkit.getPluginManager().callEvent(soliniaevent);
		return getNPC(npc.getId());
	}

	@Override
	public ISoliniaLootDrop getLootDrop(String lootdropname) {
		// TODO Auto-generated method stub
		List<ISoliniaLootDrop> list = lootdropRepository.query(q -> q.getName().equals(lootdropname));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}
	
	@Override
	public SoliniaZone getZone(String name) {
		// TODO Auto-generated method stub
		List<SoliniaZone> list = zonesRepository.query(q -> q.getName().equals(name));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}
	
	@Override
	public SoliniaCraft getCraft(String name) {
		// TODO Auto-generated method stub
		List<SoliniaCraft> list = craftRepository.query(q -> q.getRecipeName().equals(name));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}
	
	@Override
	public SoliniaWorld getWorld(String name) {
		// TODO Auto-generated method stub
		List<SoliniaWorld> list = worldRepository.query(q -> q.getName().equals(name));
		if (list.size() > 0)
			return list.get(0);

		try
		{
			for(World world : Bukkit.getWorlds())
			{
				if (!world.getName().toUpperCase().equals(name.toUpperCase()))
					continue;
	
				SoliniaWorld createdWorld = SoliniaWorldFactory.Create(world.getName());
				System.out.println("World Created: " + world.getName());
				return createdWorld;
			}
		} catch (CoreStateInitException e)
		{
			return null;
		} catch (SoliniaWorldCreationException e) {
			return null;
		}
		
		return null;
	}

	@Override
	public ISoliniaLootTable getLootTable(String loottablename) {
		// TODO Auto-generated method stub
		List<ISoliniaLootTable> list = loottableRepository.query(q -> q.getName().equals(loottablename));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}
	
	@Override
	public int getNextLootTableId() {
		int max = 0;
		for (ISoliniaLootTable entry : getLootTables()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}
	
	@Override
	public int getNextLootDropId() {
		int max = 0;
		for (ISoliniaLootDrop entry : getLootDrops()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}
	
	@Override
	public int getNextQuestId() {
		int max = 0;
		for (ISoliniaQuest entry : getQuests()) {
			if (entry.getId() > max)
				max = entry.getId();
		}

		return max + 1;
	}

	@Override
	public void editSpell(int spellid, String setting, String value, String[] additional) 
		throws InvalidSpellSettingException, NumberFormatException, CoreStateInitException {
			getSpell(spellid).editSetting(setting, value, additional);
	}

	@Override
	public void editQuest(int id, String setting, String value, String[] additional) 
		throws InvalidQuestSettingException, NumberFormatException, CoreStateInitException {
			getQuest(id).editSetting(setting, value, additional);
	}
	
	@Override
	public ISoliniaNPCMerchant getNPCMerchant(String merchantlistname) {
		List<ISoliniaNPCMerchant> list = npcmerchantRepository.query(q -> q.getName().toUpperCase().equals(merchantlistname.toUpperCase()));
		if (list.size() > 0)
			return list.get(0);

		return null;
	}

	@Override
	public void updateSpawnGroupLoc(int spawngroupid, Location location) {
		if (getSpawnGroup(spawngroupid) == null)
			return;
		
		getSpawnGroup(spawngroupid).setLocation(location);
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroupid));
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}

	@Override
	public void reloadPerks() {
		perkRepository.reload();
	}

	@Override
	public List<WorldWidePerk> getWorldWidePerks() {
		return perkRepository.query(q -> q.getId() > 0);
	}

	@Override
	public void editClass(int classid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidClassSettingException 
	{
		getClassObj(classid).editSetting(setting, value);
	}
	
	@Override
	public void editRace(int raceid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidRaceSettingException 
	{
		getRace(raceid).editSetting(setting, value);
	}

	@Override
	public void editAlignment(String alignmentid, String setting, String value) throws NumberFormatException, CoreStateInitException, InvalidAlignmentSettingException 
	{
		getAlignment(alignmentid).editSetting(setting, value);
	}
	
	@Override
	public List<ISoliniaSpell> getSpellsByClassId(int classId) {
		List<ISoliniaSpell> returnSpells = new ArrayList<ISoliniaSpell>();
		
		ISoliniaClass classObj;
		
		try {
			classObj = StateManager.getInstance().getConfigurationManager().getClassObj(classId);
		} catch (CoreStateInitException e) {
			return returnSpells;
		}
		
		for (ISoliniaSpell spell : getSpells())
		{
			boolean addSpell = false;
			for (SoliniaSpellClass spellclass : spell.getAllowedClasses())
			{
				if (spellclass.getClassname().toUpperCase().equals(classObj.getName().toUpperCase()))
				{
					addSpell = true;
					break;
				}
			}
			
			if (addSpell == true)
				returnSpells.add(spell);
		}
		
		return returnSpells;
	}

	@Override
	public List<ISoliniaSpell> getSpellsByClassIdAndMaxLevel(int classId, int level) {
		List<ISoliniaSpell> returnSpells = new ArrayList<ISoliniaSpell>();
		
		ISoliniaClass classObj;
		
		try {
			classObj = StateManager.getInstance().getConfigurationManager().getClassObj(classId);
		} catch (CoreStateInitException e) {
			return returnSpells;
		}
		
		for (ISoliniaSpell spell : getSpells())
		{
			boolean addSpell = false;
			for (SoliniaSpellClass spellclass : spell.getAllowedClasses())
			{
				if (spellclass.getMinlevel() > level)
					continue;
				
				if (spellclass.getClassname().toUpperCase().equals(classObj.getName().toUpperCase()))
				{
					addSpell = true;
					break;
				}
			}
			
			if (addSpell == true)
				returnSpells.add(spell);
		}
		
		return returnSpells;
	}
	
	@Override
	public ISoliniaAAAbility getAAAbility(int Id) {
		return aaabilitiesRepository.getByKey(Id);
	}
	
	@Override
	public SoliniaZone getZone(int Id) {
		return zonesRepository.getByKey(Id);
	}
	
	@Override
	public SoliniaCraft getCraft(int Id) {
		return craftRepository.getByKey(Id);
	}
	
	@Override
	public SoliniaWorld getWorld(int Id) {
		return worldRepository.getByKey(Id);
	}
	
	@Override
	public ISoliniaAARank getAARank(int seekRankId) {
		ISoliniaAARank aarank = null;
		try {
			for (ISoliniaAAAbility ability : StateManager.getInstance().getConfigurationManager().getAAAbilities())
			{
				for(ISoliniaAARank seekRank : ability.getRanks())
				{
					if (seekRank.getId() != seekRankId)
						continue;
					
					aarank = seekRank;
					break;
				}
			}
		} catch (CoreStateInitException e) {
			//
		}
		return aarank;
	}

	@Override
	public List<ISoliniaAAAbility> getAAAbilities() {
		return aaabilitiesRepository.query(q -> q.getId() > 0);
	}

	@Override
	public void editSpawnGroup(int spawngroupid, String setting, String value) throws NumberFormatException, InvalidSpawnGroupSettingException, CoreStateInitException, IOException {
		ISoliniaSpawnGroup spawnGroup = getSpawnGroup(spawngroupid);
		spawnGroup.editSetting(setting, value);

		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawnGroup.getId()));
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}

	@Override
	public void editFaction(int factionid, String setting, String value) throws NumberFormatException, InvalidFactionSettingException, CoreStateInitException, IOException {
		ISoliniaFaction faction = getFaction(factionid);
		faction.editSetting(setting, value);
	}
	
	@Override
	public List<ISoliniaQuest> getQuests() {
		// TODO Auto-generated method stub
		return questRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public ISoliniaQuest getQuest(int questId) {
		return questRepository.getByKey(questId);
	}
	
	@Override
	public ISoliniaQuest addQuest(SoliniaQuest quest, boolean isOperatorCreated) {
		quest.setOperatorCreated(isOperatorCreated);
		this.questRepository.add(quest);
		return getQuest(quest.getId());
	}

	@Override
	public void editNpcTriggerEvent(int npcid, String triggertext, String setting, String value) throws InvalidNPCEventSettingException {
		getNPC(npcid).editTriggerEventSetting(triggertext,setting, value);
	}

	@Override
	public List<ISoliniaItem> getItemsByPartialName(String itemMatch) {
		if (itemMatch == null)
			return new ArrayList<ISoliniaItem>();
		
		return itemRepository.query(q -> q.getDisplayname().toUpperCase().contains(itemMatch.toUpperCase()));
	}

	@Override
	public void editAAAbility(int aaid, String setting, String value) throws InvalidAASettingException {
		ISoliniaAAAbility aaability = getAAAbility(aaid);
		aaability.editSetting(setting, value);
	}

	@Override
	public ISoliniaAARank getAARankCache(int rankId) {
		if (aarankcache.values() == null)
			return null;
		
		return aarankcache.get(rankId);
	}

	@Override
	public void updateEmperors() {
		
		HashMap<UUID,Integer> goodCount = new HashMap<UUID,Integer>();
		HashMap<UUID,Integer> neutralCount = new HashMap<UUID,Integer>();
		HashMap<UUID,Integer> evilCount = new HashMap<UUID,Integer>();
		
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp earliesttimestamp = Timestamp.valueOf(datetime.minusDays(30));
		
		
		try {
			for(ISoliniaPlayer player : StateManager.getInstance().getPlayerManager().getCharacters())
			{
				if (!player.isMain())
					continue;

				if (player.getRaceId() < 1)
					continue;
				
				if (player.getFealty() == null)
					continue;
				
				if (player.getLastLogin().before(earliesttimestamp))
					continue;
				
				ISoliniaPlayer fealtyPlayer = StateManager.getInstance().getPlayerManager().getMainCharacterDataOnly(player.getFealty());
				
				if (fealtyPlayer.getLastLogin().before(earliesttimestamp))
				{
					continue;
				}
				
				if (fealtyPlayer.getRaceId() < 1)
					continue;
				
				if (!fealtyPlayer.isMain())
					continue;
				
				if (!player.getRace().getAlignment().equals(fealtyPlayer.getRace().getAlignment()))
					continue;
				
				switch(player.getRace().getAlignment())
				{
					case "GOOD":
						if (!goodCount.containsKey(player.getFealty()))
						{
							goodCount.put(player.getFealty(), 1);
						} else {
							goodCount.put(player.getFealty(), goodCount.get(player.getFealty())+1);
						}
						break;
					case "NEUTRAL":
						if (!neutralCount.containsKey(player.getFealty()))
						{
							neutralCount.put(player.getFealty(), 1);
						} else {
							neutralCount.put(player.getFealty(), neutralCount.get(player.getFealty())+1);
						}
						break;
					case "EVIL":
						if (!evilCount.containsKey(player.getFealty()))
						{
							evilCount.put(player.getFealty(), 1);
						} else {
							evilCount.put(player.getFealty(), evilCount.get(player.getFealty())+1);
						}
						break;
					default:
						break;
				}
			}
			
			// GOOD
			Entry<UUID,Integer> maxEntry = null;
			for(Entry<UUID,Integer> entry : goodCount.entrySet())
			{
				if (maxEntry == null || entry.getValue() > maxEntry.getValue()) 
				{
			        maxEntry = entry;
			    }
			}
			
			if (maxEntry != null)
			{
				ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment("GOOD");
				if (alignment.getEmperor() == null || !alignment.getEmperor().equals(maxEntry.getKey()))
					StateManager.getInstance().getConfigurationManager().getAlignment("GOOD").setEmperor(maxEntry.getKey());
			}
			
			maxEntry = null;
			for(Entry<UUID,Integer> entry : neutralCount.entrySet())
			{
				if (maxEntry == null || entry.getValue() > maxEntry.getValue()) 
				{
			        maxEntry = entry;
			    }
			}
			
			if (maxEntry != null)
			{
				ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment("NEUTRAL");
				if (alignment.getEmperor() == null || !alignment.getEmperor().equals(maxEntry.getKey()))
					StateManager.getInstance().getConfigurationManager().getAlignment("NEUTRAL").setEmperor(maxEntry.getKey());
			}
			
			maxEntry = null;
			for(Entry<UUID,Integer> entry : evilCount.entrySet())
			{
				if (maxEntry == null || entry.getValue() > maxEntry.getValue()) 
				{
			        maxEntry = entry;
			    }
			}
			
			if (maxEntry != null)
			{
				ISoliniaAlignment alignment = StateManager.getInstance().getConfigurationManager().getAlignment("EVIL");
				if (alignment.getEmperor() == null || !alignment.getEmperor().equals(maxEntry.getKey()))
					StateManager.getInstance().getConfigurationManager().getAlignment("EVIL").setEmperor(maxEntry.getKey());
			}
			
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addAlignment(String alignmentname) throws Exception {
		for(ISoliniaAlignment alignment : getAlignments())
		{
			if (alignment.getName().equals(alignmentname.toUpperCase()))
				throw new Exception("Alignment already exists");
		}
		
		SoliniaAlignment alignment = new SoliniaAlignment();
		alignment.setId(getNextAlignmentId());
		alignment.setName(alignmentname);
		
		alignmentsRepository.add(alignment);
	}
	
	@Override
	public List<ISoliniaAAAbility> getAAbilitiesBySysname(String sysname) {
		return aaabilitiesRepository.query(q -> q.getSysname().equals(sysname));
	}

	@Override
	public List<ISoliniaPlayer> getCharacters() {
		// TODO Auto-generated method stub
		return characterlistsRepository.query(q -> q.getCharacterId() != null);
	}
	
	@Override
	public List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID) {
		// TODO Auto-generated method stub
		return characterlistsRepository.query(q -> q.getCharacterId() != null && q.getUUID().equals(playerUUID));
	}
	
	@Override
	public ISoliniaPlayer getCharacterByCharacterUUID(UUID characterUUID) {
		List<ISoliniaPlayer> results = characterlistsRepository.query(q -> q.getCharacterId() != null && q.getCharacterId().equals(characterUUID));
		if (results.size() != 1)
			return null;
		
		return results.get(0);
	}

	@Override
	public IRepository<NPCSpellList> getNpcspelllistsRepository() {
		return npcspelllistsRepository;
	}

	@Override
	public void setNpcspelllistsRepository(IRepository<NPCSpellList> npcspelllistsRepository) {
		this.npcspelllistsRepository = npcspelllistsRepository;
	}
	
	@Override
	public NPCSpellList getNPCSpellList(int Id) {
		return npcspelllistsRepository.getByKey(Id);
	}

	@Override
	public List<NPCSpellList> getNPCSpellLists() {
		// TODO Auto-generated method stub
		return npcspelllistsRepository.query(q -> q.getName() != null);
	}

	@Override
	public List<SoliniaAccountClaim> getAccountClaims(String name) {
		return accountClaimsRepository.query(q -> q.isClaimed() == false && q.getMcname().toUpperCase().equals(name.toUpperCase()));
	}

	@Override
	public List<SoliniaZone> getZones() {
		return zonesRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<SoliniaCraft> getCrafts() {
		return craftRepository.query(q -> q.getId() > 0);
	}
	
	@Override
	public List<SoliniaZone> getZones(String name) {
		return zonesRepository.query(q -> q.getName().toUpperCase().equals(name.toUpperCase()));
	}
	
	@Override
	public void addZone(SoliniaZone zone) {
		this.zonesRepository.add(zone);

	}
	
	@Override
	public void addCraft(SoliniaCraft craft) {
		this.craftRepository.add(craft);

	}
	
	@Override
	public int getNextZoneId() {
		int max = 0;
		for (SoliniaZone zone : getZones()) {
			if (zone.getId() > max)
				max = zone.getId();
		}

		return max + 1;
	}
	
	@Override
	public int getNextCraftId() {
		int max = 0;
		for (SoliniaCraft entity : getCrafts()) {
			if (entity.getId() > max)
				max = entity.getId();
		}

		return max + 1;
	}
	
	@Override
	public void removeClaim(int id) {
		
		if (getAccountClaim(id) != null)
			getAccountClaim(id).setClaimed(true);
	}

	@Override
	public SoliniaAccountClaim getAccountClaim(int Id) {
		return accountClaimsRepository.getByKey(Id);
	}
	
	@Override
	public SoliniaAccountClaim getAccountClaim(String mcname, int seekClaimId) {
		List<SoliniaAccountClaim> results = accountClaimsRepository.query(q -> q.getId() == seekClaimId && q.isClaimed() == false && q.getMcname().toUpperCase().equals(mcname.toUpperCase()));
		if (results.size() != 1)
			return null;
		
		return results.get(0);
	}
	
	@Override
	public void addAccountClaim(SoliniaAccountClaim claim) {
		this.accountClaimsRepository.add(claim);

	}
	
	@Override
	public List<SoliniaAccountClaim> getAccountClaims() {
		return accountClaimsRepository.query(q -> q.getId() > 0);
	}

	@Override
	public int getNextAccountClaimId() {
		int max = 0;
		for (SoliniaAccountClaim claim : getAccountClaims()) {
			if (claim.getId() > max)
				max = claim.getId();
		}

		return max + 1;
	}

	@Override
	public List<SoliniaCraft> getCrafts(int itemid1, int itemid2) {
		List<SoliniaCraft> craft = new ArrayList<SoliniaCraft>();
		
		for(SoliniaCraft craftEntry : getCrafts())
		{
			if (craftEntry.getItem1() == itemid1 && craftEntry.getItem2() == itemid2)
			{
				craft.add(craftEntry);
				continue;
			}
			if (craftEntry.getItem1() == itemid2 && craftEntry.getItem2() == itemid1)
			{
				craft.add(craftEntry);
				continue;
			}
		}
		
		return craft;
	}

	@Override
	public boolean isCraftsHasComponent(int id) {
		for(SoliniaCraft craftEntry : getCrafts())
		{
			if (craftEntry.getItem1() == id || craftEntry.getItem2() == id)
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isWorldNameFree(String worldNameUpperCase) {
		List<SoliniaWorld> list = worldRepository.query(q -> q.getName().toUpperCase().equals(worldNameUpperCase.toUpperCase()));
		if (list.size() > 0)
			return false;
		
		return true;
	}

	@Override
	public boolean isSpellsChanged() {
		return spellsChanged;
	}

	@Override
	public void setSpellsChanged(boolean spellsChanged) {
		this.spellsChanged = spellsChanged;
	}

	@Override
	public boolean isItemsChanged() {
		return itemsChanged;
	}

	@Override
	public void setItemsChanged(boolean itemsChanged) {
		this.itemsChanged = itemsChanged;
	}

	@Override
	public void commitCsvs() {
		System.out.println("Writing Items CSV");
		this.itemRepository.writeCsv("items.csv");
		System.out.println("Writing Items CSV Finished");
		System.out.println("Writing Spells CSV");
		this.spellRepository.writeCsv("spells.csv");
		System.out.println("Writing Spells Finished");
		System.out.println("Writing Craft CSV");
		this.craftRepository.writeCsv("crafts.csv");
		System.out.println("Writing Craft Finished");
	}
	
}
