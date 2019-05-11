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
import com.solinia.solinia.Models.SoliniaCraft;

public class JsonCraftRepository implements IRepository<SoliniaCraft> {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaCraft> Crafts = new ConcurrentHashMap<Integer, SoliniaCraft>();

	@Override
	public void add(SoliniaCraft item) {
		this.Crafts.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<SoliniaCraft> items) {
		for(SoliniaCraft i : items)
		{
			this.Crafts.put(i.getId(), i);
		}
	}

	@Override
	public void update(SoliniaCraft item) {
		this.Crafts.put(item.getId(), item);
	}

	@Override
	public void remove(SoliniaCraft item) {
		this.Crafts.remove(item.getId());
	}

	@Override
	public void remove(Predicate<SoliniaCraft> filter) {
		for(SoliniaCraft i : Crafts.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Crafts.remove(i.getId());
		}
	}

	@Override
	public List<SoliniaCraft> query(Predicate<SoliniaCraft> filter) {
		return Crafts.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<SoliniaCraft> file = new ArrayList<SoliniaCraft>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaCraft>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Crafts.clear();
		for(SoliniaCraft i : file)
		{
			Crafts.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Crafts.size() + " Crafts");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Crafts.values(), new TypeToken<List<SoliniaCraft>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Crafts.size() + " Crafts");
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
	public SoliniaCraft getByKey(Object key) {
		return this.Crafts.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}

}
