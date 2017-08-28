package com.solinia.solinia.Managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.IConfigurationManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaFaction;
import com.solinia.solinia.Models.SoliniaNPC;
import com.solinia.solinia.Repositories.JsonFactionRepository;
import com.solinia.solinia.Repositories.JsonLootDropEntryRepository;
import com.solinia.solinia.Repositories.JsonLootDropRepository;
import com.solinia.solinia.Repositories.JsonLootTableEntryRepository;
import com.solinia.solinia.Repositories.JsonLootTableRepository;
import com.solinia.solinia.Repositories.JsonNPCMerchantRepository;
import com.solinia.solinia.Repositories.JsonNPCRepository;

public class ConfigurationManager implements IConfigurationManager {

	private IRepository<ISoliniaRace> raceRepository;
	private IRepository<ISoliniaClass> classRepository;
	private IRepository<ISoliniaItem> itemRepository;
	private IRepository<ISoliniaSpell> spellRepository;
	private IRepository<ISoliniaFaction> factionRepository;
	private IRepository<ISoliniaNPC> npcRepository;
	private IRepository<ISoliniaNPCMerchant> npcmerchantRepository;
	private IRepository<ISoliniaLootTable> loottableRepository;
	private IRepository<ISoliniaLootTableEntry> loottableentryRepository;
	private IRepository<ISoliniaLootDrop> lootdropRepository;
	private IRepository<ISoliniaLootDropEntry> lootdropentryRepository;
	
	public ConfigurationManager(IRepository<ISoliniaRace> raceContext, IRepository<ISoliniaClass> classContext, IRepository<ISoliniaItem> itemContext, IRepository<ISoliniaSpell> spellContext, JsonFactionRepository factionContext, JsonNPCRepository npcContext, JsonNPCMerchantRepository npcmerchantContext, JsonLootTableRepository loottableContext, JsonLootTableEntryRepository loottableentryContext, JsonLootDropRepository lootdropContext, JsonLootDropEntryRepository lootdropentryContext)
	{
		this.raceRepository = raceContext;
		this.classRepository = classContext;
		this.itemRepository = itemContext;
		this.spellRepository = spellContext;
		this.factionRepository = factionContext;
		this.npcRepository = npcContext;
		this.npcmerchantRepository = npcmerchantContext;
		this.loottableRepository = loottableContext;
		this.loottableentryRepository = loottableentryContext;
		this.lootdropRepository = lootdropContext;
		this.lootdropentryRepository = lootdropentryContext;
	}
	
