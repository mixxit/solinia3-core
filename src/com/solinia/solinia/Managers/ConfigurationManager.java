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
import com.solinia.solinia.Models.AlignmentType;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.NPCSpellList;
import com.solinia.solinia.Models.Oath;
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
	
	public List<Ideal> getIdeals()
	{
		List<Ideal> ideals = new ArrayList<Ideal>() {{
		add(new Ideal("Faith","I trust that my deity will guide my actions. I have faith that if I work hard, things will go well.",AlignmentType.Lawful));
		add(new Ideal("Tradition","The ancient traditions of worship and sacrifice must be preserved and upheld.",AlignmentType.Lawful));
		add(new Ideal("Charity","I always try to help those in need, no matter what the personal cost.",AlignmentType.Good));
		add(new Ideal("Change","We must help bring about the changes the gods are constantly working in the world.",AlignmentType.Chaotic));
		add(new Ideal("Power","I hope to one day rise to the top of my faith's religious hierarchy.",AlignmentType.Lawful));
		add(new Ideal("Aspiration","I seek to prove my self worthy of my god's favor by matching my actions against his or her teachings.",AlignmentType.Any));
		add(new Ideal("Independence","I am a free spirit--no one tells me what to do.",AlignmentType.Chaotic));
		add(new Ideal("Fairness","I never target people who can't afford to lose a few coins.",AlignmentType.Lawful));
		add(new Ideal("Charity","I distribute money I acquire to the people who really need it.",AlignmentType.Good));
		add(new Ideal("Creativity","I never run the same con twice.",AlignmentType.Chaotic));
		add(new Ideal("Friendship","Material goods come and go. Bonds of friendship last forever.",AlignmentType.Good));
		add(new Ideal("Aspiration","I'm determined to make something of myself.",AlignmentType.Any));
		add(new Ideal("Honor","I don't steal from others in the trade.",AlignmentType.Lawful));
		add(new Ideal("Freedom","Chains are meant to be broken, as are those who would forge them.",AlignmentType.Chaotic));
		add(new Ideal("Charity","I steal from the wealthy so that I can help people in need.",AlignmentType.Good));
		add(new Ideal("Greed","I will do whatever it takes to become wealthy.",AlignmentType.Evil));
		add(new Ideal("People","I'm loyal to my friends, not to any ideals, and everyone else can take a trip down the Styx for all I care.",AlignmentType.Neutral));
		add(new Ideal("Redemption","There's a spark of good in everyone.",AlignmentType.Good));
		add(new Ideal("Beauty","When I perform, I make the world better than it was.",AlignmentType.Good));
		add(new Ideal("Tradition","The stories, legends, and songs of the past must never be forgotten.",AlignmentType.Lawful));
		add(new Ideal("Creativity","The world is in need of new ideas and bold action.",AlignmentType.Chaotic));
		add(new Ideal("Greed","I'm only in it for the money and fame.",AlignmentType.Evil));
		add(new Ideal("People","I like seeing the smiles on people's faces when I perform. That's all that matters.",AlignmentType.Neutral));
		add(new Ideal("Honesty","Art should reflect the soul; it should come from within and reveal who we really are.",AlignmentType.Any));
		add(new Ideal("Respect","People deserve to be treated with dignity and respect.",AlignmentType.Good));
		add(new Ideal("Fairness","No one should get preferential treatment before the law, and no one is above the law.",AlignmentType.Lawful));
		add(new Ideal("Freedom","Tyrants must not be allowed to oppress the people.",AlignmentType.Chaotic));
		add(new Ideal("Might","If I become strong, I can take what I want--what I deserve.",AlignmentType.Evil));
		add(new Ideal("Sincerity","There's no good pretending to be something I'm not.",AlignmentType.Neutral));
		add(new Ideal("Destiny","Nothing and no one can steer me away from my higher calling.",AlignmentType.Any));
		add(new Ideal("Community","It is the duty of all civilized people to strengthen the bonds of community and the security of civilization.",AlignmentType.Lawful));
		add(new Ideal("Generosity","My talents were given to me so that I could use them to benefit the world.",AlignmentType.Good));
		add(new Ideal("Freedom","Everyone should be free to pursue his or her livelihood.",AlignmentType.Chaotic));
		add(new Ideal("Greed","I'm only in it for the money.",AlignmentType.Evil));
		add(new Ideal("People","I'm committed to the people I care about, not to ideals.",AlignmentType.Neutral));
		add(new Ideal("Aspiration","I work hard to be the best there is at my craft.",AlignmentType.Any));
		add(new Ideal("Greater Good","My gifts are meant to be shared with all, not used for my own benefit.",AlignmentType.Good));
		add(new Ideal("Logic","Emotions must not cloud our sense of what is right and true, or our logical thinking.",AlignmentType.Lawful));
		add(new Ideal("Free Thinking","Inquiry and curiosity are the pillars of progress.",AlignmentType.Chaotic));
		add(new Ideal("Power","Solitude and contemplation are paths toward mystical or magical power.",AlignmentType.Evil));
		add(new Ideal("Live and Let Live","Meddling in the affairs of others only causes trouble.",AlignmentType.Neutral));
		add(new Ideal("Self-Knowledge","If you know yourself, there're nothing left to know.",AlignmentType.Any));
		add(new Ideal("Respect","Respect is due to me because of my position, but all people regardless of station deserve to be treated with dignity.",AlignmentType.Good));
		add(new Ideal("Responsibility","It is my duty to respect the authority of those above me, just as those below me must respect mine.",AlignmentType.Lawful));
		add(new Ideal("Independence","I must prove that I can handle myself without the coddling of my family.",AlignmentType.Chaotic));
		add(new Ideal("Power","If I can attain more power, no one will tell me what to do.",AlignmentType.Evil));
		add(new Ideal("Family","Blood runs thicker than water.",AlignmentType.Any));
		add(new Ideal("Noble Obligation","It is my duty to protect and care for the people beneath me.",AlignmentType.Good));
		add(new Ideal("Change","Life is like the seasons, in constant change, and we must change with it.",AlignmentType.Chaotic));
		add(new Ideal("Greater Good","It is each person's responsibility to make the most happiness for the whole tribe.",AlignmentType.Good));
		add(new Ideal("Honor","If I dishonor myself, I dishonor my whole clan.",AlignmentType.Lawful));
		add(new Ideal("Might","The strongest are meant to rule.",AlignmentType.Evil));
		add(new Ideal("Nature","The natural world is more important than all the constructs of civilization.",AlignmentType.Neutral));
		add(new Ideal("Glory","I must earn glory in battle, for myself and my clan.",AlignmentType.Any));
		add(new Ideal("Knowledge","The path to power and self-improvement is through knowledge.",AlignmentType.Neutral));
		add(new Ideal("Beauty","What is beautiful points us beyond itself toward what is true.",AlignmentType.Good));
		add(new Ideal("Logic","Emotions must not cloud our logical thinking.",AlignmentType.Lawful));
		add(new Ideal("No Limits","Nothing should fetter the infinite possibility inherent in all existence.",AlignmentType.Chaotic));
		add(new Ideal("Power","Knowledge is the path to power and domination.",AlignmentType.Evil));
		add(new Ideal("Self-improvement","The goal of a life of study is the betterment of oneself.",AlignmentType.Neutral));
		add(new Ideal("Respect","The thing that keeps a ship together is mutual respect between captain and crew.",AlignmentType.Good));
		add(new Ideal("Fairness","We all do the work, so we all share in the rewards.",AlignmentType.Lawful));
		add(new Ideal("Freedom","The sea is freedom--the freedom to go anywhere and do anything.",AlignmentType.Chaotic));
		add(new Ideal("Master","I'm a predator, and the other ships on the sea are my prey.",AlignmentType.Evil));
		add(new Ideal("People","I'm committed to my crewmates, not to ideals.",AlignmentType.Neutral));
		add(new Ideal("Aspiration","Someday I'll own my own ship and chart my own destiny.",AlignmentType.Any));
		add(new Ideal("Greater Good","Our lot is to lay down our lives in defense of others.",AlignmentType.Good));
		add(new Ideal("Responsibility","I do what I must and obey just authority.",AlignmentType.Lawful));
		add(new Ideal("Independence","When people follow orders blindly they embrace a kind of tyranny.",AlignmentType.Chaotic));
		add(new Ideal("Might","In life as in war, the stronger force wins.",AlignmentType.Evil));
		add(new Ideal("Live and Let Live","Ideals aren't worth killing for or going to war for.",AlignmentType.Neutral));
		add(new Ideal("Nation","My city, nation, or people are all that matter.",AlignmentType.Any));
		add(new Ideal("Respect","All people, rich or poor, deserve respect.",AlignmentType.Good));
		add(new Ideal("Community","We have to take care of each other, because no one else is going to do it.",AlignmentType.Lawful));
		add(new Ideal("Change","The low are lifted up, and the high and mighty are brought down. Change is the nature of things.",AlignmentType.Chaotic));
		add(new Ideal("Retribution","The rich need to be shown what life and death are like in the gutters.",AlignmentType.Evil));
		add(new Ideal("People","I help people who help me--that's what keeps us alive.",AlignmentType.Neutral));
		add(new Ideal("Aspiration","I'm going to prove that I'm worthy of a better life.",AlignmentType.Any));
		}};
		
		return ideals;
	}
	
	@Override
	public List<String> getBonds()
	{
		List<String> bonds = new ArrayList<String>() {{
		add("I would die to recover an ancient artifact of my faith that was lost long ago.");
		add("I will someday get revenge on the corrupt temple hierarchy who branded me a heretic.");
		add("I owe me life to the priest who took me in when my parents died.");
		add("Everything I do is for the common people.");
		add("I will do anything to protect the temple where I served.");
		add("I seek to preserve a sacred text that my enemies consider heretical and seek to destroy.");
		add("I fleeced the wrong person and must work to ensure that this individual never crosses paths with me or those I care about.");
		add("I owe everything to my mentor--a horrible person who's probably rotting in jail somewhere.");
		add("Somewhere out there I have a child who doesn't know me. I'm making the world better for him or her.");
		add("I come from a noble family, and one day I'll reclaim my lands and title from those who stole them from me.");
		add("A powerful person killed someone I love. Some day soon, I'll have my revenge.");
		add("I swindled and ruined a person who didn't deserve it. I seek to atone for my misdeeds but might never be able to forgive myself.");
		add("I'm trying to pay off an old debt I owe to a generous benefactor.");
		add("My ill-gotten gains go to support my family.");
		add("Something important was taken from me, and I aim to steal it back.");
		add("I will become the greatest thief that ever lived.");
		add("I'm guilty of a terrible crime. I hope I can redeem myself for it.");
		add("Someone I loved died because of a mistake I made. That will never happen again.");
		add("My instrument is my most treasured possession, and it reminds me of someone I love.");
		add("Someone stole my precious instrument, and someday I'll get it back.");
		add("I want to be famous, whatever it takes.");
		add("I idolize a hero of the old tales and measure my deeds against that person's.");
		add("I will do anything to prove myself superior to me hated rival.");
		add("I would do anything for the other members of my old troupe.");
		add("I have a family, but I have no idea where they are. One day, I hope to see them again.");
		add("I worked the land, I love the land, and I will protect the land.");
		add("A proud noble once gave me a horrible beating, and I will take my revenge on any bully I encounter.");
		add("My tools are symbols of my past life, and I carry them so that I will never forget my roots.");
		add("I protect those who cannot protect themselves.");
		add("I wish my childhood sweetheart had come with me to pursue my destiny.");
		add("The workshop where I learned my trade is the most important place in the world to me.");
		add("I created a great work for someone, and then found them unworthy to receive it. I'm still looking for someone worthy.");
		add("I owe my guild a great debt for forging me into the person I am today.");
		add("I pursue wealth to secure someone's love.");
		add("One day I will return to my guild and prove that I am the greatest artisan of them all.");
		add("I will get revenge on the evil forces that destroyed my place of business and ruined my livelihood.");
		add("Nothing is more important than the other members of my hermitage, order, or association.");
		add("I entered seclusion to hide from the ones who might still be hunting me. I must someday confront them.");
		add("I'm still seeking the enlightenment I pursued in my seclusion, and it still eludes me.");
		add("I entered seclusion because I loved someone I could not have.");
		add("Should my discovery come to light, it could bring ruin to the world.");
		add("My isolation gave me great insight into a great evil that only I can destroy.");
		add("I will face any challenge to win the approval of my family.");
		add("My house's alliance with another noble family must be sustained at all costs.");
		add("Nothing is more important that the other members of my family.");
		add("I am in love with the heir of a family that my family despises.");
		add("My loyalty to my sovereign is unwavering.");
		add("The common folk must see me as a hero of the people.");
		add("My family, clan, or tribe is the most important thing in my life, even when they are far from me.");
		add("An injury to the unspoiled wilderness of my home is an injury to me.");
		add("I will bring terrible wrath down on the evildoers who destroyed my homeland.");
		add("I am the last of my tribe, and it is up to me to ensure their names enter legend.");
		add("I suffer awful visions of a coming disaster and will do anything to prevent it.");
		add("It is my duty to provide children to sustain my tribe.");
		add("It is my duty to protect my students.");
		add("I have an ancient text that holds terrible secrets that must not fall into the wrong hands.");
		add("I work to preserve a library, university, scriptorium, or monastery.");
		add("My life's work is a series of tomes related to a specific field of lore.");
		add("I've been searching my whole life for the answer to a certain question.");
		add("I sold my soul for knowledge. I hope to do great deeds and win it back.");
		add("I'm loyal to my captain first, everything else second.");
		add("The ship is most important--crewmates and captains come and go.");
		add("I'll always remember my first ship.");
		add("In a harbor town, I have a paramour whose eyes nearly stole me from the sea.");
		add("I was cheated of my fair share of the profits, and I want to get my due.");
		add("Ruthless pirates murdered my captain and crewmates, plundered our ship, and left me to die. Vengeance will be mine.");
		add("I would lay down my life for the people I served with.");
		add("Someone saved my life on the battlefield. To this day, I will never leave a friend behind.");
		add("My honor is my life.");
		add("I'll never forget the crushing defeat my company suffered or the enemies who dealt it.");
		add("Those who fight beside me are those worth dying for.");
		add("I fight for those who cannot fight for themselves.");
		add("My town or city is my home, and I'll fight to defend it.");
		add("I sponsor an orphanage to keep others from enduring what I was forced to endure.");
		add("I owe my survival to another urchin who taught me to live on the streets.");
		add("I owe a debt I can never repay to the person who took pity on me.");
		add("I escaped my life of poverty by robbing an important person, and I'm wanted for it.");
		add("No one else is going to have to endure the hardships I've been through.");
		}};
		return bonds;
	}
	
	@Override
	public List<String> getFlaws()
	{
		List<String> flaws = new ArrayList<String>() {{
			add("I judge others harshly, and myself even more severely.");
			add("I put too much trust in those who wield power within my temple's hierarchy.");
			add("My piety sometimes leads me to blindly trust those that profess faith in my god.");
			add("I am inflexible in my thinking.");
			add("I am suspicious of strangers and suspect the worst of them.");
			add("Once I pick a goal, I become obsessed with it to the detriment of everything else in my life.");
			add("I can't resist a pretty face.");
			add("I'm always in debt. I spend my ill-gotten gains on decadent luxuries faster than I bring them in.");
			add("I'm convinced that no one could ever fool me in the way I fool others.");
			add("I'm too greedy for my own good. I can't resist taking a risk if there's money involved.");
			add("I can't resist swindling people who are more powerful than me.");
			add("I hate to admit it and will hate myself for it, but I'll run and preserve my own hide if the going gets tough.");
			add("When I see something valuable, I can't think about anything but how to steal it.");
			add("When faced with a choice between money and my friends, I usually choose the money.");
			add("If there's a plan, I'll forget it. If I don't forget it, I'll ignore it.");
			add("I have a 'tell' that reveals when I'm lying.");
			add("I turn tail and run when things go bad.");
			add("An innocent person is in prison for a crime that I committed. I'm okay with that.");
			add("I'll do anything to win fame and renown.");
			add("I'm a sucker for a pretty face.");
			add("A scandal prevents me from ever going home again. That kind of trouble seems to follow me around.");
			add("I once satirized a noble who still wants my head. It was a mistake that I will likely repeat.");
			add("I have trouble keeping my true feelings hidden. My sharp tongue lands me in trouble.");
			add("Despite my best efforts, I am unreliable to my friends.");
			add("The tyrant who rules my land will stop at nothing to see me killed.");
			add("I'm convinced of the significance of my destiny, and blind to my shortcomings and the risk of failure.");
			add("The people who knew me when I was young know my shameful secret, so I can never go home again.");
			add("I have a weakness for the vices of the city, especially hard drink.");
			add("Secretly, I believe that things would be better if I were a tyrant lording over the land.");
			add("I have trouble trusting in my allies.");
			add("I'll do anything to get my hands on something rare or priceless.");
			add("I'm quick to assume that someone is trying to cheat me.");
			add("No one must ever learn that I once stole money from guild coffers.");
			add("I'm never satisfied with what I have--I always want more.");
			add("I would kill to acquire a noble title.");
			add("I'm horribly jealous of anyone who outshines my handiwork. Everywhere I go, I'm surrounded by rivals.");
			add("Now that I've returned to the world, I enjoy its delights a little too much.");
			add("I harbor dark bloodthirsty thoughts that my isolation failed to quell.");
			add("I am dogmatic in my thoughts and philosophy.");
			add("I let my need to win arguments overshadow friendships and harmony.");
			add("I'd risk too much to uncover a lost bit of knowledge.");
			add("I like keeping secrets and won't share them with anyone.");
			add("I secretly believe that everyone is beneath me.");
			add("I hide a truly scandalous secret that could ruin my family forever.");
			add("I too often hear veiled insults and threats in every word addressed to me, and I'm quick to anger.");
			add("I have an insatiable desire for carnal pleasures.");
			add("In fact, the world does revolve around me.");
			add("By my words and actions, I often bring shame to my family.");
			add("I am too enamored of ale, wine, and other intoxicants.");
			add("There's no room for caution in a life lived to the fullest.");
			add("I remember every insult I've received and nurse a silent resentment toward anyone who's ever wronged me.");
			add("I am slow to trust members of other races");
			add("Violence is my answer to almost any challenge.");
			add("Don't expect me to save those who can't save themselves. It is nature's way that the strong thrive and the weak perish.");
			add("I am easily distracted by the promise of information.");
			add("Most people scream and run when they see a demon. I stop and take notes on its anatomy.");
			add("Unlocking an ancient mystery is worth the price of a civilization.");
			add("I overlook obvious solutions in favor of complicated ones.");
			add("I speak without really thinking through my words, invariably insulting others.");
			add("I can't keep a secret to save my life, or anyone else's.");
			add("I follow orders, even if I think they're wrong.");
			add("I'll say anything to avoid having to do extra work.");
			add("Once someone questions my courage, I never back down no matter how dangerous the situation.");
			add("Once I start drinking, it's hard for me to stop.");
			add("I can't help but pocket loose coins and other trinkets I come across.");
			add("My pride will probably lead to my destruction");
			add("The monstrous enemy we faced in battle still leaves me quivering with fear.");
			add("I have little respect for anyone who is not a proven warrior.");
			add("I made a terrible mistake in battle that cost many lives--and I would do anything to keep that mistake secret.");
			add("My hatred of my enemies is blind and unreasoning.");
			add("I obey the law, even if the law causes misery.");
			add("I'd rather eat my armor than admit when I'm wrong.");
			add("If I'm outnumbered, I always run away from a fight.");
			add("Gold seems like a lot of money to me, and I'll do just about anything for more of it.");
			add("I will never fully trust anyone other than myself.");
			add("I'd rather kill someone in their sleep than fight fair.");
			add("It's not stealing if I need it more than someone else.");
			add("People who don't take care of themselves get what they deserve.");

		}};
		
		return flaws;
	}
	
	@Override
	public List<String> getTraits()
	{
		List<String> traits = new ArrayList<String>() {{
	    add("I idolize a particular hero of my faith and constantly refer to that person's deeds and example.");
	    add("I can find common ground between the fiercest enemies, empathizing with them and always working toward peace.");
	    add("I see omens in every event and action. The gods try to speak to us, we just need to listen.");
	    add("Nothing can shake my optimistic attitude.");
	    add("I quote (or misquote) the sacred texts and proverbs in almost every situation.");
	    add("I am tolerant (or intolerant) of other faiths and respect (or condemn) the worship of other gods.");
	    add("I've enjoyed fine food, drink, and high society among my temple's elite. Rough living grates on me.");
	    add("I've spent so long in the temple that I have little practical experience dealing with people in the outside world.");
	    add("I fall in and out of love easily, and am always pursuing someone.");
	    add("I have a joke for every occasion, especially occasions where humor is inappropriate.");
	    add("Flattery is my preferred trick for getting what I want.");
	    add("I'm a born gambler who can't resist taking a risk for a potential payoff.");
	    add("I lie about almost everything, even when there's no good reason to.");
	    add("Sarcasm and insults are my weapons of choice.");
	    add("I keep multiple holy symbols on me and invoke whatever deity might come in useful at any given moment.");
	    add("I pocket anything I see that might have some value.");
	    add("I always have plan for what to do when things go wrong.");
	    add("I am always calm, no matter what the situation. I never raise my voice or let my emotions control me.");
	    add("The first thing I do in a new place is note the locations of everything valuable--or where such things could be hidden.");
	    add("I would rather make a new friend than a new enemy.");
	    add("I am incredibly slow to trust. Those who seem the fairest often have the most to hide.");
	    add("I don't pay attention to the risks in a situation. Never tell me the odds.");
	    add("The best way to get me to do something is to tell me I can't do it.");
	    add("I blow up at the slightest insult.");
	    add("I know a story relevant to almost every situation.");
	    add("Whenever I come to a new place, I collect local rumors and spread gossip.");
	    add("I'm a hopeless romantic, always searching for that 'special someone'.");
	    add("Nobody stays angry at me or around me for long, since I can defuse any amount of tension.");
	    add("I love a good insult, even one directed at me.");
	    add("I get bitter if I'm not the center of attention.");
	    add("I'll settle for nothing less than perfection.");
	    add("I change my mood or my mind as quickly as I change key in a song.");
	    add("I judge people by their actions, not their words.");
	    add("If someone is in trouble, I'm always willing to lend help.");
	    add("When I set my mind to something, I follow through no matter what gets in my way.");
	    add("I have a strong sense of fair play and always try to find the most equitable solution to arguments.");
	    add("I'm confident in my own abilities and do what I can to instill confidence in others.");
	    add("Thinking is for other people. I prefer action.");
	    add("I misuse long words in an attempt to sound smarter.");
	    add("I get bored easily. When am I going to get on with my destiny.");
	    add("I believe that everything worth doing is worth doing right. I can't help it--I'm a perfectionist.");
	    add("I'm a snob who looks down on those who can't appreciate fine art.");
	    add("I always want to know how things work and what makes people tick.");
	    add("I'm full of witty aphorisms and have a proverb for every occasion.");
	    add("I'm rude to people who lack my commitment to hard work and fair play.");
	    add("I like to talk at length about my profession.");
	    add("I don't part with my money easily and will haggle tirelessly to get the best deal possible.");
	    add("I'm well known for my work, and I want to make sure everyone appreciates it. I'm always taken aback when people haven't heard of me.");
	    add("I've been isolated for so long that I rarely speak, preferring gestures and the occasional grunt.");
	    add("I am utterly serene, even in the face of disaster.");
	    add("The leader of my community has something wise to say on every topic, and I am eager to share that wisdom.");
	    add("I feel tremendous empathy for all who suffer.");
	    add("I'm oblivious to etiquette and social expectations.");
	    add("I connect everything that happens to me to a grand cosmic plan.");
	    add("I often get lost in my own thoughts and contemplations, becoming oblivious to my surroundings.");
	    add("I am working on a grand philosophical theory and love sharing my ideas.");
	    add("My eloquent flattery makes everyone I talk to feel like the most wonderful and important person in the world.");
	    add("The common folk love me for my kindness and generosity.");
	    add("No one could doubt by looking at my regal bearing that I am a cut above the unwashed masses.");
	    add("I take great pains to always look my best and follow the latest fashions.");
	    add("I don't like to get my hands dirty, and I won't be caught dead in unsuitable accommodations.");
	    add("Despite my birth, I do not place myself above other folk. We all have the same blood.");
	    add("My favor, once lost, is lost forever.");
	    add("If you do me an injury, I will crush you, ruin your name, and salt your fields.");
	    add("I'm driven by a wanderlust that led me away from home.");
	    add("I watch over my friends as if they were a litter of newborn pups.");
	    add("I once ran twenty-five miles without stopping to warn my clan of an approaching orc horde. I'd do it again if I had to.");
	    add("I have a lesson for every situation, drawn from observing nature.");
	    add("I place no stock in wealthy or well-mannered folk. Money and manners won't save you from a hungry owlbear.");
	    add("I'm always picking things up, absently fiddling with them, and sometimes accidentally breaking them.");
	    add("I feel far more comfortable around animals than people.");
	    add("I was, in fact, raised by wolves.");
	    add("I use polysyllabic words to convey the impression of great erudition.");
	    add("I've read every book in the world's greatest libraries--or like to boast that I have.");
	    add("I'm used to helping out those who aren't as smart as I am, and I patiently explain anything and everything to others.");
	    add("There's nothing I like more than a good mystery.");
	    add("I'm willing to listen to every side of an argument before I make my own judgment.");
	    add("I...speak...slowly...when talking...to idiots...which...almost...everyone...is...compared ...to me.");
	    add("I am horribly, horribly awkward in social situations.");
	    add("I'm convinced that people are always trying to steal my secrets.");
	    add("My friends know they can rely on me, no matter what.");
	    add("I work hard so that I can play hard when the work is done.");
	    add("I enjoy sailing into new ports and making new friends over a flagon of ale.");
	    add("I stretch the truth for the sake of a good story.");
	    add("To me, a tavern brawl is a nice way to get to know a new city.");
	    add("I never pass up a friendly wager.");
	    add("My language is as foul as an otyugh nest.");
	    add("I like a job well done, especially if I can convince someone else to do it.");
	    add("I'm always polite and respectful.");
	    add("I'm haunted by memories of war. I can't get the images of violence out of my mind.");
	    add("I've lost too many friends, and I'm slow to make new ones.");
	    add("I'm full of inspiring and cautionary tales from my military experience relevant to almost every combat situation.");
	    add("I can stare down a hellhound without flinching.");
	    add("I enjoy being strong and like breaking things.");
	    add("I have a crude sense of humor.");
	    add("I face problems head-on. A simple direct solution is the best path to success.");
	    add("I hide scraps of food and trinkets away in my pockets.");
	    add("I ask a lot of questions.");
	    add("I like to squeeze into small places where no one else can get to me.");
	    add("I sleep with my back to a wall or tree, with everything I own wrapped in a bundle in my arms.");
	    add("I eat like a pig and have bad manners.");
	    add("I think anyone who's nice to me is hiding evil intent.");
	    add("I don't like to bathe.");
	    add("I bluntly say what other people are hinting or hiding.");
		}};
	    return traits;
	}

	@Override
	public List<Oath> getOaths() {
		List<Oath> oaths = new ArrayList<Oath>();
		
		Oath devotion = new Oath(1, "OATHOFDEVOTION",new ArrayList<String>() {{
	    add("Honesty: Don�t lie or cheat. Let your word be your promise.");
	    add("Courage: Never fear to act, though caution is wise.");
	    add("Compassion: Aid others, protect the weak, and punish those who threaten them. Show mercy to your foes, but temper it with Wisdom.");
	    add("Honor: Treat others with fairness, and let your honorable deeds be an example to them. Do as much good as possible while causing the least amount of harm.");
	    add("Duty: Be responsible for your actions and their consequences, protect those entrusted to your care, and obey those who have just authority over you.");
		}});
		
		oaths.add(devotion);
		
		Oath ancients = new Oath(2, "OATHOFTHEANCIENTS", new ArrayList<String>() {{
	    add("Kindle the Light. Through your acts of mercy, kindness, and forgiveness, kindle the light of hope in the world, beating back despair.");
	    add("Shelter the Light. Where there is good, beauty, love, and laughter in the world, stand against the wickedness that would swallow it. Where life flourishes, stand against the forces that would render it barren.");
	    add("Preserve Your Own Light. Delight in song and laughter, in beauty and art. If you allow the light to die in your own heart, you can't preserve it in the world.");
	    add("Be the Light. Be a glorious beacon for all who live in despair. Let the light of your joy and courage shine forth in all your deeds.");
		}});
		
		oaths.add(ancients);
		
		Oath vengeance = new Oath(3, "OATHOFVENGEANCE", new ArrayList<String>() {{
		add("Fight the Greater Evil. Faced with a choice of fighting my sworn foes or combating a lesser evil, I choose the greater evil.");
		add("No Mercy for the Wicked. Ordinary foes might win my mercy, but my sworn enemies do not.");
		add("By Any Means Necessary. My qualms can't get in the way of exterminating my foes.");
		add("Restitution. If my foes wreak ruin on the world, it is because I failed to stop them. I must help those harmed by their misdeeds.");
		}});
		
		oaths.add(vengeance);
		return oaths;
	}
	
}
