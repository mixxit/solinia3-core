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
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Models.SoliniaLootTableEntry;

public class JsonLootTableEntryRepository implements IRepository<ISoliniaLootTableEntry> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaLootTableEntry> loottableentrys = new ConcurrentHashMap<Integer, ISoliniaLootTableEntry>();

	@Override
	public void add(ISoliniaLootTableEntry item) {
		this.loottableentrys.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaLootTableEntry> items) {
		for(ISoliniaLootTableEntry i : items)
		{
			this.loottableentrys.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaLootTableEntry item) {
		this.loottableentrys.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaLootTableEntry item) {
		this.loottableentrys.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaLootTableEntry> filter) {
		for(ISoliniaLootTableEntry i : loottableentrys.values().stream().filter(filter).collect(Collectors.toList()))
		{
			loottableentrys.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaLootTableEntry> query(Predicate<ISoliniaLootTableEntry> filter) {
		return loottableentrys.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaLootTableEntry> file = new ArrayList<ISoliniaLootTableEntry>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaLootTableEntry>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		loottableentrys.clear();
		for(ISoliniaLootTableEntry i : file)
		{
			loottableentrys.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + loottableentrys.size() + " loottableentrys");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(loottableentrys.values(), new TypeToken<List<SoliniaLootTableEntry>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + loottableentrys.size() + " loottableentrys");
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

}
