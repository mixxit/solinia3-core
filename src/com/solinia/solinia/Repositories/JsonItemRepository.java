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
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaItem;

public class JsonItemRepository implements IRepository<ISoliniaItem> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaItem> items = new ConcurrentHashMap<Integer, ISoliniaItem>();

	
	@Override
	public void add(ISoliniaItem item) {
		this.items.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaItem> items) {
		for(ISoliniaItem i : items)
		{
			this.items.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaItem item) {
		this.items.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaItem item) {
		this.items.remove(item.getId());
	}
	
	@Override
	public void remove(Predicate<ISoliniaItem> filter) {
		for(ISoliniaItem i : items.values().stream().filter(filter).collect(Collectors.toList()))
		{
			items.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaItem> query(Predicate<ISoliniaItem> filter) {
		return items.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaItem> file = new ArrayList<ISoliniaItem>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaItem>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		items.clear();
		for(ISoliniaItem i : file)
		{
			items.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + items.size() + " items");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(items.values(), new TypeToken<List<SoliniaItem>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + items.size() + " items");
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
	public ISoliniaItem getByKey(Object key) {
		return this.items.get(key);
	}
}
