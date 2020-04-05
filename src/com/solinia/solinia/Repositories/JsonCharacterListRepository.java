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
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaPlayer;

public class JsonCharacterListRepository implements IRepository<ISoliniaPlayer>  {
	private ConcurrentHashMap<Integer, ISoliniaPlayer> CharacterLists = new ConcurrentHashMap<Integer, ISoliniaPlayer>();
	private String filePath;
	
	@Override
	public void reload() {
		List<ISoliniaPlayer> fileCharacterLists = new ArrayList<ISoliniaPlayer>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			fileCharacterLists = gson.fromJson(br, new TypeToken<List<SoliniaPlayer>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		CharacterLists.clear();
		for(ISoliniaPlayer CharacterList : fileCharacterLists)
		{
			CharacterLists.put(CharacterList.getId(), CharacterList);
		}
		
		System.out.println("Reloaded " + CharacterLists.size() + " CharacterLists");
	}
	

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(CharacterLists.values(), new TypeToken<List<SoliniaPlayer>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + CharacterLists.size() + " CharacterLists");
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
		CharacterLists.put(item.getId(), item);
		
	}

	@Override
	public void add(Iterable<ISoliniaPlayer> items) {
		for(ISoliniaPlayer CharacterList : items)
		{
			this.CharacterLists.put(CharacterList.getId(), CharacterList);
		}
	}

	@Override
	public void update(ISoliniaPlayer item) {
		this.CharacterLists.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaPlayer item) {
		this.CharacterLists.remove(item.getId());		
	}

	@Override
	public void remove(Predicate<ISoliniaPlayer> filter) {
		for(ISoliniaPlayer CharacterList : CharacterLists.values().stream().filter(filter).collect(Collectors.toList()))
		{
			CharacterLists.remove(CharacterList.getId());
		}
	}

	@Override
	public List<ISoliniaPlayer> query(Predicate<ISoliniaPlayer> filter) {
		return CharacterLists.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public void setJsonFile(String jsonFile)
	{
		this.filePath = jsonFile;
	}
	
	@Override
	public ISoliniaPlayer getByKey(Object key) {
		return this.CharacterLists.get(key);
	}


	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
