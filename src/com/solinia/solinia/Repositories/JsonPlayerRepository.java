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
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaPlayer;

public class JsonPlayerRepository implements IRepository<ISoliniaPlayer>  {
	private ConcurrentHashMap<UUID, ISoliniaPlayer> players = new ConcurrentHashMap<UUID, ISoliniaPlayer>();
	private String filePath;
	
	@Override
	public void reload() {
		List<ISoliniaPlayer> filePlayers = new ArrayList<ISoliniaPlayer>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			filePlayers = gson.fromJson(br, new TypeToken<List<SoliniaPlayer>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		players.clear();
		for(ISoliniaPlayer player : filePlayers)
		{
			players.put(player.getUUID(), player);
		}
		
		System.out.println("Reloaded " + players.size() + " players");
	}
	

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(players.values(), new TypeToken<List<SoliniaPlayer>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + players.size() + " players");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void add(ISoliniaPlayer item) {
		players.put(item.getUUID(), item);
		
	}

	@Override
	public void add(Iterable<ISoliniaPlayer> items) {
		for(ISoliniaPlayer player : items)
		{
			this.players.put(player.getUUID(), player);
		}
	}

	@Override
	public void update(ISoliniaPlayer item) {
		this.players.put(item.getUUID(), item);
	}

	@Override
	public void remove(ISoliniaPlayer item) {
		this.players.remove(item.getUUID());		
	}

	@Override
	public void remove(Predicate<ISoliniaPlayer> filter) {
		for(ISoliniaPlayer player : players.values().stream().filter(filter).collect(Collectors.toList()))
		{
			players.remove(player.getUUID());
		}
	}

	@Override
	public List<ISoliniaPlayer> query(Predicate<ISoliniaPlayer> filter) {
		return players.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public void setJsonFile(String jsonFile)
	{
		this.filePath = jsonFile;
	}
	
	@Override
	public ISoliniaPlayer getByKey(Object key) {
		return this.players.get(key);
	}


	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
