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
import com.solinia.solinia.Models.SoliniaAccountClaim;

public class JsonAccountClaimRepository implements IRepository<SoliniaAccountClaim> {
	private String filePath;
	private ConcurrentHashMap<Integer, SoliniaAccountClaim> AccountClaims = new ConcurrentHashMap<Integer, SoliniaAccountClaim>();

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
		List<SoliniaAccountClaim> file = new ArrayList<SoliniaAccountClaim>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaAccountClaim>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		AccountClaims.clear();
		for(SoliniaAccountClaim i : file)
		{
			AccountClaims.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + AccountClaims.size() + " AccountClaims");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(AccountClaims.values(), new TypeToken<List<SoliniaAccountClaim>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + AccountClaims.size() + " AccountClaims");
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
	public SoliniaAccountClaim getByKey(Object key) {
		return this.AccountClaims.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
