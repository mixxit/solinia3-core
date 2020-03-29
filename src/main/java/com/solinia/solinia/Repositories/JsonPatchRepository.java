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
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Models.SoliniaPatch;

public class JsonPatchRepository implements IRepository<ISoliniaPatch> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaPatch> patches = new ConcurrentHashMap<Integer, ISoliniaPatch>();

	@Override
	public void add(ISoliniaPatch item) {
		this.patches.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaPatch> items) {
		for(ISoliniaPatch i : items)
		{
			this.patches.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaPatch item) {
		this.patches.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaPatch item) {
		this.patches.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaPatch> filter) {
		for(ISoliniaPatch i : patches.values().stream().filter(filter).collect(Collectors.toList()))
		{
			patches.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaPatch> query(Predicate<ISoliniaPatch> filter) {
		return patches.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaPatch> file = new ArrayList<ISoliniaPatch>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaPatch>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		patches.clear();
		for(ISoliniaPatch i : file)
		{
			patches.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + patches.size() + " Patchs");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(patches.values(), new TypeToken<List<SoliniaPatch>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + patches.size() + " Patchs");
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
	public ISoliniaPatch getByKey(Object key) {
		return this.patches.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
