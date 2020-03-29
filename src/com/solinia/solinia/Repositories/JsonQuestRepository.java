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
import com.solinia.solinia.Interfaces.ISoliniaQuest;
import com.solinia.solinia.Models.SoliniaQuest;

public class JsonQuestRepository implements IRepository<ISoliniaQuest> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaQuest> Quests = new ConcurrentHashMap<Integer, ISoliniaQuest>();

	@Override
	public void add(ISoliniaQuest item) {
		this.Quests.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaQuest> items) {
		for(ISoliniaQuest i : items)
		{
			this.Quests.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaQuest item) {
		this.Quests.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaQuest item) {
		this.Quests.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaQuest> filter) {
		for(ISoliniaQuest i : Quests.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Quests.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaQuest> query(Predicate<ISoliniaQuest> filter) {
		return Quests.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaQuest> file = new ArrayList<ISoliniaQuest>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaQuest>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Quests.clear();
		for(ISoliniaQuest i : file)
		{
			Quests.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Quests.size() + " Quests");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Quests.values(), new TypeToken<List<SoliniaQuest>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Quests.size() + " Quests");
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
	public ISoliniaQuest getByKey(Object key) {
		return this.Quests.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
