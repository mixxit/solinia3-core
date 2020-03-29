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
import com.solinia.solinia.Models.Fellowship;

public class JsonFellowshipRepository implements IRepository<Fellowship> {
	private String filePath;
	private ConcurrentHashMap<Integer, Fellowship> Fellowships = new ConcurrentHashMap<Integer, Fellowship>();

	@Override
	public void add(Fellowship item) {
		this.Fellowships.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<Fellowship> items) {
		for(Fellowship i : items)
		{
			this.Fellowships.put(i.getId(), i);
		}
	}

	@Override
	public void update(Fellowship item) {
		this.Fellowships.put(item.getId(), item);
	}

	@Override
	public void remove(Fellowship item) {
		this.Fellowships.remove(item.getId());
	}

	@Override
	public void remove(Predicate<Fellowship> filter) {
		for(Fellowship i : Fellowships.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Fellowships.remove(i.getId());
		}
	}

	@Override
	public List<Fellowship> query(Predicate<Fellowship> filter) {
		return Fellowships.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<Fellowship> file = new ArrayList<Fellowship>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<Fellowship>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Fellowships.clear();
		for(Fellowship i : file)
		{
			Fellowships.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Fellowships.size() + " Fellowships");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Fellowships.values(), new TypeToken<List<Fellowship>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Fellowships.size() + " Fellowships");
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
	public Fellowship getByKey(Object key) {
		return this.Fellowships.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}

}
