package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import com.solinia.solinia.Factories.ISoliniaLootDropEntryTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Models.SoliniaLootDrop;
import com.solinia.solinia.Models.SoliniaLootDropEntry;

public class JsonLootDropRepository implements IRepository<ISoliniaLootDrop> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaLootDrop> lootdrops = new ConcurrentHashMap<Integer, ISoliniaLootDrop>();
	private Timestamp lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	
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
		List<ISoliniaLootDrop> readObj = new ArrayList<ISoliniaLootDrop>();
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootDropEntryTypeAdapterFactory(SoliniaLootDropEntry.class));
		Gson gson = gsonbuilder.create();
		
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
			        readObj.add(gson.fromJson(br, new TypeToken<SoliniaLootDrop>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		lootdrops.clear();
		for(ISoliniaLootDrop i : readObj)
		{
			lootdrops.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + lootdrops.size() + " lootdrops");
	}	
	
	@Override
	public void commit() {
		// uses new method of storing each file seperately, reducing cost of saving item
		// data
		// to each file only
		// jq -cr '.[] | .id, .' lootdrops.json | awk 'NR%2{f=$0".json";next}
		// {print >f;close(f)}'
		
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootDropEntryTypeAdapterFactory(SoliniaLootDropEntry.class));
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		
		int commitCount = 0;
		for (Integer id : lootdrops.keySet()) {

			if (lootdrops.get(id).getLastUpdatedTime() == null
					|| lootdrops.get(id).getLastUpdatedTime().after(this.lastCommitedTimestamp)) {
				try {
					String jsonOutput = gson.toJson(lootdrops.get(id), new TypeToken<SoliniaLootDrop>() {
					}.getType());
					File file = new File(filePath + "/" + id + ".json");
					if (!file.exists())
						file.createNewFile();

					FileOutputStream fileOut = new FileOutputStream(file);
					OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
					outWriter.append(jsonOutput);
					outWriter.close();
					fileOut.close();
					commitCount++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Commited " + commitCount + " changed lootdrops out of " + lootdrops.size());
		this.lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}

	@Override
	public ISoliniaLootDrop getByKey(Object key) {
		return this.lootdrops.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
