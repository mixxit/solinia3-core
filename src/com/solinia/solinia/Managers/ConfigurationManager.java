package com.solinia.solinia.Managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Events.SoliniaNPCUpdatedEvent;
import com.solinia.solinia.Events.SoliniaSpawnGroupUpdatedEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidAASettingException;
import com.solinia.solinia.Exceptions.InvalidAlignmentSettingException;
import com.solinia.solinia.Exceptions.InvalidClassSettingException;
import com.solinia.solinia.Exceptions.InvalidCraftSettingException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidGodSettingException;
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
import com.solinia.solinia.Exceptions.PlayerDoesNotExistException;
import com.solinia.solinia.Exceptions.SoliniaWorldCreationException;
import com.solinia.solinia.Factories.SoliniaWorldFactory;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaGod;
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
import com.solinia.solinia.Models.Bond;
import com.solinia.solinia.Models.CharacterCreation;
import com.solinia.solinia.Models.ConfigSettings;
import com.solinia.solinia.Models.Fellowship;
import com.solinia.solinia.Models.Flaw;
import com.solinia.solinia.Models.Ideal;
import com.solinia.solinia.Models.NPCSpellList;
import com.solinia.solinia.Models.Oath;
import com.solinia.solinia.Models.RaceChoice;
import com.solinia.solinia.Models.RaceClass;
import com.solinia.solinia.Models.SoliniaAccountClaim;
import com.solinia.solinia.Models.SoliniaAlignment;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaGod;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Models.Trait;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Models.SoliniaQuest;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SoliniaWorld;
import com.solinia.solinia.Repositories.JsonAAAbilityRepository;
import com.solinia.solinia.Repositories.JsonAccountClaimRepository;
import com.solinia.solinia.Repositories.JsonAlignmentRepository;
import com.solinia.solinia.Repositories.JsonCharacterListRepository;
import com.solinia.solinia.Repositories.JsonCraftRepository;
import com.solinia.solinia.Repositories.JsonFactionRepository;
import com.solinia.solinia.Repositories.JsonFellowshipRepository;
import com.solinia.solinia.Repositories.JsonGodRepository;
import com.solinia.solinia.Repositories.JsonZoneRepository;
import com.solinia.solinia.Utils.ItemStackUtils;
import com.solinia.solinia.Utils.PlayerUtils;

