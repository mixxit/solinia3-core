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
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Models.SoliniaFaction;

public class JsonFactionRepository implements IRepository<ISoliniaFaction> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaFaction> Factions = new ConcurrentHashMap<Integer, ISoliniaFaction>();

	@Override
	public void add(ISoliniaFaction item) {
		this.Factions.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaFaction> items) {
		for(ISoliniaFaction i : items)
		{
			this.Factions.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaFaction item) {
		this.Factions.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaFaction item) {
		this.Factions.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaFaction> filter) {
		for(ISoliniaFaction i : Factions.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Factions.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaFaction> query(Predicate<ISoliniaFaction> filter) {
		return Factions.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaFaction> file = new ArrayList<ISoliniaFaction>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaFaction>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Factions.clear();
		for(ISoliniaFaction i : file)
		{
			Factions.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Factions.size() + " Factions");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Factions.values(), new TypeToken<List<SoliniaFaction>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Factions.size() + " Factions");
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
	public ISoliniaFaction getByKey(Object key) {
		return this.Factions.get(key);
	}
}
