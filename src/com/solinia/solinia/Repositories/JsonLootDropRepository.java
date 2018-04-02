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
import com.solinia.solinia.Factories.ISoliniaAARankTypeAdapterFactory;
import com.solinia.solinia.Factories.ISoliniaLootDropEntryTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaLootDrop;
import com.solinia.solinia.Models.SoliniaLootDropEntry;

public class JsonLootDropRepository implements IRepository<ISoliniaLootDrop> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaLootDrop> lootdrops = new ConcurrentHashMap<Integer, ISoliniaLootDrop>();
	
	@Override
	public void add(ISoliniaLootDrop item) {
		this.lootdrops.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaLootDrop> items) {
		for(ISoliniaLootDrop i : items)
		{
			this.lootdrops.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaLootDrop item) {
		this.lootdrops.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaLootDrop item) {
		this.lootdrops.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaLootDrop> filter) {
		for(ISoliniaLootDrop i : lootdrops.values().stream().filter(filter).collect(Collectors.toList()))
		{
			lootdrops.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaLootDrop> query(Predicate<ISoliniaLootDrop> filter) {
		return lootdrops.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaLootDrop> file = new ArrayList<ISoliniaLootDrop>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootDropEntryTypeAdapterFactory(SoliniaLootDropEntry.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaLootDrop>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		lootdrops.clear();
		for(ISoliniaLootDrop i : file)
		{
			lootdrops.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + lootdrops.size() + " lootdrops");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootDropEntryTypeAdapterFactory(SoliniaLootDropEntry.class));
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(lootdrops.values(), new TypeToken<List<SoliniaLootDrop>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + lootdrops.size() + " lootdrops");
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
	public ISoliniaLootDrop getByKey(Object key) {
		return this.lootdrops.get(key);
	}
}
