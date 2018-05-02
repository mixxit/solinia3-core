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
import com.solinia.solinia.Models.SoliniaZone;

public class JsonZoneRepository implements IRepository<SoliniaZone> {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaZone> Zones = new ConcurrentHashMap<Integer, SoliniaZone>();

	@Override
	public void add(SoliniaZone item) {
		this.Zones.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<SoliniaZone> items) {
		for(SoliniaZone i : items)
		{
			this.Zones.put(i.getId(), i);
		}
	}

	@Override
	public void update(SoliniaZone item) {
		this.Zones.put(item.getId(), item);
	}

	@Override
	public void remove(SoliniaZone item) {
		this.Zones.remove(item.getId());
	}

	@Override
	public void remove(Predicate<SoliniaZone> filter) {
		for(SoliniaZone i : Zones.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Zones.remove(i.getId());
		}
	}

	@Override
	public List<SoliniaZone> query(Predicate<SoliniaZone> filter) {
		return Zones.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<SoliniaZone> file = new ArrayList<SoliniaZone>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaZone>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Zones.clear();
		for(SoliniaZone i : file)
		{
			Zones.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Zones.size() + " Zones");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Zones.values(), new TypeToken<List<SoliniaZone>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Zones.size() + " Zones");
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
	public SoliniaZone getByKey(Object key) {
		return this.Zones.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
