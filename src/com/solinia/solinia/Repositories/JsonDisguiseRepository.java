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
import com.solinia.solinia.Models.SoliniaDisguise;

public class JsonDisguiseRepository implements IRepository<SoliniaDisguise> {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaDisguise> Disguises = new ConcurrentHashMap<Integer, SoliniaDisguise>();

	@Override
	public void add(SoliniaDisguise item) {
		this.Disguises.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<SoliniaDisguise> items) {
		for(SoliniaDisguise i : items)
		{
			this.Disguises.put(i.getId(), i);
		}
	}

	@Override
	public void update(SoliniaDisguise item) {
		this.Disguises.put(item.getId(), item);
	}

	@Override
	public void remove(SoliniaDisguise item) {
		this.Disguises.remove(item.getId());
	}

	@Override
	public void remove(Predicate<SoliniaDisguise> filter) {
		for(SoliniaDisguise i : Disguises.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Disguises.remove(i.getId());
		}
	}

	@Override
	public List<SoliniaDisguise> query(Predicate<SoliniaDisguise> filter) {
		return Disguises.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<SoliniaDisguise> file = new ArrayList<SoliniaDisguise>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaDisguise>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Disguises.clear();
		for(SoliniaDisguise i : file)
		{
			Disguises.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Disguises.size() + " Disguises");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Disguises.values(), new TypeToken<List<SoliniaDisguise>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Disguises.size() + " Disguises");
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
	public SoliniaDisguise getByKey(Object key) {
		return this.Disguises.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}

}
