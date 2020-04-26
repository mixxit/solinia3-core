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
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.SoliniaPlayer;

public class JsonCharacterListRepository implements IRepository<ISoliniaPlayer>  {
	private ConcurrentHashMap<Integer, ISoliniaPlayer> CharacterLists = new ConcurrentHashMap<Integer, ISoliniaPlayer>();
	private String filePath;
	private Timestamp lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());

	@Override
	public void reload() {
		List<ISoliniaPlayer> readObj = new ArrayList<ISoliniaPlayer>();
		Gson gson = new Gson();
		
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
			        readObj.add(gson.fromJson(br, new TypeToken<SoliniaPlayer>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		CharacterLists.clear();
		for(ISoliniaPlayer i : readObj)
		{
			CharacterLists.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + CharacterLists.size() + " characters");
	}
	

	@Override
	public void commit() {
		// uses new method of storing each file seperately, reducing cost of saving item
		// data
		// to each file only
		// jq -cr '.[] | .id, .' characterlists.json | awk 'NR%2{f=$0".json";next}
		// {print >f;close(f)}'

		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		// gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();

		int commitCount = 0;
		for (Integer id : CharacterLists.keySet()) {

			if (CharacterLists.get(id).getLastUpdatedTime() == null
					|| CharacterLists.get(id).getLastUpdatedTime().after(this.lastCommitedTimestamp)) {
				try {
					String jsonOutput = gson.toJson(CharacterLists.get(id), new TypeToken<SoliniaPlayer>() {
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

		System.out.println("Commited " + commitCount + " changed characters out of " + CharacterLists.size());

		this.lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	@Override
	public void add(ISoliniaPlayer item) {
		CharacterLists.put(item.getId(), item);
		
	}

	@Override
	public void add(Iterable<ISoliniaPlayer> items) {
		for(ISoliniaPlayer CharacterList : items)
		{
			this.CharacterLists.put(CharacterList.getId(), CharacterList);
		}
	}

	@Override
	public void update(ISoliniaPlayer item) {
		this.CharacterLists.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaPlayer item) {
		this.CharacterLists.remove(item.getId());		
	}

	@Override
	public void remove(Predicate<ISoliniaPlayer> filter) {
		for(ISoliniaPlayer CharacterList : CharacterLists.values().stream().filter(filter).collect(Collectors.toList()))
		{
			CharacterLists.remove(CharacterList.getId());
		}
	}

	@Override
	public List<ISoliniaPlayer> query(Predicate<ISoliniaPlayer> filter) {
		return CharacterLists.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public void setJsonFile(String jsonFile)
	{
		this.filePath = jsonFile;
	}
	
	@Override
	public ISoliniaPlayer getByKey(Object key) {
		return this.CharacterLists.get(key);
	}


	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
