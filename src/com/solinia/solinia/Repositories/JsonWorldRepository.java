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
import com.solinia.solinia.Models.SoliniaWorld;

public class JsonWorldRepository implements IRepository<SoliniaWorld>  {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaWorld> Worlds = new ConcurrentHashMap<Integer, SoliniaWorld>();

	@Override
	public void add(SoliniaWorld item) {
		this.Worlds.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<SoliniaWorld> items) {
		for(SoliniaWorld i : items)
		{
			this.Worlds.put(i.getId(), i);
		}
	}

	@Override
	public void update(SoliniaWorld item) {
		this.Worlds.put(item.getId(), item);
	}

	@Override
	public void remove(SoliniaWorld item) {
		this.Worlds.remove(item.getId());
	}

	@Override
	public void remove(Predicate<SoliniaWorld> filter) {
		for(SoliniaWorld i : Worlds.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Worlds.remove(i.getId());
		}
	}

	@Override
	public List<SoliniaWorld> query(Predicate<SoliniaWorld> filter) {
		return Worlds.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<SoliniaWorld> file = new ArrayList<SoliniaWorld>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaWorld>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Worlds.clear();
		for(SoliniaWorld i : file)
		{
			Worlds.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Worlds.size() + " Worlds");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Worlds.values(), new TypeToken<List<SoliniaWorld>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Worlds.size() + " Worlds");
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
	public SoliniaWorld getByKey(Object key) {
		return this.Worlds.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
