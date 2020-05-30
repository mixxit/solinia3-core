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
import com.solinia.solinia.Models.SoliniaAccountClaim;

public class JsonAccountClaimRepository implements IRepository<SoliniaAccountClaim> {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaAccountClaim> AccountClaims = new ConcurrentHashMap<Integer, SoliniaAccountClaim>();
	private Timestamp lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());

	@Override
	public void add(SoliniaAccountClaim item) {
		this.AccountClaims.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<SoliniaAccountClaim> items) {
		for(SoliniaAccountClaim i : items)
		{
			this.AccountClaims.put(i.getId(), i);
		}
	}

	@Override
	public void update(SoliniaAccountClaim item) {
		this.AccountClaims.put(item.getId(), item);
	}

	@Override
	public void remove(SoliniaAccountClaim item) {
		this.AccountClaims.remove(item.getId());
	}

	@Override
	public void remove(Predicate<SoliniaAccountClaim> filter) {
		for(SoliniaAccountClaim i : AccountClaims.values().stream().filter(filter).collect(Collectors.toList()))
		{
			AccountClaims.remove(i.getId());
		}
	}

	@Override
	public List<SoliniaAccountClaim> query(Predicate<SoliniaAccountClaim> filter) {
		return AccountClaims.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<SoliniaAccountClaim> readItems = new ArrayList<SoliniaAccountClaim>();
		
		Gson gson = new Gson();

		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
					readItems.add(gson.fromJson(br, new TypeToken<SoliniaAccountClaim>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		AccountClaims.clear();
		for(SoliniaAccountClaim i : readItems)
		{
			AccountClaims.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + AccountClaims.size() + " AccountClaims");
	}	
	
	@Override
	public void commit() {
		// uses new method of storing each file seperately, reducing cost of saving item
		// data
		// to each file only
		// jq -cr '.[] | .id, .' items.json | awk 'NR%2{f=$0".json";next} {print
		// >f;close(f)}'

		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		// gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();

		int commitCount = 0;
		for (Integer itemId : AccountClaims.keySet()) {

			if (AccountClaims.get(itemId).getLastUpdatedTime() == null
					|| AccountClaims.get(itemId).getLastUpdatedTime().after(this.lastCommitedTimestamp)) {
				try {
					String jsonOutput = gson.toJson(AccountClaims.get(itemId), new TypeToken<SoliniaAccountClaim>() {
					}.getType());
					File file = new File(filePath + "/" + itemId + ".json");
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

		System.out.println("Commited " + commitCount + " changed items out of " + AccountClaims.size());

		this.lastCommitedTimestamp = Timestamp.valueOf(LocalDateTime.now());
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public SoliniaAccountClaim getByKey(Object key) {
		return this.AccountClaims.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
