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
import com.solinia.solinia.Factories.ISoliniaSpawnGroupTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaSpawnGroup;
import com.solinia.solinia.Models.SoliniaSpawnGroup;

public class JsonSpawnGroupRepository implements IRepository<ISoliniaSpawnGroup> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaSpawnGroup> spawngroups = new ConcurrentHashMap<Integer, ISoliniaSpawnGroup>();

	@Override
	public void add(ISoliniaSpawnGroup item) {
		this.spawngroups.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaSpawnGroup> items) {
		for(ISoliniaSpawnGroup i : items)
		{
			this.spawngroups.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaSpawnGroup item) {
		this.spawngroups.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaSpawnGroup item) {
		this.spawngroups.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaSpawnGroup> filter) {
		for(ISoliniaSpawnGroup i : spawngroups.values().stream().filter(filter).collect(Collectors.toList()))
		{
			spawngroups.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaSpawnGroup> query(Predicate<ISoliniaSpawnGroup> filter) {
		return spawngroups.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaSpawnGroup> file = new ArrayList<ISoliniaSpawnGroup>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaSpawnGroupTypeAdapterFactory(SoliniaSpawnGroup.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaSpawnGroup>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		spawngroups.clear();
		for(ISoliniaSpawnGroup i : file)
		{
			spawngroups.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + spawngroups.size() + " spawngroups");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaSpawnGroupTypeAdapterFactory(SoliniaSpawnGroup.class));
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(spawngroups.values(), new TypeToken<List<SoliniaSpawnGroup>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + spawngroups.size() + " spawngroups");
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
	public ISoliniaSpawnGroup getByKey(Object key) {
		return this.spawngroups.get(key);
	}

}
