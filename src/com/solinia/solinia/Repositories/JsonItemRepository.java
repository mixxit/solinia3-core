package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Models.SoliniaItem;

public class JsonItemRepository implements IRepository<ISoliniaItem> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaItem> items = new ConcurrentHashMap<Integer, ISoliniaItem>();
	private Timestamp lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());

	
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
		List<ISoliniaItem> readItems = new ArrayList<ISoliniaItem>();
		
		Gson gson = new Gson();

		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
					readItems.add(gson.fromJson(br, new TypeToken<SoliniaItem>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		items.clear();
		for(ISoliniaItem i : readItems)
		{
			items.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + items.size() + " items");
	}	
	
	@Override
	public void commit() {
		// uses new method of storing each file seperately, reducing cost of saving item data
		// to each file only
		// jq -cr '.[] | .id, .' items.json | awk 'NR%2{f=$0".json";next} {print >f;close(f)}'
		
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		
		int commitCount = 0;
		for(Integer itemId : items.keySet())
		{
			
			if (items.get(itemId).getLastUpdatedTime() == null
					||
				items.get(itemId).getLastUpdatedTime().after(this.lastCommitedTimestamp)
					)
			{
				try
				{
					String jsonOutput = gson.toJson(items.get(itemId), new TypeToken<SoliniaItem>(){}.getType());
					File file = new File(filePath+"/"+itemId+".json");
					if (!file.exists())
						file.createNewFile();
			        
					FileOutputStream fileOut = new FileOutputStream(file);
					OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
			        outWriter.append(jsonOutput);
			        outWriter.close();
			        fileOut.close();
			        commitCount++;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Commited " + commitCount + " changed items out of " + items.size());
		
		this.lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public ISoliniaItem getByKey(Object key) {
		return this.items.get(key);
	}
	
	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
