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
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaSpell;

public class JsonSpellRepository implements IRepository<ISoliniaSpell> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaSpell> spells = new ConcurrentHashMap<Integer, ISoliniaSpell>();

	@Override
	public void add(ISoliniaSpell item) {
		this.spells.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaSpell> items) {
		for(ISoliniaSpell i : items)
		{
			this.spells.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaSpell item) {
		this.spells.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaSpell item) {
		this.spells.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaSpell> filter) {
		for(ISoliniaSpell i : spells.values().stream().filter(filter).collect(Collectors.toList()))
		{
			spells.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaSpell> query(Predicate<ISoliniaSpell> filter) {
		return spells.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaSpell> file = new ArrayList<ISoliniaSpell>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaSpell>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		spells.clear();
		for(ISoliniaSpell i : file)
		{
			spells.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + spells.size() + " spells");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(spells.values(), new TypeToken<List<SoliniaSpell>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + spells.size() + " spells");
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
	public ISoliniaSpell getByKey(Object key) {
		return this.spells.get(key);
	}

}
