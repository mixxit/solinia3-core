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
import com.solinia.solinia.Models.NPCSpellList;

public class JsonNPCSpellListRepository implements IRepository<NPCSpellList> {
	private ConcurrentHashMap<Integer, NPCSpellList> npcspelllists = new ConcurrentHashMap<Integer, NPCSpellList>();
	private String filePath;
	
	@Override
	public void reload() {
		List<NPCSpellList> file = new ArrayList<NPCSpellList>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<NPCSpellList>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		npcspelllists.clear();
		for(NPCSpellList npcspelllist : file)
		{
			npcspelllists.put(npcspelllist.getId(), npcspelllist);
		}
		
		System.out.println("Reloaded " + npcspelllists.size() + " npcspelllists");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(npcspelllists.values(), new TypeToken<List<NPCSpellList>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + npcspelllists.size() + " npcspelllists");
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
	public void add(NPCSpellList item) {
		npcspelllists.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<NPCSpellList> items) {
		for(NPCSpellList i : items)
		{
			this.npcspelllists.put(i.getId(), i);
		}
	}

	@Override
	public void update(NPCSpellList item) {
		this.npcspelllists.put(item.getId(), item);
	}

	@Override
	public void remove(NPCSpellList item) {
		this.npcspelllists.remove(item.getId());
	}

	@Override
	public void remove(Predicate<NPCSpellList> filter) {
		for(NPCSpellList npcspelllist : npcspelllists.values().stream().filter(filter).collect(Collectors.toList()))
		{
			npcspelllists.remove(npcspelllist.getId());
		}
	}

	@Override
	public List<NPCSpellList> query(Predicate<NPCSpellList> filter) {
		return npcspelllists.values().stream().filter(filter).collect(Collectors.toList());
	}

}
