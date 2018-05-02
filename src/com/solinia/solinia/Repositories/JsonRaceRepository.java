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
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Models.SoliniaRace;

public class JsonRaceRepository implements IRepository<ISoliniaRace> {
	private ConcurrentHashMap<Integer, ISoliniaRace> races = new ConcurrentHashMap<Integer, ISoliniaRace>();
	private String filePath;
	
	@Override
	public void reload() {
		List<ISoliniaRace> file = new ArrayList<ISoliniaRace>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaRace>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		races.clear();
		for(ISoliniaRace race : file)
		{
			races.put(race.getId(), race);
		}
		
		System.out.println("Reloaded " + races.size() + " races");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(races.values(), new TypeToken<List<SoliniaRace>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + races.size() + " races");
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
	public void add(ISoliniaRace item) {
		races.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaRace> items) {
		for(ISoliniaRace i : items)
		{
			this.races.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaRace item) {
		this.races.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaRace item) {
		this.races.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaRace> filter) {
		for(ISoliniaRace race : races.values().stream().filter(filter).collect(Collectors.toList()))
		{
			races.remove(race.getId());
		}
	}

	@Override
	public List<ISoliniaRace> query(Predicate<ISoliniaRace> filter) {
		return races.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public ISoliniaRace getByKey(Object key) {
		return this.races.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