import net.minecraft.server.v1_14_R1.NBTTagCompound;
import com.solinia.solinia.Repositories.JsonLootDropRepository;
import com.solinia.solinia.Repositories.JsonLootTableRepository;
import com.solinia.solinia.Repositories.JsonNPCMerchantRepository;
import com.solinia.solinia.Repositories.JsonNPCRepository;
import com.solinia.solinia.Repositories.JsonNPCSpellListRepository;
import com.solinia.solinia.Repositories.JsonPatchRepository;
import com.solinia.solinia.Repositories.JsonQuestRepository;
import com.solinia.solinia.Repositories.JsonSpawnGroupRepository;
import com.solinia.solinia.Repositories.JsonWorldRepository;

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
	private IRepository<ISoliniaGod> godsRepository;
	private IRepository<SoliniaWorld> worldRepository;
	private IRepository<Fellowship> fellowshipRepository;

	private List<Bond> bonds = new ArrayList<Bond>();
	private List<Flaw> flaws = new ArrayList<Flaw>();
	private List<Oath> oaths = new ArrayList<Oath>();
	private List<Trait> traits = new ArrayList<Trait>();
	private List<Ideal> ideals = new ArrayList<Ideal>();

	private ConcurrentHashMap<UUID, Float> lastSentPlayerManaPercent = new ConcurrentHashMap<UUID, Float>();

	private ConcurrentHashMap<UUID, String> queuedCastingPercentPackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queuedCharCreationPackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queuedEffectsPackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queuedEquipSlotsPackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queuedMemorisedSpellsPackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueSpellbookPagePackets = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPacketsneg2 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPacketsneg1 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets0 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets1 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets2 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets3 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets4 = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, String> queueMobVitalsPackets5 = new ConcurrentHashMap<UUID, String>();

	
	// Commit tracking
	private boolean spellsChanged = false;
	private boolean itemsChanged = false;
	private ConfigSettings configSettings = new ConfigSettings();

	public ConfigurationManager(IRepository<ISoliniaRace> raceContext, IRepository<ISoliniaClass> classContext,
			IRepository<ISoliniaItem> itemContext, IRepository<ISoliniaSpell> spellContext,
			JsonFactionRepository factionContext, JsonNPCRepository npcContext,
			JsonNPCMerchantRepository npcmerchantContext, JsonLootTableRepository loottableContext,
			JsonLootDropRepository lootdropContext, JsonSpawnGroupRepository spawngroupContext, 
			JsonAAAbilityRepository aaabilitiesContext, 
			JsonPatchRepository patchesContext, JsonQuestRepository questsContext, JsonAlignmentRepository alignmentsContext, 
			JsonCharacterListRepository characterlistsContext, JsonNPCSpellListRepository npcspelllistsContext, 
			JsonAccountClaimRepository accountClaimsContext, JsonZoneRepository zonesContext, JsonCraftRepository craftContext, JsonWorldRepository worldContext,
			JsonGodRepository godsContext, JsonFellowshipRepository fellowshipContext, ConfigSettings configSettings
			) {
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
		this.godsRepository = godsContext;
		this.configSettings  = configSettings;
		this.fellowshipRepository = fellowshipContext;
		
		this.setBonds(generateBonds());
		this.setOaths(generateOaths());
		this.setTraits(generateTraits());
		this.setFlaws(generateFlaws());
		this.setIdeals(generateIdeals());
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
		this.godsRepository.commit();
		this.fellowshipRepository.commit();
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
	public List<Fellowship> getFellowships() {
		return fellowshipRepository.query(q -> q.getId() > 0);
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
		List<ISoliniaNPC> results = npcRepository.query(q -> q.isCorePet() == true && q.getName().equals(name));
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
		String soliniaid = null;
		if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null && itemStack.getItemMeta().getDisplayName().startsWith("CUSTOMITEMID_"))
		{
			soliniaid = itemStack.getItemMeta().getDisplayName().split("_")[1];
		}
		else if (ItemStackUtils.getSoliniaItemId(itemStack) != null)
		{
			soliniaid = Long.toString(ItemStackUtils.getSoliniaItemId(itemStack));
		}
		else 
		{
			// Classic method, to be removed
			net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			soliniaid = compound.getString("soliniaid");
		}
		
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
	public Fellowship addFellowship(Fellowship fellowship) {
		this.fellowshipRepository.add(fellowship);
		return getFellowship(fellowship.getId());
	}

	@Override
	public ISoliniaGod addGod(SoliniaGod god) {
		this.godsRepository.add(god);
		return getGod(god.getId());

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
	public int getNextFellowshipId() {
		int max = 0;
		for (Fellowship fellowship : getFellowships()) {
			if (fellowship.getId() > max)
				max = fellowship.getId();
		}

		return max + 1;
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
	public int getNextSpellId() {
		int maxItem = 0;
		for (ISoliniaSpell itemInstance : getSpells()) {
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

		if (classes.getValidRaceClasses().containsKey(raceId))
			return true;

		return false;
	}

	@Override
	public void addRaceClass(int raceId, int classId) {
		if (getClassObj(classId) == null)
			return;

		if (getRace(raceId) == null)
			return;

		if (getClassObj(classId).getValidRaceClasses().containsKey(raceId))
			return;

		RaceClass newRaceClass = new RaceClass();
		newRaceClass.RaceId = raceId;
		
		getClassObj(classId).getValidRaceClasses().put(raceId, newRaceClass);
	}

	@Override
	public void addItem(ISoliniaItem item) {
		this.itemRepository.add(item);
	}
	
	@Override
	public void addSpawnGroup(ISoliniaSpawnGroup spawngroup, boolean providerReload) {
		this.spawngroupRepository.add(spawngroup);
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroup.getId()),providerReload);
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
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroup.getId()),true);
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}
	
	@Override
	public void updateSpawnGroupLoc(int spawngroupid, Location location) {
		if (getSpawnGroup(spawngroupid) == null)
			return;
		
		getSpawnGroup(spawngroupid).setLocation(location);
		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawngroupid), true);
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
	public void editGod(int id, String setting, String value)
			throws NumberFormatException, CoreStateInitException, InvalidGodSettingException {
		getGod(id).editSetting(setting, value);
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
	public ISoliniaGod getGod(String name) {

		List<ISoliniaGod> entries = godsRepository.query(q -> q.getName().toUpperCase().equals(name.toUpperCase()));
		if (entries.size() > 0)
			return entries.get(0);

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
	public void editQuest(CommandSender sender, int id, String setting, String value, String[] additional) 
		throws InvalidQuestSettingException, NumberFormatException, CoreStateInitException {
			getQuest(id).editSetting(sender, setting, value, additional);
	}
	
	@Override
	public ISoliniaNPCMerchant getNPCMerchant(String merchantlistname) {
		List<ISoliniaNPCMerchant> list = npcmerchantRepository.query(q -> q.getName().toUpperCase().equals(merchantlistname.toUpperCase()));
		if (list.size() > 0)
			return list.get(0);

		return null;
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
	public Fellowship getFellowship(int Id) {
		return fellowshipRepository.getByKey(Id);
	}
	
	@Override
	public ISoliniaGod getGod(int Id) {
		return godsRepository.getByKey(Id);
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

		SoliniaSpawnGroupUpdatedEvent soliniaevent = new SoliniaSpawnGroupUpdatedEvent(getSpawnGroup(spawnGroup.getId()), true);
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
	public ISoliniaQuest addQuest(SoliniaQuest quest) {
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
	public List<ISoliniaPlayer> getArchivedCharacters() {
		// TODO Auto-generated method stub
		return characterlistsRepository.query(q -> q.getCharacterId() != null);
	}
	
	@Override
	public List<ISoliniaPlayer> getArchivedCharactersByPlayerUUID(UUID playerUUID) {
		// TODO Auto-generated method stub
		return characterlistsRepository.query(q -> q.getCharacterId() != null && q.getUUID().equals(playerUUID));
	}
	
	@Override
	public ISoliniaPlayer getArchivedCharacterByCharacterUUID(UUID characterUUID) {
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
	public List<ISoliniaGod> getGods() {
		return godsRepository.query(q -> q.getId() > 0);
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
	public int getNextGodId() {
		int max = 0;
		for (ISoliniaGod entry : getGods()) {
			if (entry.getId() > max)
				max = entry.getId();
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
	public boolean isGodNameFree(String godNameUpperCase) {
		List<ISoliniaGod> list = godsRepository.query(q -> q.getName().toUpperCase().equals(godNameUpperCase.toUpperCase()));
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
	}

	@Override
	public List<Bond> getBonds() {
		return bonds;
	}

	@Override
	public void setBonds(List<Bond> bonds) {
		this.bonds = bonds;
	}

	@Override
	public List<Flaw> getFlaws() {
		return flaws;
	}

	@Override
	public void setFlaws(List<Flaw> flaws) {
		this.flaws = flaws;
	}

	@Override
	public List<Oath> getOaths() {
		return oaths;
	}

	@Override
	public void setOaths(List<Oath> oaths) {
		this.oaths = oaths;
	}

	@Override
	public List<Trait> getTraits() {
		return traits;
	}

	@Override
	public void setTraits(List<Trait> traits) {
		this.traits = traits;
	}
	
	public List<Ideal> generateIdeals()
	{
		List<Ideal> ideals = new ArrayList<Ideal>() {/**
			 * 
			 */
			private static final long serialVersionUID = 3921136482442842920L;

		{
		add(new Ideal(1,"Faith","I trust that my deity will guide my actions. I have faith that if I work hard, things will go well.",AlignmentType.Lawful));
		add(new Ideal(2,"Tradition","The ancient traditions of worship and sacrifice must be preserved and upheld.",AlignmentType.Lawful));
		add(new Ideal(3,"Charity","I always try to help those in need, no matter what the personal cost.",AlignmentType.Good));
		add(new Ideal(4,"Change","We must help bring about the changes the gods are constantly working in the world.",AlignmentType.Chaotic));
		add(new Ideal(5,"Power","I hope to one day rise to the top of my faith's religious hierarchy.",AlignmentType.Lawful));
		add(new Ideal(6,"Aspiration","I seek to prove my self worthy of my god's favor by matching my actions against his or her teachings.",AlignmentType.Any));
		add(new Ideal(7,"Independence","I am a free spirit--no one tells me what to do.",AlignmentType.Chaotic));
		add(new Ideal(8,"Fairness","I never target people who can't afford to lose a few coins.",AlignmentType.Lawful));
		add(new Ideal(9,"Charity","I distribute money I acquire to the people who really need it.",AlignmentType.Good));
		add(new Ideal(10,"Creativity","I never run the same con twice.",AlignmentType.Chaotic));
		add(new Ideal(11,"Friendship","Material goods come and go. Bonds of friendship last forever.",AlignmentType.Good));
		add(new Ideal(12,"Aspiration","I'm determined to make something of myself.",AlignmentType.Any));
		add(new Ideal(13,"Honor","I don't steal from others in the trade.",AlignmentType.Lawful));
		add(new Ideal(14,"Freedom","Chains are meant to be broken, as are those who would forge them.",AlignmentType.Chaotic));
		add(new Ideal(15,"Charity","I steal from the wealthy so that I can help people in need.",AlignmentType.Good));
		add(new Ideal(16,"Greed","I will do whatever it takes to become wealthy.",AlignmentType.Evil));
		add(new Ideal(17,"People","I'm loyal to my friends, not to any ideals, and everyone else can take a trip down the Styx for all I care.",AlignmentType.Neutral));
		add(new Ideal(18,"Redemption","There's a spark of good in everyone.",AlignmentType.Good));
		add(new Ideal(19,"Beauty","When I perform, I make the world better than it was.",AlignmentType.Good));
		add(new Ideal(20,"Tradition","The stories, legends, and songs of the past must never be forgotten.",AlignmentType.Lawful));
		add(new Ideal(21,"Creativity","The world is in need of new ideas and bold action.",AlignmentType.Chaotic));
		add(new Ideal(22,"Greed","I'm only in it for the money and fame.",AlignmentType.Evil));
		add(new Ideal(23,"People","I like seeing the smiles on people's faces when I perform. That's all that matters.",AlignmentType.Neutral));
		add(new Ideal(24,"Honesty","Art should reflect the soul; it should come from within and reveal who we really are.",AlignmentType.Any));
		add(new Ideal(25,"Respect","People deserve to be treated with dignity and respect.",AlignmentType.Good));
		add(new Ideal(26,"Fairness","No one should get preferential treatment before the law, and no one is above the law.",AlignmentType.Lawful));
		add(new Ideal(27,"Freedom","Tyrants must not be allowed to oppress the people.",AlignmentType.Chaotic));
		add(new Ideal(28,"Might","If I become strong, I can take what I want--what I deserve.",AlignmentType.Evil));
		add(new Ideal(29,"Sincerity","There's no good pretending to be something I'm not.",AlignmentType.Neutral));
		add(new Ideal(30,"Destiny","Nothing and no one can steer me away from my higher calling.",AlignmentType.Any));
		add(new Ideal(31,"Community","It is the duty of all civilized people to strengthen the bonds of community and the security of civilization.",AlignmentType.Lawful));
		add(new Ideal(32,"Generosity","My talents were given to me so that I could use them to benefit the world.",AlignmentType.Good));
		add(new Ideal(33,"Freedom","Everyone should be free to pursue his or her livelihood.",AlignmentType.Chaotic));
		add(new Ideal(34,"Greed","I'm only in it for the money.",AlignmentType.Evil));
		add(new Ideal(35,"People","I'm committed to the people I care about, not to ideals.",AlignmentType.Neutral));
		add(new Ideal(36,"Aspiration","I work hard to be the best there is at my craft.",AlignmentType.Any));
		add(new Ideal(37,"Greater Good","My gifts are meant to be shared with all, not used for my own benefit.",AlignmentType.Good));
		add(new Ideal(38,"Logic","Emotions must not cloud our sense of what is right and true, or our logical thinking.",AlignmentType.Lawful));
		add(new Ideal(39,"Free Thinking","Inquiry and curiosity are the pillars of progress.",AlignmentType.Chaotic));
		add(new Ideal(40,"Power","Solitude and contemplation are paths toward mystical or magical power.",AlignmentType.Evil));
		add(new Ideal(41,"Live and Let Live","Meddling in the affairs of others only causes trouble.",AlignmentType.Neutral));
		add(new Ideal(42,"Self-Knowledge","If you know yourself, there're nothing left to know.",AlignmentType.Any));
		add(new Ideal(43,"Respect","Respect is due to me because of my position, but all people regardless of station deserve to be treated with dignity.",AlignmentType.Good));
		add(new Ideal(44,"Responsibility","It is my duty to respect the authority of those above me, just as those below me must respect mine.",AlignmentType.Lawful));
		add(new Ideal(45,"Independence","I must prove that I can handle myself without the coddling of my family.",AlignmentType.Chaotic));
		add(new Ideal(46,"Power","If I can attain more power, no one will tell me what to do.",AlignmentType.Evil));
		add(new Ideal(47,"Family","Blood runs thicker than water.",AlignmentType.Any));
		add(new Ideal(48,"Noble Obligation","It is my duty to protect and care for the people beneath me.",AlignmentType.Good));
		add(new Ideal(49,"Change","Life is like the seasons, in constant change, and we must change with it.",AlignmentType.Chaotic));
		add(new Ideal(50,"Greater Good","It is each person's responsibility to make the most happiness for the whole tribe.",AlignmentType.Good));
		add(new Ideal(51,"Honor","If I dishonor myself, I dishonor my whole clan.",AlignmentType.Lawful));
		add(new Ideal(52,"Might","The strongest are meant to rule.",AlignmentType.Evil));
		add(new Ideal(53,"Nature","The natural world is more important than all the constructs of civilization.",AlignmentType.Neutral));
		add(new Ideal(54,"Glory","I must earn glory in battle, for myself and my clan.",AlignmentType.Any));
		add(new Ideal(55,"Knowledge","The path to power and self-improvement is through knowledge.",AlignmentType.Neutral));
		add(new Ideal(56,"Beauty","What is beautiful points us beyond itself toward what is true.",AlignmentType.Good));
		add(new Ideal(57,"Logic","Emotions must not cloud our logical thinking.",AlignmentType.Lawful));
		add(new Ideal(58,"No Limits","Nothing should fetter the infinite possibility inherent in all existence.",AlignmentType.Chaotic));
		add(new Ideal(59,"Power","Knowledge is the path to power and domination.",AlignmentType.Evil));
		add(new Ideal(60,"Self-improvement","The goal of a life of study is the betterment of oneself.",AlignmentType.Neutral));
		add(new Ideal(61,"Respect","The thing that keeps a ship together is mutual respect between captain and crew.",AlignmentType.Good));
		add(new Ideal(62,"Fairness","We all do the work, so we all share in the rewards.",AlignmentType.Lawful));
		add(new Ideal(63,"Freedom","The sea is freedom--the freedom to go anywhere and do anything.",AlignmentType.Chaotic));
		add(new Ideal(64,"Master","I'm a predator, and the other ships on the sea are my prey.",AlignmentType.Evil));
		add(new Ideal(65,"People","I'm committed to my crewmates, not to ideals.",AlignmentType.Neutral));
		add(new Ideal(66,"Aspiration","Someday I'll own my own ship and chart my own destiny.",AlignmentType.Any));
		add(new Ideal(67,"Greater Good","Our lot is to lay down our lives in defense of others.",AlignmentType.Good));
		add(new Ideal(68,"Responsibility","I do what I must and obey just authority.",AlignmentType.Lawful));
		add(new Ideal(69,"Independence","When people follow orders blindly they embrace a kind of tyranny.",AlignmentType.Chaotic));
		add(new Ideal(70,"Might","In life as in war, the stronger force wins.",AlignmentType.Evil));
		add(new Ideal(71,"Live and Let Live","Ideals aren't worth killing for or going to war for.",AlignmentType.Neutral));
		add(new Ideal(72,"Nation","My city, nation, or people are all that matter.",AlignmentType.Any));
		add(new Ideal(73,"Respect","All people, rich or poor, deserve respect.",AlignmentType.Good));
		add(new Ideal(74,"Community","We have to take care of each other, because no one else is going to do it.",AlignmentType.Lawful));
		add(new Ideal(75,"Change","The low are lifted up, and the high and mighty are brought down. Change is the nature of things.",AlignmentType.Chaotic));
		add(new Ideal(76,"Retribution","The rich need to be shown what life and death are like in the gutters.",AlignmentType.Evil));
		add(new Ideal(77,"People","I help people who help me--that's what keeps us alive.",AlignmentType.Neutral));
		add(new Ideal(78,"Aspiration","I'm going to prove that I'm worthy of a better life.",AlignmentType.Any));
		}};
		
		return ideals;
	}
	
	public List<Bond> generateBonds()
	{
		List<Bond> bonds = new ArrayList<Bond>() {/**
			 * 
			 */
			private static final long serialVersionUID = 8054859622604966777L;

		{
			add(new Bond(1,"I would die to recover an ancient artifact of my faith that was lost long ago."));
			add(new Bond(2,"I will someday get revenge on the corrupt temple hierarchy who branded me a heretic."));
			add(new Bond(3,"I owe me life to the priest who took me in when my parents died."));
			add(new Bond(4,"Everything I do is for the common people."));
			add(new Bond(5,"I will do anything to protect the temple where I served."));
			add(new Bond(6,"I seek to preserve a sacred text that my enemies consider heretical and seek to destroy."));
			add(new Bond(7,"I fleeced the wrong person and must work to ensure that this individual never crosses paths with me or those I care about."));
			add(new Bond(8,"I owe everything to my mentor--a horrible person who's probably rotting in jail somewhere."));
			add(new Bond(9,"Somewhere out there I have a child who doesn't know me. I'm making the world better for him or her."));
			add(new Bond(10,"I come from a noble family, and one day I'll reclaim my lands and title from those who stole them from me."));
			add(new Bond(11,"A powerful person killed someone I love. Some day soon, I'll have my revenge."));
			add(new Bond(12,"I swindled and ruined a person who didn't deserve it. I seek to atone for my misdeeds but might never be able to forgive myself."));
			add(new Bond(13,"I'm trying to pay off an old debt I owe to a generous benefactor."));
			add(new Bond(14,"My ill-gotten gains go to support my family."));
			add(new Bond(15,"Something important was taken from me, and I aim to steal it back."));
			add(new Bond(16,"I will become the greatest thief that ever lived."));
			add(new Bond(17,"I'm guilty of a terrible crime. I hope I can redeem myself for it."));
			add(new Bond(18,"Someone I loved died because of a mistake I made. That will never happen again."));
			add(new Bond(19,"My instrument is my most treasured possession, and it reminds me of someone I love."));
			add(new Bond(20,"Someone stole my precious instrument, and someday I'll get it back."));
			add(new Bond(21,"I want to be famous, whatever it takes."));
			add(new Bond(22,"I idolize a hero of the old tales and measure my deeds against that person's."));
			add(new Bond(23,"I will do anything to prove myself superior to me hated rival."));
			add(new Bond(24,"I would do anything for the other members of my old troupe."));
			add(new Bond(25,"I have a family, but I have no idea where they are. One day, I hope to see them again."));
			add(new Bond(26,"I worked the land, I love the land, and I will protect the land."));
			add(new Bond(27,"A proud noble once gave me a horrible beating, and I will take my revenge on any bully I encounter."));
			add(new Bond(28,"My tools are symbols of my past life, and I carry them so that I will never forget my roots."));
			add(new Bond(29,"I protect those who cannot protect themselves."));
			add(new Bond(30,"I wish my childhood sweetheart had come with me to pursue my destiny."));
			add(new Bond(31,"The workshop where I learned my trade is the most important place in the world to me."));
			add(new Bond(32,"I created a great work for someone, and then found them unworthy to receive it. I'm still looking for someone worthy."));
			add(new Bond(33,"I owe my guild a great debt for forging me into the person I am today."));
			add(new Bond(34,"I pursue wealth to secure someone's love."));
			add(new Bond(35,"One day I will return to my guild and prove that I am the greatest artisan of them all."));
			add(new Bond(36,"I will get revenge on the evil forces that destroyed my place of business and ruined my livelihood."));
			add(new Bond(37,"Nothing is more important than the other members of my hermitage, order, or association."));
			add(new Bond(38,"I entered seclusion to hide from the ones who might still be hunting me. I must someday confront them."));
			add(new Bond(39,"I'm still seeking the enlightenment I pursued in my seclusion, and it still eludes me."));
			add(new Bond(40,"I entered seclusion because I loved someone I could not have."));
			add(new Bond(41,"Should my discovery come to light, it could bring ruin to the world."));
			add(new Bond(42,"My isolation gave me great insight into a great evil that only I can destroy."));
			add(new Bond(43,"I will face any challenge to win the approval of my family."));
			add(new Bond(44,"My house's alliance with another noble family must be sustained at all costs."));
			add(new Bond(45,"Nothing is more important that the other members of my family."));
			add(new Bond(46,"I am in love with the heir of a family that my family despises."));
			add(new Bond(47,"My loyalty to my sovereign is unwavering."));
			add(new Bond(48,"The common folk must see me as a hero of the people."));
			add(new Bond(49,"My family, clan, or tribe is the most important thing in my life, even when they are far from me."));
			add(new Bond(50,"An injury to the unspoiled wilderness of my home is an injury to me."));
			add(new Bond(51,"I will bring terrible wrath down on the evildoers who destroyed my homeland."));
			add(new Bond(52,"I am the last of my tribe, and it is up to me to ensure their names enter legend."));
			add(new Bond(53,"I suffer awful visions of a coming disaster and will do anything to prevent it."));
			add(new Bond(54,"It is my duty to provide children to sustain my tribe."));
			add(new Bond(55,"It is my duty to protect my students."));
			add(new Bond(56,"I have an ancient text that holds terrible secrets that must not fall into the wrong hands."));
			add(new Bond(57,"I work to preserve a library, university, scriptorium, or monastery."));
			add(new Bond(58,"My life's work is a series of tomes related to a specific field of lore."));
			add(new Bond(59,"I've been searching my whole life for the answer to a certain question."));
			add(new Bond(60,"I sold my soul for knowledge. I hope to do great deeds and win it back."));
			add(new Bond(61,"I'm loyal to my captain first, everything else second."));
			add(new Bond(62,"The ship is most important--crewmates and captains come and go."));
			add(new Bond(63,"I'll always remember my first ship."));
			add(new Bond(64,"In a harbor town, I have a paramour whose eyes nearly stole me from the sea."));
			add(new Bond(65,"I was cheated of my fair share of the profits, and I want to get my due."));
			add(new Bond(66,"Ruthless pirates murdered my captain and crewmates, plundered our ship, and left me to die. Vengeance will be mine."));
			add(new Bond(67,"I would lay down my life for the people I served with."));
			add(new Bond(68,"Someone saved my life on the battlefield. To this day, I will never leave a friend behind."));
			add(new Bond(69,"My honor is my life."));
			add(new Bond(70,"I'll never forget the crushing defeat my company suffered or the enemies who dealt it."));
			add(new Bond(71,"Those who fight beside me are those worth dying for."));
			add(new Bond(72,"I fight for those who cannot fight for themselves."));
			add(new Bond(73,"My town or city is my home, and I'll fight to defend it."));
			add(new Bond(74,"I sponsor an orphanage to keep others from enduring what I was forced to endure."));
			add(new Bond(75,"I owe my survival to another urchin who taught me to live on the streets."));
			add(new Bond(76,"I owe a debt I can never repay to the person who took pity on me."));
			add(new Bond(77,"I escaped my life of poverty by robbing an important person, and I'm wanted for it."));
			add(new Bond(78,"No one else is going to have to endure the hardships I've been through."));

		}};
		return bonds;
	}
	
	public List<Flaw> generateFlaws()
	{
		List<Flaw> flaws = new ArrayList<Flaw>() {/**
			 * 
			 */
			private static final long serialVersionUID = -5697274465454253872L;

		{
			add(new Flaw(1,"I judge others harshly, and myself even more severely."));
			add(new Flaw(2,"I put too much trust in those who wield power within my temple's hierarchy."));
			add(new Flaw(3,"My piety sometimes leads me to blindly trust those that profess faith in my god."));
			add(new Flaw(4,"I am inflexible in my thinking."));
			add(new Flaw(5,"I am suspicious of strangers and suspect the worst of them."));
			add(new Flaw(6,"Once I pick a goal, I become obsessed with it to the detriment of everything else in my life."));
			add(new Flaw(7,"I can't resist a pretty face."));
			add(new Flaw(8,"I'm always in debt. I spend my ill-gotten gains on decadent luxuries faster than I bring them in."));
			add(new Flaw(9,"I'm convinced that no one could ever fool me in the way I fool others."));
			add(new Flaw(10,"I'm too greedy for my own good. I can't resist taking a risk if there's money involved."));
			add(new Flaw(11,"I can't resist swindling people who are more powerful than me."));
			add(new Flaw(12,"I hate to admit it and will hate myself for it, but I'll run and preserve my own hide if the going gets tough."));
			add(new Flaw(13,"When I see something valuable, I can't think about anything but how to steal it."));
			add(new Flaw(14,"When faced with a choice between money and my friends, I usually choose the money."));
			add(new Flaw(15,"If there's a plan, I'll forget it. If I don't forget it, I'll ignore it."));
			add(new Flaw(16,"I have a 'tell' that reveals when I'm lying."));
			add(new Flaw(17,"I turn tail and run when things go bad."));
			add(new Flaw(18,"An innocent person is in prison for a crime that I committed. I'm okay with that."));
			add(new Flaw(19,"I'll do anything to win fame and renown."));
			add(new Flaw(20,"I'm a sucker for a pretty face."));
			add(new Flaw(21,"A scandal prevents me from ever going home again. That kind of trouble seems to follow me around."));
			add(new Flaw(22,"I once satirized a noble who still wants my head. It was a mistake that I will likely repeat."));
			add(new Flaw(23,"I have trouble keeping my true feelings hidden. My sharp tongue lands me in trouble."));
			add(new Flaw(24,"Despite my best efforts, I am unreliable to my friends."));
			add(new Flaw(25,"The tyrant who rules my land will stop at nothing to see me killed."));
			add(new Flaw(26,"I'm convinced of the significance of my destiny, and blind to my shortcomings and the risk of failure."));
			add(new Flaw(27,"The people who knew me when I was young know my shameful secret, so I can never go home again."));
			add(new Flaw(28,"I have a weakness for the vices of the city, especially hard drink."));
			add(new Flaw(29,"Secretly, I believe that things would be better if I were a tyrant lording over the land."));
			add(new Flaw(30,"I have trouble trusting in my allies."));
			add(new Flaw(31,"I'll do anything to get my hands on something rare or priceless."));
			add(new Flaw(32,"I'm quick to assume that someone is trying to cheat me."));
			add(new Flaw(33,"No one must ever learn that I once stole money from guild coffers."));
			add(new Flaw(34,"I'm never satisfied with what I have--I always want more."));
			add(new Flaw(35,"I would kill to acquire a noble title."));
			add(new Flaw(36,"I'm horribly jealous of anyone who outshines my handiwork. Everywhere I go, I'm surrounded by rivals."));
			add(new Flaw(37,"Now that I've returned to the world, I enjoy its delights a little too much."));
			add(new Flaw(38,"I harbor dark bloodthirsty thoughts that my isolation failed to quell."));
			add(new Flaw(39,"I am dogmatic in my thoughts and philosophy."));
			add(new Flaw(40,"I let my need to win arguments overshadow friendships and harmony."));
			add(new Flaw(41,"I'd risk too much to uncover a lost bit of knowledge."));
			add(new Flaw(42,"I like keeping secrets and won't share them with anyone."));
			add(new Flaw(43,"I secretly believe that everyone is beneath me."));
			add(new Flaw(44,"I hide a truly scandalous secret that could ruin my family forever."));
			add(new Flaw(45,"I too often hear veiled insults and threats in every word addressed to me, and I'm quick to anger."));
			add(new Flaw(46,"I have an insatiable desire for carnal pleasures."));
			add(new Flaw(47,"In fact, the world does revolve around me."));
			add(new Flaw(48,"By my words and actions, I often bring shame to my family."));
			add(new Flaw(49,"I am too enamored of ale, wine, and other intoxicants."));
			add(new Flaw(50,"There's no room for caution in a life lived to the fullest."));
			add(new Flaw(51,"I remember every insult I've received and nurse a silent resentment toward anyone who's ever wronged me."));
			add(new Flaw(52,"I am slow to trust members of other races"));
			add(new Flaw(53,"Violence is my answer to almost any challenge."));
			add(new Flaw(54,"Don't expect me to save those who can't save themselves. It is nature's way that the strong thrive and the weak perish."));
			add(new Flaw(55,"I am easily distracted by the promise of information."));
			add(new Flaw(56,"Most people scream and run when they see a demon. I stop and take notes on its anatomy."));
			add(new Flaw(57,"Unlocking an ancient mystery is worth the price of a civilization."));
			add(new Flaw(58,"I overlook obvious solutions in favor of complicated ones."));
			add(new Flaw(59,"I speak without really thinking through my words, invariably insulting others."));
			add(new Flaw(60,"I can't keep a secret to save my life, or anyone else's."));
			add(new Flaw(61,"I follow orders, even if I think they're wrong."));
			add(new Flaw(62,"I'll say anything to avoid having to do extra work."));
			add(new Flaw(63,"Once someone questions my courage, I never back down no matter how dangerous the situation."));
			add(new Flaw(64,"Once I start drinking, it's hard for me to stop."));
			add(new Flaw(65,"I can't help but pocket loose coins and other trinkets I come across."));
			add(new Flaw(66,"My pride will probably lead to my destruction"));
			add(new Flaw(67,"The monstrous enemy we faced in battle still leaves me quivering with fear."));
			add(new Flaw(68,"I have little respect for anyone who is not a proven warrior."));
			add(new Flaw(69,"I made a terrible mistake in battle that cost many lives--and I would do anything to keep that mistake secret."));
			add(new Flaw(70,"My hatred of my enemies is blind and unreasoning."));
			add(new Flaw(71,"I obey the law, even if the law causes misery."));
			add(new Flaw(72,"I'd rather eat my armor than admit when I'm wrong."));
			add(new Flaw(73,"If I'm outnumbered, I always run away from a fight."));
			add(new Flaw(74,"Gold seems like a lot of money to me, and I'll do just about anything for more of it."));
			add(new Flaw(75,"I will never fully trust anyone other than myself."));
			add(new Flaw(76,"I'd rather kill someone in their sleep than fight fair."));
			add(new Flaw(77,"It's not stealing if I need it more than someone else."));
			add(new Flaw(78,"People who don't take care of themselves get what they deserve."));

		}};
		
		return flaws;
	}
	
	public List<Trait> generateTraits()
	{
		List<Trait> traits = new ArrayList<Trait>() {/**
			 * 
			 */
			private static final long serialVersionUID = -7029458265461728023L;

		{
			add(new Trait(1,"I idolize a particular hero of my faith and constantly refer to that person's deeds and example.")); 
			add(new Trait(2,"I can find common ground between the fiercest enemies, empathizing with them and always working toward peace."));
			add(new Trait(3,"I see omens in every event and action. The gods try to speak to us, we just need to listen."));
			add(new Trait(4,"Nothing can shake my optimistic attitude."));
			add(new Trait(5,"I quote (or misquote) the sacred texts and proverbs in almost every situation."));
			add(new Trait(6,"I am tolerant (or intolerant) of other faiths and respect (or condemn) the worship of other gods."));
			add(new Trait(7,"I've enjoyed fine food, drink, and high society among my temple's elite. Rough living grates on me."));
			add(new Trait(8,"I've spent so long in the temple that I have little practical experience dealing with people in the outside world."));
			add(new Trait(9,"I fall in and out of love easily, and am always pursuing someone."));
			add(new Trait(10,"I have a joke for every occasion, especially occasions where humor is inappropriate."));
			add(new Trait(11,"Flattery is my preferred trick for getting what I want."));
			add(new Trait(12,"I'm a born gambler who can't resist taking a risk for a potential payoff."));
			add(new Trait(13,"I lie about almost everything, even when there's no good reason to."));
			add(new Trait(14,"Sarcasm and insults are my weapons of choice."));
			add(new Trait(15,"I keep multiple holy symbols on me and invoke whatever deity might come in useful at any given moment."));
			add(new Trait(16,"I pocket anything I see that might have some value."));
			add(new Trait(17,"I always have plan for what to do when things go wrong."));
			add(new Trait(18,"I am always calm, no matter what the situation. I never raise my voice or let my emotions control me."));
			add(new Trait(19,"The first thing I do in a new place is note the locations of everything valuable--or where such things could be hidden."));
			add(new Trait(20,"I would rather make a new friend than a new enemy."));
			add(new Trait(21,"I am incredibly slow to trust. Those who seem the fairest often have the most to hide."));
			add(new Trait(22,"I don't pay attention to the risks in a situation. Never tell me the odds."));
			add(new Trait(23,"The best way to get me to do something is to tell me I can't do it."));
			add(new Trait(24,"I blow up at the slightest insult."));
			add(new Trait(25,"I know a story relevant to almost every situation."));
			add(new Trait(26,"Whenever I come to a new place, I collect local rumors and spread gossip."));
			add(new Trait(27,"I'm a hopeless romantic, always searching for that 'special someone'."));
			add(new Trait(28,"Nobody stays angry at me or around me for long, since I can defuse any amount of tension."));
			add(new Trait(29,"I love a good insult, even one directed at me."));
			add(new Trait(30,"I get bitter if I'm not the center of attention."));
			add(new Trait(31,"I'll settle for nothing less than perfection."));
			add(new Trait(32,"I change my mood or my mind as quickly as I change key in a song."));
			add(new Trait(33,"I judge people by their actions, not their words."));
			add(new Trait(34,"If someone is in trouble, I'm always willing to lend help."));
			add(new Trait(35,"When I set my mind to something, I follow through no matter what gets in my way."));
			add(new Trait(36,"I have a strong sense of fair play and always try to find the most equitable solution to arguments."));
			add(new Trait(37,"I'm confident in my own abilities and do what I can to instill confidence in others."));
			add(new Trait(38,"Thinking is for other people. I prefer action."));
			add(new Trait(39,"I misuse long words in an attempt to sound smarter."));
			add(new Trait(40,"I get bored easily. When am I going to get on with my destiny."));
			add(new Trait(41,"I believe that everything worth doing is worth doing right. I can't help it--I'm a perfectionist."));
			add(new Trait(42,"I'm a snob who looks down on those who can't appreciate fine art."));
			add(new Trait(43,"I always want to know how things work and what makes people tick."));
			add(new Trait(44,"I'm full of witty aphorisms and have a proverb for every occasion."));
			add(new Trait(45,"I'm rude to people who lack my commitment to hard work and fair play."));
			add(new Trait(46,"I like to talk at length about my profession."));
			add(new Trait(47,"I don't part with my money easily and will haggle tirelessly to get the best deal possible."));
			add(new Trait(48,"I'm well known for my work, and I want to make sure everyone appreciates it. I'm always taken aback when people haven't heard of me."));
			add(new Trait(49,"I've been isolated for so long that I rarely speak, preferring gestures and the occasional grunt."));
			add(new Trait(50,"I am utterly serene, even in the face of disaster."));
			add(new Trait(51,"The leader of my community has something wise to say on every topic, and I am eager to share that wisdom."));
			add(new Trait(52,"I feel tremendous empathy for all who suffer."));
			add(new Trait(53,"I'm oblivious to etiquette and social expectations."));
			add(new Trait(54,"I connect everything that happens to me to a grand cosmic plan."));
			add(new Trait(55,"I often get lost in my own thoughts and contemplations, becoming oblivious to my surroundings."));
			add(new Trait(56,"I am working on a grand philosophical theory and love sharing my ideas."));
			add(new Trait(57,"My eloquent flattery makes everyone I talk to feel like the most wonderful and important person in the world."));
			add(new Trait(58,"The common folk love me for my kindness and generosity."));
			add(new Trait(59,"No one could doubt by looking at my regal bearing that I am a cut above the unwashed masses."));
			add(new Trait(60,"I take great pains to always look my best and follow the latest fashions."));
			add(new Trait(61,"I don't like to get my hands dirty, and I won't be caught dead in unsuitable accommodations."));
			add(new Trait(62,"Despite my birth, I do not place myself above other folk. We all have the same blood."));
			add(new Trait(63,"My favor, once lost, is lost forever."));
			add(new Trait(64,"If you do me an injury, I will crush you, ruin your name, and salt your fields."));
			add(new Trait(65,"I'm driven by a wanderlust that led me away from home."));
			add(new Trait(66,"I watch over my friends as if they were a litter of newborn pups."));
			add(new Trait(67,"I once ran twenty-five miles without stopping to warn my clan of an approaching orc horde. I'd do it again if I had to."));
			add(new Trait(68,"I have a lesson for every situation, drawn from observing nature."));
			add(new Trait(69,"I place no stock in wealthy or well-mannered folk. Money and manners won't save you from a hungry owlbear."));
			add(new Trait(70,"I'm always picking things up, absently fiddling with them, and sometimes accidentally breaking them."));
			add(new Trait(71,"I feel far more comfortable around animals than people."));
			add(new Trait(72,"I was, in fact, raised by wolves."));
			add(new Trait(73,"I use polysyllabic words to convey the impression of great erudition."));
			add(new Trait(74,"I've read every book in the world's greatest libraries--or like to boast that I have."));
			add(new Trait(75,"I'm used to helping out those who aren't as smart as I am, and I patiently explain anything and everything to others."));
			add(new Trait(76,"There's nothing I like more than a good mystery."));
			add(new Trait(77,"I'm willing to listen to every side of an argument before I make my own judgment."));
			add(new Trait(78,"I...speak...slowly...when talking...to idiots...which...almost...everyone...is...compared ...to me."));
			add(new Trait(79,"I am horribly, horribly awkward in social situations."));
			add(new Trait(80,"I'm convinced that people are always trying to steal my secrets."));
			add(new Trait(81,"My friends know they can rely on me, no matter what."));
			add(new Trait(82,"I work hard so that I can play hard when the work is done."));
			add(new Trait(83,"I enjoy sailing into new ports and making new friends over a flagon of ale."));
			add(new Trait(84,"I stretch the truth for the sake of a good story."));
			add(new Trait(85,"To me, a tavern brawl is a nice way to get to know a new city."));
			add(new Trait(86,"I never pass up a friendly wager."));
			add(new Trait(87,"My language is as foul as an otyugh nest."));
			add(new Trait(88,"I like a job well done, especially if I can convince someone else to do it."));
			add(new Trait(89,"I'm always polite and respectful."));
			add(new Trait(90,"I'm haunted by memories of war. I can't get the images of violence out of my mind."));
			add(new Trait(91,"I've lost too many friends, and I'm slow to make new ones."));
			add(new Trait(92,"I'm full of inspiring and cautionary tales from my military experience relevant to almost every combat situation."));
			add(new Trait(93,"I can stare down a hellhound without flinching."));
			add(new Trait(94,"I enjoy being strong and like breaking things."));
			add(new Trait(95,"I have a crude sense of humor."));
			add(new Trait(96,"I face problems head-on. A simple direct solution is the best path to success."));
			add(new Trait(97,"I hide scraps of food and trinkets away in my pockets."));
			add(new Trait(98,"I ask a lot of questions."));
			add(new Trait(99,"I like to squeeze into small places where no one else can get to me."));
			add(new Trait(100,"I sleep with my back to a wall or tree, with everything I own wrapped in a bundle in my arms."));
			add(new Trait(101,"I eat like a pig and have bad manners."));
			add(new Trait(102,"I think anyone who's nice to me is hiding evil intent."));
			add(new Trait(103,"I don't like to bathe."));
			add(new Trait(104,"I bluntly say what other people are hinting or hiding."));

		}};
	    return traits;
	}

	public List<Oath> generateOaths() {
		List<Oath> oaths = new ArrayList<Oath>();
		
		Oath devotion = new Oath(1, "OATHOFDEVOTION",new ArrayList<String>() {/**
			 * 
			 */
			private static final long serialVersionUID = 8645556480079434489L;

		{
	    add("Honesty: Don't lie or cheat. Let your word be your promise.");
	    add("Courage: Never fear to act, though caution is wise.");
	    add("Compassion: Aid others, protect the weak, and punish those who threaten them. Show mercy to your foes, but temper it with Wisdom.");
	    add("Honor: Treat others with fairness, and let your honorable deeds be an example to them. Do as much good as possible while causing the least amount of harm.");
	    add("Duty: Be responsible for your actions and their consequences, protect those entrusted to your care, and obey those who have just authority over you.");
		}});
		
		oaths.add(devotion);
		
		Oath ancients = new Oath(2, "OATHOFTHEANCIENTS", new ArrayList<String>() {/**
			 * 
			 */
			private static final long serialVersionUID = -8620492639333309551L;

		{
	    add("Kindle the Light. Through your acts of mercy, kindness, and forgiveness, kindle the light of hope in the world, beating back despair.");
	    add("Shelter the Light. Where there is good, beauty, love, and laughter in the world, stand against the wickedness that would swallow it. Where life flourishes, stand against the forces that would render it barren.");
	    add("Preserve Your Own Light. Delight in song and laughter, in beauty and art. If you allow the light to die in your own heart, you can't preserve it in the world.");
	    add("Be the Light. Be a glorious beacon for all who live in despair. Let the light of your joy and courage shine forth in all your deeds.");
		}});
		
		oaths.add(ancients);
		
		Oath vengeance = new Oath(3, "OATHOFVENGEANCE", new ArrayList<String>() {/**
			 * 
			 */
			private static final long serialVersionUID = -1935429221551125606L;

		{
		add("Fight the Greater Evil. Faced with a choice of fighting my sworn foes or combating a lesser evil, I choose the greater evil.");
		add("No Mercy for the Wicked. Ordinary foes might win my mercy, but my sworn enemies do not.");
		add("By Any Means Necessary. My qualms can't get in the way of exterminating my foes.");
		add("Restitution. If my foes wreak ruin on the world, it is because I failed to stop them. I must help those harmed by their misdeeds.");
		}});
		
		oaths.add(vengeance);
		return oaths;
	}

	@Override
	public List<Ideal> getIdeals() {
		return ideals;
	}

	@Override
	public void setIdeals(List<Ideal> ideals) {
		this.ideals = ideals;
	}

	@Override
	public Bond getBond(int bondId) {
		// TODO Auto-generated method stub
		return getBonds().stream().filter(e -> e.id == bondId).findFirst().orElse(null);
	}
	
	@Override
	public Oath getOath(int oathId) {
		// TODO Auto-generated method stub
		return getOaths().stream().filter(e -> e.id == oathId).findFirst().orElse(null);
	}

	@Override
	public Flaw getFlaw(int flawId) {
		// TODO Auto-generated method stub
		return getFlaws().stream().filter(e -> e.id == flawId).findFirst().orElse(null);
	}

	@Override
	public Ideal getIdeal(int idealId) {
		// TODO Auto-generated method stub
		return getIdeals().stream().filter(e -> e.id == idealId).findFirst().orElse(null);
	}

	@Override
	public Trait getTrait(int traitId) {
		// TODO Auto-generated method stub
		return getTraits().stream().filter(e -> e.id == traitId).findFirst().orElse(null);
	}

	@Override
	public CharacterCreation getCharacterCreationChoices() {
		CharacterCreation characterCreation = new CharacterCreation();
		List<ISoliniaClass> classes = getClasses();
		
		for(ISoliniaRace race : this.getRaces())
		{
			if (race.isAdmin())
				continue;
			
			for (ISoliniaClass solclass : classes) {
				if (!solclass.getValidRaceClasses().containsKey(race.getId()))
					continue;
				
				RaceChoice raceChoice = new RaceChoice(race.getId(),solclass.getId(),race.getName(),solclass.getName(),race.getShortName(),solclass.getShortName(),race.getDescription(),solclass.getDescription(),race.getStrength(),race.getStamina(),race.getAgility(),race.getDexterity(),race.getIntelligence(),race.getWisdom(),race.getCharisma(), race.getAlignment());
				characterCreation.raceChoices.put(raceChoice.RaceName +"_"+raceChoice.ClassName, raceChoice);
			}
		}
		
		return characterCreation;
	}
	
	@Override
	public void setConfigSettings(ConfigSettings configSettings) {
		this.configSettings = configSettings;
	}
	
	@Override
	public int getMaxLevel()
	{
		return this.configSettings.MaxLevel;
	}
	
	@Override
	public double getMaxExperience()
	{
		// get the max level, then get 1 point of experience less than the next level after it
		// this allows players to have a buffer experience incase they die
		return PlayerUtils.getExperienceRequirementForLevel((getMaxLevel() + 1))-1;
	}
	
	@Override
	public ConfigSettings getConfigSettings()
	{
		return this.configSettings;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueuedCastingPercentPackets() {
		return queuedCastingPercentPackets;
	}

	@Override
	public void setQueuedCastingPercentPackets(ConcurrentHashMap<UUID, String> queuedCastingPercentPackets) {
		this.queuedCastingPercentPackets = queuedCastingPercentPackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueuedEffectsPackets() {
		return queuedEffectsPackets;
	}

	@Override
	public void setQueuedEffectsPackets(ConcurrentHashMap<UUID, String> queuedEffectsPackets) {
		this.queuedEffectsPackets = queuedEffectsPackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueuedEquipSlotsPackets() {
		return queuedEquipSlotsPackets;
	}

	@Override
	public void setQueuedEquipSlotsPackets(ConcurrentHashMap<UUID, String> queuedEquipSlotsPackets) {
		this.queuedEquipSlotsPackets = queuedEquipSlotsPackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueuedMemorisedSpellsPackets() {
		return queuedMemorisedSpellsPackets;
	}

	@Override
	public void setQueuedMemorisedSpellsPackets(ConcurrentHashMap<UUID, String> queuedMemorisedSpellsPackets) {
		this.queuedMemorisedSpellsPackets = queuedMemorisedSpellsPackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueueSpellbookPagePackets() {
		return queueSpellbookPagePackets;
	}

	@Override
	public void setQueueSpellbookPagePackets(ConcurrentHashMap<UUID, String> queueSpellbookPagePackets) {
		this.queueSpellbookPagePackets = queueSpellbookPagePackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueuedCharCreationPackets() {
		return queuedCharCreationPackets;
	}

	@Override
	public void setQueuedCharCreationPackets(ConcurrentHashMap<UUID, String> queuedCharCreationPackets) {
		this.queuedCharCreationPackets = queuedCharCreationPackets;
	}

	@Override
	public ConcurrentHashMap<UUID, String> getQueueMobVitalsPackets(int queueNumber) throws Exception {
		switch (queueNumber)
		{
			case -2:
				return this.queueMobVitalsPacketsneg2;
			case -1:
				return this.queueMobVitalsPacketsneg1;
			case 0:
				return this.queueMobVitalsPackets0;
			case 1:
				return this.queueMobVitalsPackets1;
			case 2:
				return this.queueMobVitalsPackets2;
			case 3:
				return this.queueMobVitalsPackets3;
			case 4:
				return this.queueMobVitalsPackets4;
			case 5:
				return this.queueMobVitalsPackets5;
			default:
				throw new Exception("Request to call invalid mob vitals packet queue: " + queueNumber);
		}
	}

	@Override
	public void setQueueMobVitalsPackets(int queueNumber, ConcurrentHashMap<UUID, String> queueMobVitalsPackets) throws Exception {
		switch (queueNumber)
		{
			case -2:
				this.queueMobVitalsPacketsneg2 = queueMobVitalsPackets;
				break;
			case -1:
				this.queueMobVitalsPacketsneg1 = queueMobVitalsPackets;
				break;
			case 0:
				this.queueMobVitalsPackets0 = queueMobVitalsPackets;
				break;
			case 1:
				this.queueMobVitalsPackets1 = queueMobVitalsPackets;
				break;
			case 2:
				this.queueMobVitalsPackets2 = queueMobVitalsPackets;
				break;
			case 3:
				this.queueMobVitalsPackets3 = queueMobVitalsPackets;
				break;
			case 4:
				this.queueMobVitalsPackets4 = queueMobVitalsPackets;
				break;
			case 5:
				this.queueMobVitalsPackets5 = queueMobVitalsPackets;
				break;
			default:
				throw new Exception("Request to call invalid mob vitals packet queue: " + queueNumber);
		}
	}

	@Override
	public ConcurrentHashMap<UUID, Float> getLastSentPlayerManaPercent() {
		return lastSentPlayerManaPercent;
	}

	@Override
	public void setLastSentPlayerManaPercent(ConcurrentHashMap<UUID, Float> lastSentPlayerManaPercent) {
		this.lastSentPlayerManaPercent = lastSentPlayerManaPercent;
	}

	@Override
	public void removeFellowship(int id) {
		Fellowship fellowship = this.getFellowship(id);
		if(fellowship == null)
			return;
		
		try
		{
			for (int i = 0; i < fellowship.getMembers().size(); i++) {
				UUID newownerCharacterId = fellowship.getMembers().get(i);
				try
				{
					ISoliniaPlayer member = StateManager.getInstance().getPlayerManager().getArchivedCharacterOrActivePlayerByCharacterUUID(newownerCharacterId);
					member.setCharacterFellowshipId(0);
				} catch (PlayerDoesNotExistException e)
				{
					
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		fellowship.getMembers().clear();
		fellowship.setOwnerUuid(null);
		
		this.fellowshipRepository.remove(fellowship);
	}
}
