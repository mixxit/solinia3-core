package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Factories.ISoliniaLootTableEntryTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Models.SoliniaLootTable;
import com.solinia.solinia.Models.SoliniaLootTableEntry;

public class JsonLootTableRepository implements IRepository<ISoliniaLootTable> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaLootTable> loottables = new ConcurrentHashMap<Integer, ISoliniaLootTable>();

	@Override
	public void add(ISoliniaLootTable item) {
		this.loottables.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaLootTable> items) {
		for(ISoliniaLootTable i : items)
		{
			this.loottables.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaLootTable item) {
		this.loottables.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaLootTable item) {
		this.loottables.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaLootTable> filter) {
		for(ISoliniaLootTable i : loottables.values().stream().filter(filter).collect(Collectors.toList()))
		{
			loottables.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaLootTable> query(Predicate<ISoliniaLootTable> filter) {
		return loottables.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaLootTable> file = new ArrayList<ISoliniaLootTable>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootTableEntryTypeAdapterFactory(SoliniaLootTableEntry.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaLootTable>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		loottables.clear();
		for(ISoliniaLootTable i : file)
		{
			loottables.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + loottables.size() + " loottables");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootTableEntryTypeAdapterFactory(SoliniaLootTableEntry.class));
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(loottables.values(), new TypeToken<List<SoliniaLootTable>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + loottables.size() + " loottables");
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
	public ISoliniaLootTable getByKey(Object key) {
		return this.loottables.get(key);
	}
}
