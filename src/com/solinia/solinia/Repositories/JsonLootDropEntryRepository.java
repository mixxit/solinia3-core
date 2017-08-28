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
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Models.SoliniaLootDropEntry;

public class JsonLootDropEntryRepository implements IRepository<ISoliniaLootDropEntry> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaLootDropEntry> lootdropentrys = new ConcurrentHashMap<Integer, ISoliniaLootDropEntry>();

	@Override
	public void add(ISoliniaLootDropEntry item) {
		this.lootdropentrys.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaLootDropEntry> items) {
		for(ISoliniaLootDropEntry i : items)
		{
			this.lootdropentrys.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaLootDropEntry item) {
		this.lootdropentrys.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaLootDropEntry item) {
		this.lootdropentrys.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaLootDropEntry> filter) {
		for(ISoliniaLootDropEntry i : lootdropentrys.values().stream().filter(filter).collect(Collectors.toList()))
		{
			lootdropentrys.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaLootDropEntry> query(Predicate<ISoliniaLootDropEntry> filter) {
		return lootdropentrys.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaLootDropEntry> file = new ArrayList<ISoliniaLootDropEntry>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaLootDropEntry>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		lootdropentrys.clear();
		for(ISoliniaLootDropEntry i : file)
		{
			lootdropentrys.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + lootdropentrys.size() + " lootdropentrys");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(lootdropentrys.values(), new TypeToken<List<SoliniaLootDropEntry>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + lootdropentrys.size() + " lootdropentrys");
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
