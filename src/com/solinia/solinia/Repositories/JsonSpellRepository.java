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
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaSpell;

public class JsonSpellRepository implements IRepository<ISoliniaSpell> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaSpell> spells = new ConcurrentHashMap<Integer, ISoliniaSpell>();
	private Timestamp lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());

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
		List<ISoliniaSpell> readObj = new ArrayList<ISoliniaSpell>();
		Gson gson = new Gson();
		
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
			        readObj.add(gson.fromJson(br, new TypeToken<SoliniaSpell>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		spells.clear();
		for(ISoliniaSpell i : readObj)
		{
			spells.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + spells.size() + " spells");
	}	
	
	@Override
	public void commit() {
		// uses new method of storing each file seperately, reducing cost of saving item data
		// to each file only
		// jq -cr '.[] | .id, .' spells.json | awk 'NR%2{f=$0".json";next} {print >f;close(f)}'
		
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		
		int commitCount = 0;
		for(Integer id : spells.keySet())
		{
			
			if (spells.get(id).getLastUpdatedTime() == null
					||
					spells.get(id).getLastUpdatedTime().after(this.lastCommitedTimestamp)
					)
			{
				try
				{
					String jsonOutput = gson.toJson(spells.get(id), new TypeToken<SoliniaSpell>(){}.getType());
					File file = new File(filePath+"/"+id+".json");
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
		
		System.out.println("Commited " + commitCount + " changed spells out of " + spells.size());
		
		this.lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public ISoliniaSpell getByKey(Object key) {
		return this.spells.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}

}