	@Override
	public List<ISoliniaNPCMerchant> getNPCMerchants() {
		// TODO Auto-generated method stub
		return npcmerchantRepository.query(q ->q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaLootTable> getLootTables() {
		// TODO Auto-generated method stub
		return loottableRepository.query(q ->q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaLootTableEntry> getLootTableEntrys() {
		// TODO Auto-generated method stub
		return loottableentryRepository.query(q ->q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaLootDrop> getLootDrops() {
		// TODO Auto-generated method stub
		return lootdropRepository.query(q ->q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaLootDropEntry> getLootDropEntrys() {
		// TODO Auto-generated method stub
		return lootdropentryRepository.query(q ->q.getId() > 0);
	}
	
	@Override
	public List<ISoliniaFaction> getFactions() {
		// TODO Auto-generated method stub
		return factionRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaNPC> getNPCs() {
		// TODO Auto-generated method stub
		return npcRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaRace> getRaces() {
		// TODO Auto-generated method stub
		return raceRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaClass> getClasses() {
		// TODO Auto-generated method stub
		return classRepository.query(q ->q.getName() != null);
	}
	
	@Override
	public List<ISoliniaItem> getItems() {
		// TODO Auto-generated method stub
		return itemRepository.query(q ->q.getId() > 0);
	}

	@Override
	public ISoliniaRace getRace(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaRace> list = raceRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaFaction getFaction(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaFaction> list = factionRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaNPCMerchant getNPCMerchant(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaNPCMerchant> list = npcmerchantRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaLootTable getLootTable(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaLootTable> list = loottableRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaLootTableEntry getLootTableEntry(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaLootTableEntry> list = loottableentryRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaLootDrop getLootDrop(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaLootDrop> list = lootdropRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaLootDropEntry getLootDropEntry(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaLootDropEntry> list = lootdropentryRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaNPC getNPC(int Id) {
		// TODO Auto-generated method stub
		List<ISoliniaNPC> list = npcRepository.query(q ->q.getId() == Id);
		if (list.size() > 0)
			return list.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaClass getClassObj(int classId) {
		// TODO Auto-generated method stub
		List<ISoliniaClass> classes = classRepository.query(q ->q.getId() == classId);
		if (classes.size() > 0)
			return classes.get(0);
			
		return null;
	}

	@Override
	public ISoliniaRace getRace(String race) {
		
		List<ISoliniaRace> races = raceRepository.query(q ->q.getName().toUpperCase().equals(race.toUpperCase()));
		if (races.size() > 0)
			return races.get(0);
			
		return null;
	}

	@Override
	public ISoliniaItem getItem(int Id) {
		
		List<ISoliniaItem> items = itemRepository.query(q ->q.getId() == Id);
		if (items.size() > 0)
			return items.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaSpell getSpell(int Id) {
		
		List<ISoliniaSpell> items = spellRepository.query(q ->q.getId() == Id);
		if (items.size() > 0)
			return items.get(0);
			
		return null;
	}
	
	@Override
	public ISoliniaItem getItem(ItemStack itemStack) {
		int Id = (itemStack.getEnchantmentLevel(Enchantment.OXYGEN)-1000);
		List<ISoliniaItem> items = itemRepository.query(q ->q.getId() == Id);
		if (items.size() > 0)
			return items.get(0);
			
		return null;
	}
	
	
	@Override
	public ISoliniaClass getClassObj(String classname) {
		
		List<ISoliniaClass> classes = classRepository.query(q ->q.getName().toUpperCase().equals(classname.toUpperCase()));
		if (classes.size() > 0)
			return classes.get(0);
			
		return null;
	}
	
	@Override
	public void commit() {
		this.raceRepository.commit();
		this.classRepository.commit();
		this.itemRepository.commit();
		// TODO this is never needed?
		//this.spellRepository.commit();
		this.factionRepository.commit();
		this.npcRepository.commit();
		this.npcmerchantRepository.commit();
		this.loottableRepository.commit();
		this.loottableentryRepository.commit();
		this.lootdropRepository.commit();
		this.lootdropentryRepository.commit();
	}

	@Override
	public void addNPCMerchant(ISoliniaNPCMerchant merchant) {
		this.npcmerchantRepository.add(merchant);
		
	}
	
	@Override
	public void addLootTable(ISoliniaLootTable table) {
		this.loottableRepository.add(table);
		
	}
	
	@Override
	public void addLootTableEntry(ISoliniaLootTableEntry entry) {
		this.loottableentryRepository.add(entry);
		
	}
	
	@Override
	public void addLootDrop(ISoliniaLootDrop drop) {
		this.lootdropRepository.add(drop);
		
	}
	
	@Override
	public void addLootDropEntry(ISoliniaLootDropEntry entry) {
		this.lootdropentryRepository.add(entry);
		
	}
	
	@Override
	public void addRace(ISoliniaRace race) {
		this.raceRepository.add(race);
		
	}
		
	@Override
	public void addClass(ISoliniaClass classobj) {
		this.classRepository.add(classobj);
		
	}

	@Override
	public int getNextRaceId() {
		int maxRace = 0;
		for(ISoliniaRace race : getRaces())
		{
			if (race.getId() > maxRace)
				maxRace = race.getId();
		}
		
		return maxRace + 1;
	}
	
	@Override
	public int getNextClassId() {
		int maxClass = 0;
		for(ISoliniaClass classInstance : getClasses())
		{
			if (classInstance.getId() > maxClass)
				maxClass = classInstance.getId();
		}
		
		return maxClass + 1;
	}
	
	@Override
	public int getNextItemId() {
		int maxItem = 0;
		for(ISoliniaItem itemInstance : getItems())
		{
			if (itemInstance.getId() > maxItem)
				maxItem = itemInstance.getId();
		}
		
		return maxItem + 1;
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
	public List<ISoliniaSpell> getSpells() {
		return spellRepository.query(q ->q.getId() != null);
	}
	
	@Override
	public List<ISoliniaItem> getSpellItem(int Id) {
		return itemRepository.query(q ->q.isSpellscroll() == true && q.getAbilityid() == Id);
	}

	@Override
	public void updateItem(ISoliniaItem item) {
		this.itemRepository.update(item);
		
	}

	@Override
	public void editNPC(int npcid, String setting, String value) throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException {
		getNPC(npcid).editSetting(setting, value);
	}

	@Override
	public int getNextFactionId() {
		int max = 0;
		for(ISoliniaFaction entry : getFactions())
		{
			if (entry.getId() > max)
				max = entry.getId();
		}
		
		return max + 1;
	}

	@Override
	public void addFaction(SoliniaFaction faction) {
		this.factionRepository.add(faction);
	}

	@Override
	public int getNextNPCId() {
		int max = 0;
		for(ISoliniaNPC entry : getNPCs())
		{
			if (entry.getId() > max)
				max = entry.getId();
		}
		
		return max + 1;
	}

	@Override
	public void addNPC(SoliniaNPC npc) {
		this.npcRepository.add(npc);
	}
}
