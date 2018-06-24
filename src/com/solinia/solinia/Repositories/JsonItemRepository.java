package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Models.SoliniaItem;

public class JsonItemRepository implements IRepository<ISoliniaItem> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaItem> items = new ConcurrentHashMap<Integer, ISoliniaItem>();

	
	@Override
	public void add(ISoliniaItem item) {
		this.items.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaItem> items) {
		for(ISoliniaItem i : items)
		{
			this.items.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaItem item) {
		this.items.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaItem item) {
		this.items.remove(item.getId());
	}
	
	@Override
	public void remove(Predicate<ISoliniaItem> filter) {
		for(ISoliniaItem i : items.values().stream().filter(filter).collect(Collectors.toList()))
		{
			items.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaItem> query(Predicate<ISoliniaItem> filter) {
		return items.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaItem> file = new ArrayList<ISoliniaItem>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaItem>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		items.clear();
		for(ISoliniaItem i : file)
		{
			items.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + items.size() + " items");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(items.values(), new TypeToken<List<SoliniaItem>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + items.size() + " items");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public ISoliniaItem getByKey(Object key) {
		return this.items.get(key);
	}
	
	@Override
	public void writeCsv(String filePath)
	{
		try (
	            BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
	            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
	            		"id","displayname","basename","abilityid","strength","stamina","agility","dexterity","intelligence","wisdom","charisma","allowedClassNames","questitem","damage","weaponabilityid","attackspeed","enchantment1val","enchantment2val","enchantment3val","enchantment4val","hpregen","mpregen","worth","coreitem","fireResist","coldResist","magicResist","poisonResist","diseaseResist","spellscroll","color","dye","isTemporary","isConsumable","baneUndead","isPetControlRod","isAugmentation","isQuest","augmentationFitsSlotType","discoverer","minLevel","ac","hp","mana","isExperienceBonus","skillModType","skillModValue","reagent","throwing","artifact","artifactFound","skillModType2","skillModValue2","skillModType3","skillModValue3","skillModType4","skillModValue4","operatorCreated","isFingersItem","isNeckItem","isShouldersItem","isEarsItem","territoryFlag","identifyMessage","bandage"
	            	));
	        ) 
		{
			for(Entry<Integer, ISoliniaItem> keyValuePair : items.entrySet())
			{
				ISoliniaItem item =  keyValuePair.getValue();
				
	            csvPrinter.printRecord(item.getId(), 
	            		item.getDisplayname(), 
	            		item.getBasename(), 
	            		item.getAbilityid(), 
	            		item.getStrength(), 
	            		item.getStamina(), 
	            		item.getAgility(), 
	            		item.getDexterity(), 
	            		item.getIntelligence(), 
	            		item.getWisdom(), 
	            		item.getCharisma(), 
	            		Arrays.asList(item.getAllowedClassNames().stream()
	  					      .map(t -> t.toString())
	  					      .collect(Collectors.joining(","))),  // comma seperated class list
	            		item.getQuestitem(), 
	            		item.getDamage(), 
	            		item.getWeaponabilityid(),
	            		item.getAttackspeed(), 
	            		item.getEnchantment1val(), 
	            		item.getEnchantment2val(), 
	            		item.getEnchantment3val(), 
	            		item.getEnchantment4val(), 
	            		item.getHpregen(), 
	            		item.getMpregen(), 
	            		item.getWorth(),
	            		item.isCoreitem(), 
	            		item.getFireResist(), 
	            		item.getColdResist(), 
	            		item.getMagicResist(), 
	            		item.getPoisonResist(), 
	            		item.getDiseaseResist(), 
	            		item.isSpellscroll(), 
	            		item.getColor(), 
	            		item.getDye(), 
	            		item.isTemporary(), 
	            		item.isConsumable(), 
	            		item.getBaneUndead(), 
	            		item.isPetControlRod(), 
	            		item.isAugmentation(), 
	            		item.isQuest(), 
	            		item.getAugmentationFitsSlotType(), 
	            		item.getDiscoverer(), 
	            		item.getMinLevel(), 
	            		item.getAC(), 
	            		item.getHp(), 
	            		item.getMana(), 
	            		item.isExperienceBonus(), 
	            		item.getSkillModType(), 
	            		item.getSkillModValue(), 
	            		item.isReagent(), 
	            		item.isThrowing(), 
	            		item.isArtifact(), 
	            		item.isArtifactFound(), 
	            		item.getSkillModType2(), 
	            		item.getSkillModValue2(), 
	            		item.getSkillModType3(), 
	            		item.getSkillModValue3(), 
	            		item.getSkillModType4(), 
	            		item.getSkillModValue4(), 
	            		item.isOperatorCreated(), 
	            		item.isFingersItem(), 
	            		item.isNeckItem(), 
	            		item.isShouldersItem(), 
	            		item.isEarsItem(), 
	            		item.isTerritoryFlag(), 
	            		item.getIdentifyMessage(), 
	            		item.isBandage()
	            		);
			}
			
			csvPrinter.flush();            
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
