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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Models.PlayerState;

public class JsonPlayerStateRepository implements IRepository<PlayerState>  {
	private String filePath;
	private ConcurrentHashMap<UUID, PlayerState> PlayerStates = new ConcurrentHashMap<UUID, PlayerState>();

	@Override
	public void add(PlayerState item) {
		this.PlayerStates.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<PlayerState> items) {
		for(PlayerState i : items)
		{
			this.PlayerStates.put(i.getId(), i);
		}
	}

	@Override
	public void update(PlayerState item) {
		this.PlayerStates.put(item.getId(), item);
	}

	@Override
	public void remove(PlayerState item) {
		this.PlayerStates.remove(item.getId());
	}

	@Override
	public void remove(Predicate<PlayerState> filter) {
		for(PlayerState i : PlayerStates.values().stream().filter(filter).collect(Collectors.toList()))
		{
			PlayerStates.remove(i.getId());
		}
	}

	@Override
	public List<PlayerState> query(Predicate<PlayerState> filter) {
		return PlayerStates.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<PlayerState> file = new ArrayList<PlayerState>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<PlayerState>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		PlayerStates.clear();
		for(PlayerState i : file)
		{
			PlayerStates.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + PlayerStates.size() + " PlayerStates");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(PlayerStates.values(), new TypeToken<List<PlayerState>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + PlayerStates.size() + " PlayerStates");
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
	public PlayerState getByKey(Object key) {
		return this.PlayerStates.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
