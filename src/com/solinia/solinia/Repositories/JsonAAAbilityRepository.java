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
import com.solinia.solinia.Factories.ISoliniaAAEffectTypeAdapterFactory;
import com.solinia.solinia.Factories.ISoliniaAARankTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Models.SoliniaAAAbility;
import com.solinia.solinia.Models.SoliniaAAEffect;
import com.solinia.solinia.Models.SoliniaAARank;

public class JsonAAAbilityRepository implements IRepository<ISoliniaAAAbility> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaAAAbility> aaabilities = new ConcurrentHashMap<Integer, ISoliniaAAAbility>();

	@Override
	public void add(ISoliniaAAAbility item) {
		this.aaabilities.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaAAAbility> items) {
		for(ISoliniaAAAbility i : items)
		{
			this.aaabilities.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaAAAbility item) {
		this.aaabilities.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaAAAbility item) {
		this.aaabilities.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaAAAbility> filter) {
		for(ISoliniaAAAbility i : aaabilities.values().stream().filter(filter).collect(Collectors.toList()))
		{
			aaabilities.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaAAAbility> query(Predicate<ISoliniaAAAbility> filter) {
		return aaabilities.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaAAAbility> file = new ArrayList<ISoliniaAAAbility>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaAARankTypeAdapterFactory(SoliniaAARank.class));
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaAAEffectTypeAdapterFactory(SoliniaAAEffect.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaAAAbility>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		aaabilities.clear();
		for(ISoliniaAAAbility i : file)
		{
			aaabilities.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + aaabilities.size() + " AA abilities");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaAARankTypeAdapterFactory(SoliniaAARank.class));
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaAAEffectTypeAdapterFactory(SoliniaAAEffect.class));
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(aaabilities.values(), new TypeToken<List<SoliniaAAAbility>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + aaabilities.size() + " AA abilities");
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
	public ISoliniaAAAbility getByKey(Object key) {
		return this.aaabilities.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}

}
